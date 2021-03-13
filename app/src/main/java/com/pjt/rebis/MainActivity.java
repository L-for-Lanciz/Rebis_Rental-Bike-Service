package com.pjt.rebis;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pjt.rebis.authentication.Login;
import com.pjt.rebis.notification.NotificationActivity;
import com.pjt.rebis.ui.booking.Booking;
import com.pjt.rebis.ui.profile.online_identification;
import com.pjt.rebis.ui.profile.profile_handler_frag;
import com.pjt.rebis.ui.profile.walletMagnifier;
import com.pjt.rebis.ui.welcomeFrag;
import com.pjt.rebis.utility.SaveSharedPreference;
import com.pjt.rebis.notification.NotifySender;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/* This is the Main Activity. It hosts the bottom navigation view, and almost every fragment.
    *  It also computes operations needed in the backend to speed up or enhance processes. */
public class MainActivity extends AppCompatActivity {
    private String _usertype;
    private static FirebaseDatabase database;
    private String currentuser;
    private ImageView propic;
    private BottomNavigationView navView;
    private int currentItem;
    //public static Location current_location = new Location(LocationManager.GPS_PROVIDER);;

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
            currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            setUsernameAndUsertype();

            setToolbarAndDrawer();

            View bottomCU = findViewById(R.id.nav_viewC);
            View bottomRE = findViewById(R.id.nav_viewR);


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

            navView = findViewById(userizer);
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                     R.id.navigation_bikes, R.id.navigation_QRScan, R.id.navigation_history).build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupWithNavController(navView, navController);

            this.getSupportFragmentManager().popBackStack("welcomy", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            goToWelcome();

            //getLocation();

            if (NotifySender.mutex)
                new NotifySender(this, this).checkForNotificationToPrompt();
            //new Thread(new RentingNotifier(this, this)).start();

        } catch (Exception stopit) {
            stopit.printStackTrace();
            goToLogin();
        }
    }

    private void setUsernameAndUsertype() {
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
    }

    private void setToolbarAndDrawer() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final DrawerLayout drawerLayout = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.dranav);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.bringToFront();

        View nav_header = LayoutInflater.from(this).inflate(R.layout.header_drawer, null);
        String nomen= SaveSharedPreference.getUserName(this);
        if(nomen.length()>22)
            nomen = nomen.substring(0,20).concat("…");
        ((TextView) nav_header.findViewById(R.id.header_user)).setText(nomen);
        ((TextView) nav_header.findViewById(R.id.header_type)).setText(_usertype);
        propic = (ImageView) nav_header.findViewById(R.id.header_propic);

        setPropic();
        propic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPicHandler();
            }
        });

        navigationView.addHeaderView(nav_header);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.dr_wallets:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        goToWallet();
                        break;
                    case R.id.dr_identf:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        goToOID();
                        break;
                    case R.id.dr_notify:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        goToNotifications();
                        break;
                    case R.id.dr_logout:
                        logout();
                        break;
                    case R.id.dr_exit:
                        System.exit(0);
                        break;
                }
                return false;
            }
        });

    }

    private void upusertype(String user) {
        _usertype = user;
    }

    private void goToWallet() {
        int container = buildOverFrag();
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag1 = new walletMagnifier(container);
        ft.replace(container, frag1, "magnify");
        ft.commit();
    }

    private void goToOID() {
        int container = buildOverFrag();
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag3 = new online_identification();
        ft.replace(container, frag3, "identy");
        ft.commit();
    }

    private void setPropic() {
        DatabaseReference mReferencePropic = FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuser).child("PersonalData");
        mReferencePropic.child("profileImage").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String myPic = dataSnapshot.getValue(String.class);
                try {
                    Uri profilepicture = Uri.parse(myPic);
                    Picasso.get().load(profilepicture).resize(600,600).onlyScaleDown().into(propic);
                } catch (Exception sdjbnsd) {
                    sdjbnsd.printStackTrace();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});
    }

    private void goToLogin() {
        Intent login = new Intent(MainActivity.this, Login.class);
        startActivity(login);
        finish();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        NotifySender.notificationList.clear();
        SaveSharedPreference.clearPreferences(this);
        goToLogin();
    }

    private void goToWelcome(){
        int container = buildOverFrag();
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag3 = new welcomeFrag();
        ft.replace(container, frag3, "welcomy").addToBackStack("welcomy");
        ft.commit();
    }

    private int buildOverFrag() {
        currentItem = navView.getSelectedItemId();
        if (currentItem == R.id.navigation_history)
            return R.id.his_const;
        else if (currentItem == R.id.navigation_bikes)
            return R.id.bk_constr;
        else if (currentItem == R.id.navigation_QRScan)
            return R.id.constr_qr;
        else if (currentItem == R.id.navigation_newqrRENTER)
            return R.id.constr_qrre;
        else
            return R.id.constr_book;
    }

    private void goToPicHandler() {
        int container = buildOverFrag();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag5 = new profile_handler_frag(propic, navView.getSelectedItemId());
        ft.replace(container, frag5, "pikky");
        ft.commit();
    }

    private void goToNotifications() {
        Intent notif = new Intent(MainActivity.this, NotificationActivity.class);
        startActivity(notif);
        finish();
    }

    private void checkBookingTimeout() {
        DatabaseReference datab = FirebaseDatabase.getInstance().getReference().child("BOOKINGS");
        Query dbquery;
        if (SaveSharedPreference.getUserType(this).equals("customer"))
            dbquery = datab.orderByChild("customer").equalTo(SaveSharedPreference.getUserName(this));
        else
            dbquery = datab.orderByChild("renter").equalTo(SaveSharedPreference.getUserName(this));

        dbquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    ArrayList<Booking> bookingList = (ArrayList<Booking>) snapshot.getValue();
                    Booking booking = bookingList.get(0);
Log.e("#ITEM0", snapshot.toString());
Log.e("#ITEM", booking.toString());
                    //String key = (String) booking.keySet().toArray()[0];
//Log.e("#KEY", booking.keySet().toArray()[0].toString());
                    //Booking value = (Booking) booking.get(key);
                    Calendar current = Calendar.getInstance();
Log.e("DATE0", current.toString());
Log.e("DATE00", booking.getTime());
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ITALIAN);
                    Calendar old = Calendar.getInstance();
Log.e("DATE1", old.toString());
                    old.setTime(sdf.parse(booking.getTime()));
Log.e("DATE2", old.toString());
                    old.add(Calendar.HOUR_OF_DAY, 3);
                    if (old.before(current)) {
                        DatabaseReference brob = FirebaseDatabase.getInstance().getReference().child("BOOKINGS").child(String.valueOf(booking.getKey()));
                        brob.child("customer").removeValue();
                        brob.child("renter").removeValue();
                    }

                } catch (Exception ezghe) {
                    ezghe.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

/*
    private void getLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            current_location.setLatitude(location.getLatitude());
                            current_location.setLongitude(location.getLongitude());
                        }
                    }
                });
    }
*/
    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

}