package strimmenos295.SmartAlert;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public class CreateWarningMessage extends AppCompatActivity implements AdapterView.OnItemSelectedListener, LocationListener {
    String[] categories = {"πυρκαγιά", "Σεισμός", "Πλυμήρα"};
    LocationManager locationManager;
    StringBuilder sb = new StringBuilder();
    String gpsXY="";
    EditText comments;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase root;
    DatabaseReference MessageFromUser;
    DatabaseReference MessageFromAdmin;
    DatabaseReference Statistics;
    StorageReference storageReference;
    MessageFromUserClass message1;
    MessageFromAdminClass m2;
    ArrayList<MessageFromAdminClass> listAdmin;
    StatisticsClass s1;
    Spinner spin;
    String photo;
    ImageSwitcher imageView;
    Uri img;
    int maxId;
    private static final int REQUEST_IMAGE_PICKER = 1;
    private ActivityResultLauncher<String> pickImageLauncher;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_warning_message);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        m2 = new MessageFromAdminClass();
        listAdmin = new ArrayList<>();

        root = FirebaseDatabase.getInstance();
        MessageFromUser = root.getReference("MessageFromUser");
        Statistics = root.getReference("Statistics");
        MessageFromAdmin = root.getReference("MessageFromAdmin");
        storageReference = FirebaseStorage.getInstance().getReference().child("photos/");

        comments = findViewById(R.id.comments);

        message1 = new MessageFromUserClass();

        s1= new StatisticsClass();


        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        spin = findViewById(R.id.category);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,categories);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        //Getting the location Service
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        // Ευρεση Τοποθεσίας
        //Στο 1ο if ελέγχω αν το gps της συσκευής είναι ανοιχτό
        //Στο 2ο ελέγχω για τα permissions
        //χρησιμοποιώ τους παρακάτω ελεγχους και το request permissions 2 φορές μέσα στον κώδικα για να προλάβει
        //να παρει τμή το gpsXY
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
                //return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, this);

        }else{
            showMessage(this.getText(R.string.error).toString(), this.getText(R.string.gps).toString());
            this.finish();
        }


        //fotografia
        message1.setPhoto("");
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        // Use the image file URI to load the image into an ImageView
                        //imageView.setImageURI(result);
                        img=result;
                        photo = "photo_" + System.currentTimeMillis() + ".jpg";
                        message1.setPhoto(photo);
                        Toast.makeText(this, photo, Toast.LENGTH_SHORT).show();
                        //showMessage("foto",result.toString());


                        // Use the image file URI to upload the image to Firebase Realtime Database (as described in my previous answer)
                        // ...
                    }
                }
        );

    }

    public void submit(View view){
        //timestamp
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String timestamp = dtf.format(now);
        message1.setTimestamp(timestamp);

        String category= spin.getSelectedItem().toString();
        message1.setCategory(category);
        message1.setComments(comments.getText().toString());




        // Ευρεση Τοποθεσίας
        //Στο 1ο if ελέγχω αν το gps της συσκευής είναι ανοιχτό
        //Στο 2ο ελέγχω για τα permissions
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, this);

        }else{
            showMessage(this.getText(R.string.error).toString(), this.getText(R.string.gps).toString());
            return;
        }



        if(!message1.getPhoto().equals("")){
            // Generate a unique file name for the photo (e.g. using a timestamp)
            //String fileName = "photo_" + System.currentTimeMillis() + ".jpg";
            storageReference.child(message1.getPhoto()).putFile(img)
                    .addOnSuccessListener(taskSnapshot -> Log.d(TAG, "Image uploaded successfully"))
                    .addOnFailureListener(exception -> Log.e(TAG, "Error uploading image: " + exception.getMessage()));

            //storageReference.getDownloadUrl().addOnSuccessListener(uri -> message1.setPhoto(uri.toString()));
        }



        MessageFromAdmin.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child: snapshot.getChildren()){
                    m2 = child.getValue(MessageFromAdminClass.class);
                    listAdmin.add(m2);
                }
                if (calculateDistance(m2.getLatitude(),m2.getLongtitude(),
                        message1.getLatitude(),message1.getLongtitude())<=1&& message1.getCategory().equals(m2.getCategory())){
                    Toast.makeText(getApplicationContext(), CreateWarningMessage.this.getText(R.string.alreadyThere).toString(), Toast.LENGTH_LONG).show();
                }else {
                    //βρίσκω το max id που υπάρχει για να προσθέσω +1 και μετά τα προσθέτω στην βάση
                    MessageFromUser.orderByChild("id").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                maxId = childSnapshot.child("id").getValue(Integer.class);
                            }
                            maxId++;
                            message1.setId(maxId);
                            maxId=0;

                            MessageFromUser.child(Integer.toString(message1.getId())).setValue(message1).addOnCompleteListener((task) ->{
                                if (task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(), CreateWarningMessage.this.getText(R.string.success).toString(), Toast.LENGTH_LONG).show();
                                }else{
                                    showMessage("Error", Objects.requireNonNull(task.getException()).getLocalizedMessage());
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    Statistics.orderByChild("id").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                maxId = childSnapshot.child("id").getValue(Integer.class);
                            }
                            maxId++;
                            s1.setId(maxId);
                            s1.setCategory(message1.getCategory());
                            s1.setLatitude(message1.getLatitude());
                            s1.setLongtitude(message1.getLongtitude());
                            s1.setTimestamp(message1.getTimestamp());
                            s1.setStatus("Σε Αναμονή");
                            s1.setUid(user.getUid());
                            maxId=0;

                            Statistics.child(Integer.toString(s1.getId())).setValue(s1).addOnCompleteListener((task) ->{
                                if (task.isSuccessful()){
                                    //showMessage("Success!", "Saved!");
                                }else{
                                    showMessage("Error", Objects.requireNonNull(task.getException()).getLocalizedMessage());
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void searchPhoto(View view){
        pickImageLauncher.launch("image/*");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICKER && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            // Use the image file URI to load the image into an ImageView
            //imageView.setImageURI(imageUri);
            img=imageUri;
            message1.setPhoto(photo);
            showMessage("foto",imageUri.toString());

            // Use the image file URI to upload the image to Firebase Realtime Database (as described in my previous answer)
            // ...
        }
    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //Toast.makeText(getApplicationContext(), categories[i], Toast.LENGTH_LONG).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                // handle location provider being unavailable
                break;
            case LocationProvider.AVAILABLE:
                // handle location provider becoming available
                break;
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        message1.setLatitude(location.getLatitude());
        message1.setLongtitude(location.getLongitude());
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
}