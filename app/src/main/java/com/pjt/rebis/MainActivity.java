package com.pjt.rebis;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.ByteArrayOutputStream;

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
                    R.id.navigation_profile, R.id.navigation_bikes, R.id.navigation_QRScan, R.id.navigation_history)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupWithNavController(navView, navController);


            String profilepicture = SaveSharedPreference.getUserPropic(this);
            if (profilepicture.length() < 100) {
                DatabaseReference mReferencePropic = FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuser).child("PersonalData");
                mReferencePropic.child("profileImage").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String mgg = dataSnapshot.getValue(String.class);
                        SaveSharedPreference.setUserPropic(MainActivity.this, mgg);
                        try {
                            Uri bikeImgUrl = Uri.parse(mgg);
                            Picasso.get().load(bikeImgUrl).into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    String tmp = BitMapToString(bitmap);
                                    SaveSharedPreference.setUserPropic(MainActivity.this, tmp);
                                }
                                @Override
                                public void onBitmapFailed(Exception sghi, Drawable errorDrawable) { }
                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {}
                            });
                        } catch (Exception sdjbnsd) {
                            sdjbnsd.printStackTrace();
                        }
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

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
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