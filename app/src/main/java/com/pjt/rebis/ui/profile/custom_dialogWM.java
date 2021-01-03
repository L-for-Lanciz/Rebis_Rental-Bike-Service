package com.pjt.rebis.ui.profile;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pjt.rebis.R;

    /* This is a custom alert dialog which is prompted when a user try to delete an address inside his wallet. */
public class custom_dialogWM extends Dialog implements android.view.View.OnClickListener {
    public Activity c;
    public Dialog d;
    public Button yes,no;
    private String idref, title, msg;

    public custom_dialogWM(Activity a, String dbref, String _title, String _msg) {
        super(a);
        this.c = a;
        this.idref = dbref;
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
                String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuser).child("Wallets").child("List")
                        .child(idref).removeValue();
                break;
            default:
                break;
        }
        dismiss();
    }

}