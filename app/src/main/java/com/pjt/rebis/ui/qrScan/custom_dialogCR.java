package com.pjt.rebis.ui.qrScan;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pjt.rebis.R;
import com.pjt.rebis.WebAPI.ImplementationAPI;
import com.pjt.rebis.ui.history.RentalItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/* This is a custom alert dialog which spawns when a customer scans a valid QR-Code. */
public class custom_dialogCR extends Dialog implements android.view.View.OnClickListener{
    public Activity c;
    public Dialog d;
    public Button yes,no;
    private String msg, title;
    private RentalItem itemR;
    private Calendar cal = Calendar.getInstance();
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    public custom_dialogCR(Activity a, RentalItem robj, String _title, String _msg) {
        super(a);
        this.c = a;
        this.title = _title;
        this.msg = _msg;
        this.itemR = robj;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                updRentalOnDatabase(itemR);
                transaction(itemR);
                break;
            default:
                break;
        }
        dismiss();
    }

    private void updRentalOnDatabase(RentalItem obj) {
        int rid = obj.getID();
        String customer[] = obj.getCustomer().split("#@&@#");
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("RENTALS").child(rid+"");
        mDatabase.child("State").setValue("rented");

        mDatabase.child("Customer").setValue(obj.getCustomer());
        mDatabase.child("Addresscustomer").setValue(obj.getAddressCustomer());

        String[] tmp = obj.getRenter().split("#@&@#");
        final DatabaseReference mBikeRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(tmp[1])
                .child("Bikes").child(obj.getBike());
        mBikeRef.child("status").setValue("unavailable");
        try {
            Date date1 = new SimpleDateFormat("yyyy/MM/dd").parse(obj.getDate());
            cal.setTime(date1);
            cal.add(Calendar.DAY_OF_MONTH, obj.getDays());
            Date finalDate = cal.getTime();
            mBikeRef.child("customer").setValue(customer[0]+"#@&@#"+dateFormat.format(finalDate));
        } catch (Exception e) { }
        mBikeRef.child("rentedCNT").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer scnt = dataSnapshot.getValue(Integer.class);
                try {
                    //int cnt = Integer.parseInt(scnt);
                    mBikeRef.child("rentedCNT").setValue(scnt+1);
                } catch (Exception fd) {
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});
    }

    private void transaction(RentalItem rentobk) {
        ImplementationAPI api = new ImplementationAPI();
        api.post(c, rentobk);
    }

}
