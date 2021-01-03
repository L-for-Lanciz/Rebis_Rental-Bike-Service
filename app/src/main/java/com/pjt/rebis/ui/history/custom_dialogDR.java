package com.pjt.rebis.ui.history;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pjt.rebis.R;

    /* This is a custom alert dialog which spawns when a renter types on a not processed rental item in his history. */
public class custom_dialogDR extends Dialog implements android.view.View.OnClickListener {
    public Activity c;
    public Dialog d;
    public Button yes,no;
    private int idref;

    public custom_dialogDR(Activity a, int dbref) {
        super(a);
        this.c = a;
        this.idref = dbref;
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cddr_btnNo:
                dismiss();
                break;
            case R.id.cddr_btnYes:
                removeFromDB(idref);
                break;
            default:
                break;
        }
        dismiss();
    }

    private void removeFromDB(int _ref) {
        DatabaseReference removeRef = FirebaseDatabase.getInstance().getReference().child("RENTALS").child(_ref+"");
        removeRef.child("Renter").setValue("");
    }
}