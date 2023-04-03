package strimmenos295.SmartAlert;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserService extends Service implements LocationListener {

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase root;
    DatabaseReference MessageFromAdmin;
    MessageFromAdminClass message;
    LocationManager locationManager;
    double latitude, longtitude;
    Location currentLocation;


    public UserService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        message=new MessageFromAdminClass();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        root = FirebaseDatabase.getInstance();
        MessageFromAdmin = root.getReference("MessageFromAdmin");

        MessageFromAdmin.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    if (ActivityCompat.checkSelfPermission(UserService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(UserService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }


                    try{
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, UserService.this);

                        Thread.sleep(3000);
                    } catch (InterruptedException | SecurityException e) {
                        e.printStackTrace();
                    }
                    do {
                        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }while(currentLocation==null);
                    locationManager.removeUpdates(UserService.this);

                }else{
                    //showMessage("Πρόβλημα", "Το gps είναι κλειστό. Παρακαλώ ανοίξτε το!");
                    return;
                }
                MessageFromAdminClass message = snapshot.getValue(MessageFromAdminClass.class);

                if(calculateDistance(currentLocation.getLatitude(),currentLocation.getLongitude(),message.getLatitude(),message.getLongtitude())<=5){
                    Intent intent = new Intent(getApplicationContext(), AlarmsUserActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                    NotificationChannel channel = new NotificationChannel("123","channelUnipi", NotificationManager.IMPORTANCE_HIGH);
                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.createNotificationChannel(channel);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"123");
                    if(message.getCategory().equals("πυρκαγιά")){
                        builder.setContentTitle(UserService.this.getText(R.string.newdanger))
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setContentText(UserService.this.getText(R.string.newdanger).toString()+" "+UserService.this.getText(R.string.fire)+UserService.this.getText(R.string.area)+message.getTimestamp())
                                .setAutoCancel(true).setContentIntent(pendingIntent);
                    }else if (message.getCategory().equals("Πλυμήρα")){
                        builder.setContentTitle(UserService.this.getText(R.string.newdanger))
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setContentText(UserService.this.getText(R.string.newdanger).toString()+" "+UserService.this.getText(R.string.flood)+UserService.this.getText(R.string.area)+message.getTimestamp())
                                .setAutoCancel(true).setContentIntent(pendingIntent);
                    }else {
                        builder.setContentTitle(UserService.this.getText(R.string.newdanger))
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setContentText(UserService.this.getText(R.string.newdanger).toString()+" "+UserService.this.getText(R.string.earthquake)+UserService.this.getText(R.string.area)+message.getTimestamp())
                                .setAutoCancel(true).setContentIntent(pendingIntent);
                    }
                    manager.notify(1,builder.build());
                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longtitude = location.getLongitude();
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