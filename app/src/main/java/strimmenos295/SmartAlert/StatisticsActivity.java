package strimmenos295.SmartAlert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class StatisticsActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase root;
    DatabaseReference Statistics;
    ArrayList<StatisticsClass> mList;
    ArrayList<String> list;
    ListView listView;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        listView = findViewById(R.id.listStat);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mList = new ArrayList<>();
        list = new ArrayList<>();
        root = FirebaseDatabase.getInstance();
        Statistics = root.getReference("Statistics");
        adapter = new ArrayAdapter(StatisticsActivity.this, android.R.layout.simple_list_item_single_choice, list);
        listView.setAdapter(adapter);

        Statistics.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mList.clear();
                list.clear();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    StatisticsClass message1 = childSnapshot.getValue(StatisticsClass.class);
                    if (message1.getUid().equals(user.getUid())){
                        mList.add(message1);
                        list.add(message1.toStringMessage(StatisticsActivity.this));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}