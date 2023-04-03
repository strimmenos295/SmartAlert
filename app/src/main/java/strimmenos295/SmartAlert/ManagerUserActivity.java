package strimmenos295.SmartAlert;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ManagerUserActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase root;
    DatabaseReference MessageFromUser;
    MessageFromUserClass message;
    int maxId=0;
    ArrayList<MessageFromUserClass> mList;
    Intent intentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_user);
        intentService = new Intent(this,ServiceForMessages.class);
        startService(intentService);
        message=new MessageFromUserClass();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mList = new ArrayList<>();
        root = FirebaseDatabase.getInstance();
        MessageFromUser = root.getReference("MessageFromUser");

    }

    public void signOut2(View view){
        stopService(intentService);
        mAuth.signOut();
        this.finish();
    }

    public void alarms(View view){
        Intent intent = new Intent(this, AlarmsAdminActivity.class);
        startActivity(intent);
    }

    public void openActForMesAdmin(View view){
        Intent intent = new Intent(this, ListOfMessagesAdmin.class);
        startActivity(intent);
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

}