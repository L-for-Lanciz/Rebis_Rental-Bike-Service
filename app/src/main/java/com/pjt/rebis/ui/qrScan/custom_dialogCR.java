package com.pjt.rebis.ui.qrScan;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

//import com.coinbase.android.sdk.OAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pjt.rebis.R;
import com.pjt.rebis.WebAPI.ImplementationAPI;
import com.pjt.rebis.WebAPI.Payload;
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
    private Payload payloadItm;
    public static Payload payloadObjectTMP;
    static final String REDIRECT_URI = "rebis://coinbase-oauth";
    private final String CLIENT_ID = "0ab8763e1079d920050d3316f198736a91572633be35a383bfe2b5972dbf365f";

    public custom_dialogCR(Activity a, Payload pobj, String _title, String _msg) {
        super(a);
        this.c = a;
        this.title = _title;
        this.msg = _msg;
        this.itemR = pobj.getRentalItem();
        this.payloadItm = pobj;
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
                //updRentalOnDatabase(itemR); //this is going to be done on the successful result upon transaction
                transaction(payloadItm);
                break;
            default:
                break;
        }
        dismiss();
    }

    private void transaction(Payload payobk) {
        payloadObjectTMP = payobk;

        // TRUST WALLET
        //Intent login = new Intent(c, Transaction_intent.class);
        //c.startActivity(login);
        //c.finish();

        //COINWALLET
        // Launch the web browser or Coinbase app to authenticate the user.
        try {
        //    OAuth.beginAuthorization(getContext(), CLIENT_ID, "user", REDIRECT_URI, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //LOCAL ENCRYPTION
       // ImplementationAPI api = new ImplementationAPI();
       // api.payTransaction(c, payobk);
    }

}
