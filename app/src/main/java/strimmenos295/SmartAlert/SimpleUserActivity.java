package strimmenos295.SmartAlert;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SimpleUserActivity extends AppCompatActivity  {

    FirebaseAuth mAuth;
    FirebaseUser user;
    TextView test;
    Intent intentService;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_user);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);

                //return;
            }else{
                intentService = new Intent(this,UserService.class);
                startService(intentService);
            }

        }else{
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        test = findViewById(R.id.welcomeSimple);
        intentService = new Intent(this,UserService.class);
        startService(intentService);
        //test.setText(user.getUid());
    }

    public void signOut(View view){
        stopService(intentService);
        mAuth.signOut();
        this.finish();
        //Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
    }

    public void alarms(View view){
        Intent intent = new Intent(this, AlarmsUserActivity.class);
        startActivity(intent);
    }

    public void statistics(View view){
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    public void warnTheAdmin(View view){
        Intent intent = new Intent(this, CreateWarningMessage.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // Do something when the back button is pressed
        // For example, show a dialog asking the user to confirm they want to exit the app
        // or move the user to a different activity

        stopService(intentService);
        mAuth.signOut();
        this.finish();


        // You can also call the super method to let the default behavior (finish the activity) to take place
        super.onBackPressed();
    }


    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}