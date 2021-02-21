package com.pjt.rebis.ui.qrScan;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.pjt.rebis.R;
import com.pjt.rebis.utility.SaveSharedPreference;
import com.pjt.rebis.webAPI.Payload;
import com.pjt.rebis.ui.history.RentalItem;
import com.pjt.rebis.webAPI.Transaction_metamask;

/* This is a custom alert dialog which spawns when a customer scans a valid QR-Code. */
public class custom_dialogCR extends Dialog implements android.view.View.OnClickListener{
    public Activity c;
    public Dialog d;
    public Button yes,no;
    private String msg, title;
    private RentalItem itemR;
    private Payload payloadItm;
    //public static Payload payloadObjectTMP;

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

    private void transaction(Payload payobj) {
        //payloadObjectTMP = payobj;

        // TRUST WALLET
       //Intent login = new Intent(c, Transaction_intent.class);
       //c.startActivity(login);
       //c.finish();

        // LOCAL ENCRYPTION
        //ImplementationAPI api = new ImplementationAPI();
        //api.payTransaction(c, payobk);

        //METAMASK DEEPLINK
        String contract_address = "0x86e43bf70244816Df7DA68471daB0C68f5A553D8";

        double value = payobj.getRentalItem().getFee() + payobj.getRentalItem().getDeposit();
        String DEEP_LINK_URL = "https://metamask.app.link/send/pay-" + contract_address + "@3?value=SPLIT" + value;// + "e18";

        String[] array = new String[] {
                payobj.getRentalItem().getRenter(),
                payobj.getRentalItem().getCustomer(),
                payobj.getRentalItem().getAddressRenter(),
                payobj.getRentalItem().getAddressCustomer(),
                payobj.getRentalItem().getID()+"",
                payobj.getRentalItem().getDate(),
                payobj.getRentalItem().getDays()+"",
                payobj.getRentalItem().getFee()+"",
                payobj.getRentalItem().getDeposit()+"",
                payobj.getRentalItem().getState(),
                payobj.getRentalItem().getBike(),
            payobj.getMnemonic()
        };

        Intent trxInt = new Intent(c, Transaction_metamask.class);
        trxInt.putExtra("DEEPLINK", DEEP_LINK_URL);
        trxInt.putExtra("PAYLOAD_OBJECT", array);
        c.startActivity(trxInt);
        c.finish();
    }

}
