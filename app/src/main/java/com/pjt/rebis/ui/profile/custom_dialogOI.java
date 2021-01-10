package com.pjt.rebis.ui.profile;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.pjt.rebis.R;

    /* This is a custom alert dialog which is spawned when a user completes the 'Online Identification' form. */
public class custom_dialogOI extends Dialog implements android.view.View.OnClickListener {
    public Activity c;
    public Dialog d;
    public Button ok;

    public custom_dialogOI(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        setContentView(R.layout.custom_dialogoi);
        ok = (Button) findViewById(R.id.btn_yes);
        ok.setOnClickListener(this);
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