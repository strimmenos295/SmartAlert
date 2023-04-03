package strimmenos295.SmartAlert;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

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

public class ServiceForMessages extends Service {
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase root;
    DatabaseReference MessageFromUser;
    MessageFromUserClass message;
    int maxId=0;
    ArrayList<MessageFromUserClass> mList;

    public ServiceForMessages() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        message=new MessageFromUserClass();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mList = new ArrayList<>();
        root = FirebaseDatabase.getInstance();
        MessageFromUser = root.getReference("MessageFromUser");
        MessageFromUser.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MessageFromUserClass message = snapshot.getValue(MessageFromUserClass.class);

                Intent intent = new Intent(getApplicationContext(), ListOfMessagesAdmin.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                NotificationChannel channel = new NotificationChannel("123","channelUnipi", NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.createNotificationChannel(channel);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"123");
                if(message.getCategory().equals("πυρκαγιά")){
                    builder.setContentTitle(ServiceForMessages.this.getText(R.string.newdanger))
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentText(ServiceForMessages.this.getText(R.string.fire)+message.getTimestamp())
                            .setAutoCancel(true).setContentIntent(pendingIntent);
                }else if (message.getCategory().equals("Πλυμήρα")){
                    builder.setContentTitle(ServiceForMessages.this.getText(R.string.newdanger))
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentText(ServiceForMessages.this.getText(R.string.flood)+message.getTimestamp())
                            .setAutoCancel(true).setContentIntent(pendingIntent);
                }else {
                    builder.setContentTitle(ServiceForMessages.this.getText(R.string.newdanger))
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentText(ServiceForMessages.this.getText(R.string.earthquake)+message.getTimestamp())
                            .setAutoCancel(true).setContentIntent(pendingIntent);
                }

                manager.notify(1,builder.build());


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
}