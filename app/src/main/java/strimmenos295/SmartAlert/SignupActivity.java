package strimmenos295.SmartAlert;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    EditText email, password, reEnterPas;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        email= findViewById(R.id.email);
        password = findViewById(R.id.enterPass);
        reEnterPas=findViewById(R.id.reEnterPass);
        mAuth = FirebaseAuth.getInstance();

    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }

    public void signup(View view){
        if (password.getText().toString().equals(reEnterPas.getText().toString()) &&
                !email.getText().toString().equals("")&&
                !password.getText().toString().equals("") && !reEnterPas.getText().toString().equals("")){

            mAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                    .addOnCompleteListener((task) ->{
                        if (task.isSuccessful()){
                            showMessage(this.getText(R.string.suc).toString(), this.getText(R.string.successReg).toString());
                        }else{
                            showMessage(this.getText(R.string.error).toString(), task.getException().getLocalizedMessage());
                        }
                    });
        }else{
            showMessage(this.getText(R.string.error).toString(), this.getText(R.string.notPasswords).toString());
        }

    }
}