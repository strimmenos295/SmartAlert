package strimmenos295.SmartAlert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ListOfMessagesAdmin extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase root;
    DatabaseReference MessageFromUser;
    DatabaseReference MessageFromAdmin;
    DatabaseReference Statistics;
    MessageFromUserClass message;
    int maxiId=0;
    int maxId=0;
    int pos = -1;
    ArrayList<MessageFromUserClass> mList;
    ArrayList<MessageFromUserClass> helpList;
    ArrayList<MessageFromUserClass> alreadyInList;
    ArrayList<MessageFromUserClass> forfelete;
    ArrayList<StatisticsClass> forstatus;
    ArrayList<String> list;
    ArrayList<Integer> dangerlevel;
    ArrayList<Integer> times;
    ArrayList<MessageFromUserClass>similar;
    ListView listView;
    ArrayAdapter adapter;
    MessageFromAdminClass m1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_messages_admin);
        listView = findViewById(R.id.listAdmin);
        message=new MessageFromUserClass();
        m1 = new MessageFromAdminClass();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mList = new ArrayList<>();
        helpList = new ArrayList<>();
        list = new ArrayList<>();
        forstatus = new ArrayList<>();
        alreadyInList = new ArrayList<>();
        forfelete = new ArrayList<>();
        dangerlevel = new ArrayList<>();
        times = new ArrayList<>();
        similar = new ArrayList<>();
        root = FirebaseDatabase.getInstance();
        MessageFromUser = root.getReference("MessageFromUser");
        MessageFromAdmin = root.getReference("MessageFromAdmin");
        Statistics = root.getReference("Statistics");
        adapter = new ArrayAdapter(ListOfMessagesAdmin.this, android.R.layout.simple_list_item_single_choice,list);
        listView.setAdapter(adapter);


        MessageFromUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mList.clear();
                list.clear();
                alreadyInList.clear();
                int i=0;


                //Ομαδοποιώ και ταξινομώ την λίστα που εμφανίζεται
                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                    MessageFromUserClass message1 = childSnapshot.getValue(MessageFromUserClass.class);
                    try {
                        if (!checkIfAlready(alreadyInList,message1)){
                            alreadyInList.add(message1);
                            i=0;
                            if (message1.getCategory().equals("πυρκαγιά")){
                                for (DataSnapshot child2 : snapshot.getChildren()){
                                    //assert message1 != null;
                                    String date = child2.child("timestamp").getValue(String.class);
                                    if (calculateDistance(message1.getLatitude(),message1.getLongtitude(),
                                            child2.child("latitude").getValue(Double.class),
                                            child2.child("longtitude").getValue(Double.class))<=1
                                            && Objects.requireNonNull(child2.child("category").getValue()).toString().equals("πυρκαγιά")&&
                                            !checkDate(message1.getTimestamp(),date)){
                                        i++;
                                    }
                                }

                            }else if(message1.getCategory().equals("Σεισμός")){
                                for (DataSnapshot child2 : snapshot.getChildren()){
                                    //assert message1 != null;
                                    String date = child2.child("timestamp").getValue(String.class);
                                    if(calculateDistance(message1.getLatitude(),message1.getLongtitude(),
                                            child2.child("latitude").getValue(Double.class),
                                            child2.child("longtitude").getValue(Double.class))<=1
                                            && Objects.requireNonNull(child2.child("category").getValue()).toString().equals("Σεισμός")&&
                                            !checkDate(message1.getTimestamp(),date)){
                                        i++;
                                    }
                                }

                            }else if(message1.getCategory().equals("Πλυμήρα")){
                                for (DataSnapshot child2 : snapshot.getChildren()){
                                    String date = child2.child("timestamp").getValue(String.class);
                                    if(calculateDistance(message1.getLatitude(),message1.getLongtitude(),
                                            child2.child("latitude").getValue(Double.class),
                                            child2.child("longtitude").getValue(Double.class))<=1
                                            && Objects.requireNonNull(child2.child("category").getValue()).toString().equals("Πλυμήρα")&&
                                            !checkDate(message1.getTimestamp(),date)){
                                        i++;
                                    }
                                }

                            }
                            int danger;
                            if (i==1){
                                dangerlevel.add(1);
                                danger =1;
                            }else if (i<=5){
                                dangerlevel.add(2);
                                danger = 2;
                            }else {
                                dangerlevel.add(3);
                                danger=3;
                            }

                            times.add(i);
                            mList.add(message1);
                            list.add(ListOfMessagesAdmin.this.getString(R.string.dangerLevel)+danger+", "+message1.toStringMessage(ListOfMessagesAdmin.this)+" "+i+" "+ListOfMessagesAdmin.this.getString(R.string.times));


                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                }

                // ταξινόμηση βάση επικυνδυνότητας με αλγόριθμο φυσαλλίδα

                boolean swapped = true;
                int j = 0;
                while (swapped) {
                    swapped = false;
                    j++;
                    for (int k = 0; k < list.size() - j; k++) {
                        if (dangerlevel.get(k) < dangerlevel.get(k + 1)) {
                            int temp = dangerlevel.get(k);
                            dangerlevel.set(k, dangerlevel.get(k + 1));
                            dangerlevel.set(k + 1, temp);
                            String t2 = list.get(k);
                            list.set(k, list.get(k + 1));
                            list.set(k + 1, t2);
                            MessageFromUserClass t3=mList.get(k);
                            mList.set(k, mList.get(k + 1));
                            mList.set(k + 1, t3);
                            int temp1 = times.get(k);
                            times.set(k, times.get(k + 1));
                            times.set(k + 1, temp1);
                            swapped = true;
                        }
                    }
                }


                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                listView.setSelection(position);

                pos = position;

            }
        });
    }

    //Με αυτήν την συναρτηση κοιτάω αν υπάχει κάποιο παρόμοιο αντικείμενο help Message μεσα στην λίστα
    private boolean checkIfAlready(ArrayList<MessageFromUserClass> list,MessageFromUserClass helpMessage) throws ParseException {
        if (list.isEmpty()) return false;
        for (MessageFromUserClass message : list){
            if (calculateDistance(message.getLatitude(),message.getLongtitude(),
                    helpMessage.getLatitude(),helpMessage.getLongtitude())<=1&& helpMessage.getCategory().equals(message.getCategory())
                    &&!checkDate(message.getTimestamp(),helpMessage.getTimestamp())){
                return true;
            }
        }
        return false;
    }

    public void accept(View view){
        //helpList=mList;
        helpList.clear();
        for (MessageFromUserClass h: mList){
            helpList.add(h);
        }
        if (pos!=-1){
            //βρίσκω το max id που υπάρχει για να προσθέσω +1 και μετά τα προσθέτω στην βάση
            MessageFromAdmin.orderByChild("id").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        maxiId = childSnapshot.child("id").getValue(Integer.class);
                    }
                    maxiId++;
                    m1.setId(maxiId);
                    m1.setCategory(helpList.get(pos).getCategory());
                    m1.setLatitude(helpList.get(pos).getLatitude());
                    m1.setLongtitude(helpList.get(pos).getLongtitude());
                    m1.setTimestamp(helpList.get(pos).getTimestamp());
                    maxiId=0;


                    //To Προσθέτω στην βάση των συναγερμών
                    MessageFromAdmin.child(Integer.toString(m1.getId())).setValue(m1).addOnCompleteListener((task) ->{
                        if (task.isSuccessful()){
                            Toast.makeText(ListOfMessagesAdmin.this, ListOfMessagesAdmin.this.getString(R.string.published), Toast.LENGTH_SHORT).show();
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
                            }
                            for (StatisticsClass s: forstatus){
                                try {
                                    if (calculateDistance(s.getLatitude(),s.getLongtitude(),
                                            helpList.get(pos).getLatitude(),helpList.get(pos).getLongtitude())<=1&& helpList.get(pos).getCategory().equals(s.getCategory())
                                            &&!checkDate(helpList.get(pos).getTimestamp(),s.getTimestamp()))
                                    {

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
                                            helpList.get(pos).getLatitude(),helpList.get(pos).getLongtitude())<=1&& helpList.get(pos).getCategory().equals(m.getCategory())
                                            &&!checkDate(helpList.get(pos).getTimestamp(),m.getTimestamp())){

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

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else showMessage(ListOfMessagesAdmin.this.getString(R.string.error),ListOfMessagesAdmin.this.getString(R.string.nothingChosen));
    }

    public void decline(View view){
        helpList.clear();
        forstatus.clear();
        forfelete.clear();
        for (MessageFromUserClass h: mList){
            helpList.add(h);
        }
        if (pos!=-1){
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
                                    helpList.get(pos).getLatitude(),helpList.get(pos).getLongtitude())<=1&& helpList.get(pos).getCategory().equals(s.getCategory())
                                    &&!checkDate(helpList.get(pos).getTimestamp(),s.getTimestamp())){

                                Statistics.child(Integer.toString(s.getId())).child("status").setValue("Απορρίφθηκε").addOnCompleteListener((task)->{
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
                                    helpList.get(pos).getLatitude(),helpList.get(pos).getLongtitude())<=1&& helpList.get(pos).getCategory().equals(m.getCategory())
                                    &&!checkDate(helpList.get(pos).getTimestamp(),m.getTimestamp())){

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


        }else showMessage(ListOfMessagesAdmin.this.getString(R.string.error),ListOfMessagesAdmin.this.getString(R.string.nothingChosen));

    }

    public void moreInfo(View view){
        if (pos!=-1){
            helpList.clear();
            similar.clear();
            similar.add(mList.get(pos));
            MessageFromUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot data: snapshot.getChildren()){
                        helpList.add(data.getValue(MessageFromUserClass.class));

                    }

                    for (MessageFromUserClass mes : helpList){
                        try {
                            if (calculateDistance(mes.getLatitude(),mes.getLongtitude(),
                                    similar.get(0).getLatitude(),similar.get(0).getLongtitude())<=1&& similar.get(0).getCategory()
                                    .equals(mes.getCategory()) &&!checkDate(mes.getTimestamp(),similar.get(0).getTimestamp())){
                                similar.add(mes);
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
            Intent intent = new Intent(this,MoreInfoActivity.class);
            intent.putExtra("dangerLevel",dangerlevel.get(pos));
            intent.putExtra("times",times.get(pos));
            intent.putExtra("listOfSimilarMessages", similar);
            startActivity(intent);
            this.finish();
        }else showMessage(ListOfMessagesAdmin.this.getString(R.string.error),ListOfMessagesAdmin.this.getString(R.string.nothingChosen));

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