package com.pjt.rebis.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pjt.rebis.MainActivity;
import com.pjt.rebis.R;
import com.pjt.rebis.utility.SaveSharedPreference;

/* Login Activity: allows users to access the service. Of course it requires registered and confirmed accounts. */
public class Login extends AppCompatActivity {
    public static int makethedifference;
    EditText emailfield,passfield;
    TextView reg, lostPass;
    Button logBtn;
    String email;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailfield=findViewById(R.id.log_email);
        passfield=findViewById(R.id.log_password);
        logBtn=findViewById(R.id.log_signin);
        reg=findViewById(R.id.log_toregister);
        lostPass=findViewById(R.id.log_lostpassw);

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               goToRegister();
            }
        });

        FirebaseApp.initializeApp(this.getApplicationContext());
        mAuth= FirebaseAuth.getInstance();

        makethedifference=0;

        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=emailfield.getText().toString();
                String pass=passfield.getText().toString();
                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {
                    //checking validity of the password
                    if(pass.length()<8)
                        makeToast(getResources().getString(R.string.reg_pwdlength));
                    else if(!pass.matches(".*[a-z].*"))
                        makeToast(getResources().getString(R.string.reg_pwdlowercase));
                    else if(!pass.matches(".*[A-Z].*"))
                        makeToast(getResources().getString(R.string.reg_pwduppercase));
                    else{
                        //success pass. now check with database
                        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    checkIfEmailVerified();
                                }
                                else{
                                    makeToast(getResources().getString(R.string.log_failedto));
                                }
                            }
                        });
                    }
                }
                else{
                    makeToast(getResources().getString(R.string.reg_entall));
                }
            }
        });

        lostPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotYourPassword();
            }
        });

    }

    private void goToHome(){
        Intent home=new Intent(Login.this, MainActivity.class);
        startActivity(home);
        finish();
    }

    private void goToRegister(){
            atVeryBeginning();
    }

    private void atVeryBeginning() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag = new roleSelector();
        ft.replace(R.id.constraintlog, frag, "roles");
        ft.commit();
    }

    private void checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified()) {
            // user is verified, so you can finish this activity or send user to activity which you want.
            finish();
            Toast.makeText(Login.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
            SaveSharedPreference.setEmail(this, email);
            goToHome();
        }
        else {
            // email is not verified, so just prompt the message to the user and restart this activity.
            // NOTE: don't forget to log out the user.
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(Login.this,"Confirm your E-mail...", Toast.LENGTH_LONG).show();
            //restart this activity
        }
    }

    private void forgotYourPassword() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag322 = new forgot_YourPassword();
        ft.replace(R.id.constraintlog, frag322, "fypfrgm");
        ft.commit();
    }

    private void makeToast(String _text) {
        Toast.makeText(Login.this, _text, Toast.LENGTH_LONG).show();
    }
}