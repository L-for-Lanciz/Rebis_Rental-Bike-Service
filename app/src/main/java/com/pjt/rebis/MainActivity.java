package com.pjt.rebis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pjt.rebis.Authentication.Login;
import com.pjt.rebis.Authentication.SaveSharedPreference;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

    /* This is the Main Activity. It hosts the bottom navigation view, and almost every fragment.
    *  It also computes operations needed in the backend to speed up or enhance processes. */
public class MainActivity extends AppCompatActivity {
    private String _usertype, _username;
    private static FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (database == null) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
        }

        if (SaveSharedPreference.getEmail(MainActivity.this).length() == 0) {
            goToLogin();
        }

        try {
            String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference credRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuser).child("Credentials");
            if (SaveSharedPreference.getUserName(MainActivity.this).length() == 0) {
                credRef.child("Username").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        SaveSharedPreference.setUserName(MainActivity.this, dataSnapshot.getValue(String.class));
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }});
            }

            View bottomCU = findViewById(R.id.nav_viewC);
            View bottomRE = findViewById(R.id.nav_viewR);

            _usertype = SaveSharedPreference.getUserType(MainActivity.this);
            if (SaveSharedPreference.getUserType(MainActivity.this).length() == 0) {
                credRef.child("Usertype").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String usrr = dataSnapshot.getValue(String.class);
                        SaveSharedPreference.setUserType(MainActivity.this, usrr);
                        upusertype(usrr);
                        finish();
                        startActivity(getIntent());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }});
            }

            int userizer=0;
            if (_usertype.equals("customer")) {
                userizer = R.id.nav_viewC;
                bottomCU.setVisibility(View.VISIBLE);
                bottomRE.setVisibility(View.INVISIBLE);
            } else if (_usertype.equals("renter")) {
                userizer = R.id.nav_viewR;
                bottomCU.setVisibility(View.INVISIBLE);
                bottomRE.setVisibility(View.VISIBLE);
            }

            BottomNavigationView navView = findViewById(userizer);

            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_profile, R.id.navigation_QRScan, R.id.navigation_history)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupWithNavController(navView, navController);


            String profilepicture = SaveSharedPreference.getUserPropic(this);
            if (profilepicture.length() == 0) {
                DatabaseReference mReferencePropic = FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuser).child("PersonalData").child("profileImage");
                mReferencePropic.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String mgg = dataSnapshot.getValue(String.class);
                        SaveSharedPreference.setUserPropic(MainActivity.this, mgg);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }});
            }

        } catch (Exception stopit) {
            stopit.printStackTrace();
            goToLogin();
        }

    }

    private void goToLogin() {
        Intent login = new Intent(MainActivity.this, Login.class);
        startActivity(login);
        finish();
    }

    private void upusertype(String user) {
        _usertype = user;
    }

}

/*
    activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, 1011);
    //  Then override the method that will be called after the permission dialog result:

    @Override
    public void onRequestPermissionsResult(int requestCode,
    String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1011: {

        // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        // Here user granted the permission
                } else {

        // permission denied, boo! Disable the
        // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your Camera", Toast.LENGTH_SHORT).show();
                }
            return;
            }
        }
    }

*/