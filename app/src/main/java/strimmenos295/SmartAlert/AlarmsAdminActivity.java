package strimmenos295.SmartAlert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

public class AlarmsAdminActivity extends AppCompatActivity {

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
    int pos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms_admin);
        listView = findViewById(R.id.alarmsForAdmin);
        m1 = new MessageFromAdminClass();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mList = new ArrayList<>();
        list = new ArrayList<>();
        root = FirebaseDatabase.getInstance();
        MessageFromUser = root.getReference("MessageFromUser");
        MessageFromAdmin = root.getReference("MessageFromAdmin");
        Statistics = root.getReference("Statistics");
        adapter = new ArrayAdapter(AlarmsAdminActivity.this, android.R.layout.simple_list_item_single_choice, list);
        listView.setAdapter(adapter);

        MessageFromAdmin.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mList.clear();
                list.clear();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    MessageFromAdminClass message1 = childSnapshot.getValue(MessageFromAdminClass.class);
                    mList.add(message1);
                    list.add(message1.toStringMessage(AlarmsAdminActivity.this));
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

    public void delete(View view){
        if(pos!=-1){
            MessageFromAdmin.child(Integer.toString(mList.get(pos).getId())).removeValue().addOnCompleteListener((task)->{
                if (task.isSuccessful()){
                    //showMessage("success","success");
                }
                else{
                    showMessage("Error", Objects.requireNonNull(task.getException()).getLocalizedMessage());
                }

            });
        }else showMessage(this.getString(R.string.error),this.getString(R.string.nothingChosen));

    }

    void showMessage(String title, String message){
        //Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}