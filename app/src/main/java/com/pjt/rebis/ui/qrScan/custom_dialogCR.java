package com.pjt.rebis.ui.qrScan;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.pjt.rebis.R;

/* This is a custom alert dialog which spawns when a customer scans a valid QR-Code. */
public class custom_dialogCR extends Dialog implements android.view.View.OnClickListener{
    public Activity c;
    public Dialog d;
    public Button yes,no;
    private String msg, title;
    public custom_dialogCR(Activity a, String _title, String _msg) {
        super(a);
        this.c = a;
        this.title = _title;
        this.msg = _msg;
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

                break;
            default:
                break;
        }
        dismiss();
    }

}
