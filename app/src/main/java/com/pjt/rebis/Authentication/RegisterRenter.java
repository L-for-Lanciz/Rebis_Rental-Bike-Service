package com.pjt.rebis.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pjt.rebis.R;

import java.util.HashMap;
import java.util.Map;

    /* Register Activity for renters only. Allows users to register to the system. Accessible from the login activity. The users must
    *  give a valid email and choose valid username and password.
    *  After that, they will be sent to the 'storeInformations' fragment. */
public class RegisterRenter extends AppCompatActivity {
    private EditText passField,cpassField,emailField, storenameField;
    private TextView loginField;
    private Button regBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_renter);

        FirebaseApp.initializeApp(this);
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        storenameField=findViewById(R.id.rgRE_storename);
        passField=findViewById(R.id.rgRE_pass);
        cpassField=findViewById(R.id.rgRE_copass);
        emailField=findViewById(R.id.rgRE_email);
        loginField=findViewById(R.id.rgRE_tologin);
        regBtn=findViewById(R.id.rgRE_signup);


        loginField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String storename=storenameField.getText().toString();
                final String pass=passField.getText().toString();
                final String cpass=cpassField.getText().toString();
                final String email=emailField.getText().toString();

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("unicity").child("usernames").child(storename);
                userRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String ridi=" ";
                        if (dataSnapshot.getValue(String.class) != null)
                            ridi=dataSnapshot.getValue(String.class);
                        if ((ridi.toLowerCase()).equals(storename.toLowerCase())) {
                            storenameField.setText("");
                            storenameField.setError(getString(R.string.reg_alreadytkn));
                        } else {

                            if(!TextUtils.isEmpty(pass) && !TextUtils.isEmpty(cpass) && !TextUtils.isEmpty(email)){
                                if(pass.equals(cpass)) {

                                    if(pass.length()<8)
                                        passField.setError(getResources().getString(R.string.reg_pwdlength));
                                    else if(!pass.matches(".*[a-z].*"))
                                        passField.setError(getResources().getString(R.string.reg_pwdlowercase));
                                    else if(!pass.matches(".*[A-Z].*"))
                                        passField.setError(getResources().getString(R.string.reg_pwduppercase));
                                    else{
                                        //success pass. now try to add to database
                                        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if(task.isSuccessful()) {
                                                    mCurrentUser = mAuth.getCurrentUser();

                                                    Map<String ,String> hashmap=new HashMap<>();
                                                    hashmap.put("email", email);

                                                    DatabaseReference uniqueRef = FirebaseDatabase.getInstance().getReference().child("unicity").child("usernames").child(storename);
                                                    uniqueRef.child("name").setValue(storename);

                                                    DatabaseReference currentRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(mCurrentUser.getUid())
                                                            .child("Credentials");
                                                    currentRef.child("Username").setValue(storename);
                                                    currentRef.child("Usertype").setValue("renter");

                                                    db.collection("USERS")
                                                            .document(mCurrentUser.getUid())
                                                            .set(hashmap)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                }
                                                            });
                                                    //Snackbar.make(findViewById(R.id.constraintreg), R.string.ver_email, Snackbar.LENGTH_LONG).show();
                                                    goToStoreInfo();
                                                    //sendVerificationEmail();
                                                } else {
                                                    makeToast(getResources().getString(R.string.reg_failedtoreg));
                                                }
                                            }
                                        });
                                    }
                                } else
                                    passField.setError(getResources().getString(R.string.reg_paswmis));
                            } else {
                                makeToast(getResources().getString(R.string.reg_entall));
                            }

                        }
                    }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }});
            }
        });

    }

    public void proceedPlease(int x) {
        if (x==0)
            sendVerificationEmail();
        else {
            Toast.makeText(this, getString(R.string.stin_failed), Toast.LENGTH_LONG);
            DatabaseReference currentRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(mCurrentUser.getUid());
            currentRef.setValue(null);
        }
    }

    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                       // email sent
                       // after email is sent just logout the user and finish this activity
                       FirebaseAuth.getInstance().signOut();
                       startActivity(new Intent(RegisterRenter.this, Login.class));
                       finish();
                    } else {
                       // email not sent, so display message and restart the activity or do whatever you wish to do
                       //restart this activity
                       overridePendingTransition(0, 0);
                       finish();
                       overridePendingTransition(0, 0);
                       startActivity(getIntent());
                    }
                }
        });
    }

    private void goToLogin(){
        Intent regiRE = new Intent(RegisterRenter.this, Login.class);
        startActivity(regiRE);
        finish();
    }

    private void goToStoreInfo() {
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fraggy = new storeInformations();
        ft.replace(R.id.constraintrgRE, fraggy, "stin");
        ft.commit();
    }

    private void makeToast(String _text) {
        Toast.makeText(RegisterRenter.this, _text, Toast.LENGTH_LONG).show();
    }

}