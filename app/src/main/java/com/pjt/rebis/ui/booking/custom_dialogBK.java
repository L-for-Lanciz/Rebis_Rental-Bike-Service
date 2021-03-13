package com.pjt.rebis.ui.booking;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pjt.rebis.R;
import com.pjt.rebis.ui.bikes.Bicicletta;
import com.pjt.rebis.utility.SaveSharedPreference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class custom_dialogBK extends Dialog implements android.view.View.OnClickListener {
    public Activity c;
    public Dialog d;
    public Button yes,no;
    private String ref, title, msg;
    private Bicicletta bk;
    private String storename;

    public custom_dialogBK(Activity a, String _ref, Bicicletta bk, String storename, String _title, String _msg) {
        super(a);
        this.c = a;
        this.title = _title;
        this.msg = _msg;
        this.ref = _ref;
        this.bk = bk;
        this.storename = storename;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialogdr);
        yes = (Button) findViewById(R.id.cddr_btnYes);
        yes.setOnClickListener(this);
        no = (Button) findViewById(R.id.cddr_btnNo);
        no.setOnClickListener(this);
        TextView myTitle = findViewById(R.id.cddr_title);
        TextView myMsg = findViewById(R.id.cddr_msg);
        myTitle.setText(title);
        myMsg.setText(msg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cddr_btnNo:
                dismiss();
                break;
            case R.id.cddr_btnYes:
                updateBookOnDB();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void updateBookOnDB() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("USERS").child(ref).child("Bikes");
        Query query = db.orderByChild("name").equalTo(bk.getName());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                Map.Entry<String,Object> entry = map.entrySet().iterator().next();
                String key = entry.getKey();
                String reference = entry.getValue().toString();
                try {
                    final String biceuid = key;
                    if (reference.contains("status=available")) { //!(reference.contains("unavailable")) && !(reference.contains("booked#@&@#"))
                        final Date currentTime = Calendar.getInstance().getTime();
                        dataSnapshot.getRef().child(key).child("customer").setValue(SaveSharedPreference.getUserName(c) + "#@&@#" + currentTime);
                        dataSnapshot.getRef().child(key).child("status").setValue("booked");

                        final DatabaseReference dbtmp = FirebaseDatabase.getInstance().getReference().child("BOOKINGS");
                        dbtmp.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int totalNumber = (int) dataSnapshot.getChildrenCount();
                                totalNumber = totalNumber+1;

                                DatabaseReference bikeRef = dbtmp.child(String.valueOf(totalNumber));
                                bikeRef.child("customer").setValue(SaveSharedPreference.getUserName(c));
                                bikeRef.child("renter").setValue(storename);
                                bikeRef.child("renterUID").setValue(ref);
                                bikeRef.child("bike").setValue(biceuid);
                                bikeRef.child("time").setValue(currentTime.toString());
                                bikeRef.child("key").setValue(totalNumber);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }});

                        Toast.makeText(c, c.getString(R.string.cdbk_yep), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(c, c.getString(R.string.cdbk_nop), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception esx) {
                    esx.printStackTrace();
                    Toast.makeText(c, c.getString(R.string.cdbk_nop), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(c, c.getString(R.string.cdbk_nop), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
