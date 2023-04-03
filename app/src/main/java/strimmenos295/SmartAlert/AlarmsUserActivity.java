package strimmenos295.SmartAlert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class AlarmsUserActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase root;
    DatabaseReference MessageFromUser;
    DatabaseReference MessageFromAdmin;
    DatabaseReference Statistics;
    ArrayList<MessageFromAdminClass> mList;
    ArrayList<String> list;
    ListView listView;
    ArrayAdapter adapter;
    MessageFromAdminClass m1;
    LocationManager locationManager;
    Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms_user);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listView = findViewById(R.id.listView2);
        m1 = new MessageFromAdminClass();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mList = new ArrayList<>();
        list = new ArrayList<>();
        root = FirebaseDatabase.getInstance();
        MessageFromUser = root.getReference("MessageFromUser");
        MessageFromAdmin = root.getReference("MessageFromAdmin");
        Statistics = root.getReference("Statistics");
        adapter = new ArrayAdapter(AlarmsUserActivity.this, android.R.layout.simple_list_item_single_choice, list);
        listView.setAdapter(adapter);

        MessageFromAdmin.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mList.clear();
                list.clear();
                do {
                    if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                        if (ActivityCompat.checkSelfPermission(AlarmsUserActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(AlarmsUserActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(AlarmsUserActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
                            return;
                        }
                        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    }else{
                        showMessage("Πρόβλημα", "Το gps είναι κλειστό. Παρακαλώ ανοίξτε το!");
                        return;
                    }
                    currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }while(currentLocation==null);

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    MessageFromAdminClass message1 = childSnapshot.getValue(MessageFromAdminClass.class);
                    if (calculateDistance(message1.getLatitude(),message1.getLongtitude(),currentLocation.getLatitude(),currentLocation.getLongitude())<=5){
                        mList.add(message1);
                        list.add(message1.toStringMessage(AlarmsUserActivity.this));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    void showMessage(String title, String message){
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
}
