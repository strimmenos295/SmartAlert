package strimmenos295.SmartAlert;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    FirebaseAuth mAuth;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        email = findViewById(R.id.email2);
        password = findViewById(R.id.enterPass2);
        mAuth = FirebaseAuth.getInstance();
    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }

    public void signIn(View view){
        if(!email.getText().toString().equals("")&&!password.getText()
                .toString().equals("")){
            mAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                    .addOnCompleteListener((task) -> {
                        if (task.isSuccessful()){
                            user = mAuth.getCurrentUser();
                            if (user.getUid().equals("sr13F7P9wVMtXSYw70uGHMjOSaF2")){
                                Intent intent = new Intent(this, ManagerUserActivity.class);
                                startActivity(intent);
                            }else{
                                Intent intent = new Intent(this, SimpleUserActivity.class);
                                startActivity(intent);
                            }
                        }else{
                            showMessage("Error", Objects.requireNonNull(task.getException()).getLocalizedMessage());
                        }
                    });
        }else{
            showMessage("Error", "provide for all information");
        }
    }

}