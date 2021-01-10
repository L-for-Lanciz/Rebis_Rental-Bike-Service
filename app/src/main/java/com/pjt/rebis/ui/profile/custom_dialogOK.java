package com.pjt.rebis.ui.profile;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.pjt.rebis.R;

public class custom_dialogOK extends Dialog implements android.view.View.OnClickListener {
    public Activity c;
    public Dialog d;
    public Button ok;
    private int howFar;
    private String error, iltitle;

    public custom_dialogOK(Activity a, int _howFar, String _error, String _title) {
        super(a);
        this.c = a;
        this.howFar = _howFar;
        this.error = _error;
        this.iltitle = _title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        setContentView(R.layout.custom_dialogoi);
        ok = (Button) findViewById(R.id.btn_yes);
        ok.setOnClickListener(this);

        TextView title = (TextView) findViewById(R.id.cduppername);
        TextView msg = (TextView) findViewById(R.id.txt_dia);

        title.setText(iltitle);
        msg.setText(error);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                dismiss();
                c.onBackPressed();
                break;
            default:
                break;
        }
        dismiss();
    }
}