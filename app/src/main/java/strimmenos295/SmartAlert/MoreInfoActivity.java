package strimmenos295.SmartAlert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class MoreInfoActivity extends AppCompatActivity {
    int danger;
    int times;
    ArrayList<MessageFromUserClass> similar;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase root;
    DatabaseReference MessageFromUser;
    DatabaseReference MessageFromAdmin;
    DatabaseReference Statistics;
    StorageReference photoRef;
    ImageView image;
    EditText comments;
    TextView category, coordinates;
    MessageFromAdminClass m1=new MessageFromAdminClass();
    ArrayList<MessageFromAdminClass>helpList;
    ArrayList<MessageFromUserClass> forfelete;
    ArrayList<StatisticsClass> forstatus;
    int maxiId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);
        danger = getIntent().getIntExtra("dangerLevel",0);
        times = getIntent().getIntExtra("times",0);
        similar = (ArrayList<MessageFromUserClass>) getIntent().getSerializableExtra("listOfSimilarMessages");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        root = FirebaseDatabase.getInstance();
        MessageFromUser = root.getReference("MessageFromUser");
        MessageFromAdmin = root.getReference("MessageFromAdmin");
        Statistics = root.getReference("Statistics");
        image = (ImageView) findViewById(R.id.image);
        comments = findViewById(R.id.editTextTextMultiLine2);
        StringBuilder s1 = new StringBuilder();
        category = findViewById(R.id.category1);
        helpList = new ArrayList<>();
        forfelete = new ArrayList<>();
        forstatus = new ArrayList<>();
        coordinates = findViewById(R.id.textView4);




        if (similar.get(0).getCategory().equals("πυρκαγιά")){
            category.setText(this.getText(R.string.fire)+" "+similar.get(0).getTimestamp() + " "+this.getText(R.string.dangerLevel)
                    +" "+danger+", "+this.getText(R.string.times)+":"+times);
        }else if (similar.get(0).getCategory().equals("Πλυμήρα")){
            category.setText(this.getText(R.string.flood)+" "+similar.get(0).getTimestamp() + " "+this.getText(R.string.dangerLevel)
                    +" "+danger+", "+this.getText(R.string.times)+":"+times);
        }else {
            category.setText(this.getText(R.string.earthquake)+" "+similar.get(0).getTimestamp() + " "+this.getText(R.string.dangerLevel)
                    +" "+danger+", "+this.getText(R.string.times)+":"+times);
        }

        coordinates.setText(similar.get(0).getLatitude()+", "+similar.get(0).getLongtitude());


        for(MessageFromUserClass m:similar){
            if(!m.getPhoto().equals("")){
                photoRef = FirebaseStorage.getInstance().getReference().child("photos/"+m.getPhoto());

                // Download the bytes of the photo
                photoRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                    // Convert the bytes to a Bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    // Display the photo in an ImageView
                    image.setImageBitmap(bitmap);
                }).addOnFailureListener(exception -> {
                    // Handle failed download
                });
                //image.setImageURI(Uri.parse(m.getPhoto()));
                break;

            }
        }
        for(MessageFromUserClass m:similar){
            s1.append(m.getComments()).append("\n");
        }

        comments.setText(s1.toString());


    }

    public void accept(View view){

        //βρίσκω το max id που υπάρχει για να προσθέσω +1 και μετά τα προσθέτω στην βάση
        MessageFromAdmin.orderByChild("id").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    maxiId = childSnapshot.child("id").getValue(Integer.class);
                }
                maxiId++;
                m1.setId(maxiId);
                m1.setCategory(similar.get(0).getCategory());
                m1.setLatitude(similar.get(0).getLatitude());
                m1.setLongtitude(similar.get(0).getLongtitude());
                m1.setTimestamp(similar.get(0).getTimestamp());
                maxiId=0;


                //To Προσθέτω στην βάση των συναγερμών
                MessageFromAdmin.child(Integer.toString(m1.getId())).setValue(similar.get(0)).addOnCompleteListener((task) ->{
                    if (task.isSuccessful()){
                        Toast.makeText(MoreInfoActivity.this, MoreInfoActivity.this.getString(R.string.published), Toast.LENGTH_SHORT).show();
                        //showMessage("Success!", "Saved!");
                    }else{
                        showMessage("Error", Objects.requireNonNull(task.getException()).getLocalizedMessage());
                    }
                });

                //κανω ενημέρωση την κατασταση των μηνυματων του καθε χρηστη
                Statistics.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data: snapshot.getChildren()){
                            forstatus.add(data.getValue(StatisticsClass.class));

                            for (StatisticsClass s: forstatus){
                                try {
                                    if (calculateDistance(s.getLatitude(),s.getLongtitude(),
                                            similar.get(0).getLatitude(),similar.get(0).getLongtitude())<=1&& similar.get(0).getCategory().equals(s.getCategory())
                                            &&!checkDate(similar.get(0).getTimestamp(),s.getTimestamp())){

                                        Statistics.child(Integer.toString(s.getId())).child("status").setValue("Ενεργοποιήθηκε").addOnCompleteListener((task)->{
                                            if (task.isSuccessful()){
                                                //showMessage("success","success");
                                            }
                                            else{
                                                showMessage("Error", Objects.requireNonNull(task.getException()).getLocalizedMessage());
                                            }
                                        });
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //Diagrafi apo to proto
                MessageFromUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data: snapshot.getChildren()){
                            forfelete.add(data.getValue(MessageFromUserClass.class));
                        }
                        for (MessageFromUserClass m: forfelete){
                            try {
                                if (calculateDistance(m.getLatitude(),m.getLongtitude(),
                                        similar.get(0).getLatitude(),similar.get(0).getLongtitude())<=1&& similar.get(0).getCategory().equals(m.getCategory())
                                        &&!checkDate(similar.get(0).getTimestamp(),m.getTimestamp())){

                                    MessageFromUser.child(Integer.toString(m.getId())).removeValue().addOnCompleteListener((task)->{
                                        if (task.isSuccessful()){
                                            //showMessage("success","success");
                                        }
                                        else{
                                            showMessage("Error", Objects.requireNonNull(task.getException()).getLocalizedMessage());
                                        }

                                    });

                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Intent intent = new Intent(MoreInfoActivity.this,ManagerUserActivity.class);
                startActivity(intent);
                MoreInfoActivity.this.finish();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void decline(View view){
        //κανω ενημέρωση την κατασταση των μηνυματων του καθε χρηστη
        Statistics.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data: snapshot.getChildren()){
                    forstatus.add(data.getValue(StatisticsClass.class));
                }
                for (StatisticsClass s: forstatus){
                    try {
                        if (calculateDistance(s.getLatitude(),s.getLongtitude(),
                                similar.get(0).getLatitude(),similar.get(0).getLongtitude())<=1&& similar.get(0).getCategory().equals(s.getCategory())
                                &&!checkDate(similar.get(0).getTimestamp(),s.getTimestamp())){

                            Statistics.child(Integer.toString(s.getId())).child("status").setValue("Απορρίφθηκε").addOnCompleteListener((task)->{
                                if (task.isSuccessful()){
                                    //showMessage("success","success");
                                    Intent intent = new Intent(MoreInfoActivity.this,ManagerUserActivity.class);
                                    startActivity(intent);
                                    MoreInfoActivity.this.finish();
                                }
                                else{
                                    showMessage("Error", Objects.requireNonNull(task.getException()).getLocalizedMessage());
                                }
                            });
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Το διαγραφω από το MessageFromUser
        MessageFromUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data: snapshot.getChildren()){
                    forfelete.add(data.getValue(MessageFromUserClass.class));
                }
                for (MessageFromUserClass m: forfelete){
                    try {
                        if (calculateDistance(m.getLatitude(),m.getLongtitude(),
                                similar.get(0).getLatitude(),similar.get(0).getLongtitude())<=1&& similar.get(0).getCategory().equals(m.getCategory())
                                &&!checkDate(similar.get(0).getTimestamp(),m.getTimestamp())){

                            MessageFromUser.child(Integer.toString(m.getId())).removeValue().addOnCompleteListener((task)->{
                                if (task.isSuccessful()){
                                    //showMessage("success","success");
                                }
                                else{
                                    showMessage("Error", Objects.requireNonNull(task.getException()).getLocalizedMessage());
                                }

                            });

                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void showMessage(String title, String message){
        //Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2){
        final double R = 6371; // Earth's radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public boolean checkDate(String date11, String date22) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date1 = format.parse(date11);
        Date date2 = format.parse(date22);
        long timeDiff = Math.abs(date2.getTime() - date1.getTime());
        int daysDiff = (int) (timeDiff / (1000 * 60 * 60 * 24));
        if(daysDiff>0){
            return true;
        }else return false;
    }
}