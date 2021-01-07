package com.pjt.rebis.ui.qrScan;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pjt.rebis.Authentication.SaveSharedPreference;
import com.pjt.rebis.R;
import com.pjt.rebis.WebAPI.ImplementationAPI;
import com.pjt.rebis.ui.history.RentalItem;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

    /* 'main' fragment for the transaction section for a renter. Here, a renter can insert information about a new rental.
    *   Once the renter select the corresponding button, a QR-Code is immediately spawned for the customer to scan.
    *   In the backend, a rentalitem object is built with information provided, and saved in the database. */
public class QRCodeFragment extends Fragment {
    private String currentuser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_qrscan, container, false);

        currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ImageView image = root.findViewById(R.id.qrc_image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

                    startActivityForResult(intent, 0);

                } catch (Exception e) {

                    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                    startActivity(marketIntent);

                }

            }

        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {
                String rec = data.getStringExtra("SCAN_RESULT");


                String rentalitem = rec.substring(20);
                String[] pieces = rentalitem.split("§");

                boolean completed = blockchainTrx(pieces);
                if (!completed) {
                    Toast toast = Toast.makeText(getActivity(), getString(R.string.trx_failed), Toast.LENGTH_LONG);
                    toast.show();
                }
            }
            if (resultCode == RESULT_CANCELED){
                Toast toast = Toast.makeText(this.getActivity(),  getString(R.string.trx_failed), Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    private boolean blockchainTrx(String[] _given) {
        final DatabaseReference currentRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuser)
                .child("Wallets").child("Current");
        final String[] pieces = _given;
        if (pieces.length < 9)
            return false;

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("RENTALS").child(pieces[4]);
        currentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String address = dataSnapshot.getValue(String.class);

                String customer = SaveSharedPreference.getUserName(getActivity())+"#@&@#"+currentuser;
                int id = Integer.parseInt(pieces[4]);
                int days = Integer.parseInt(pieces[6]);
                double fee = Double.parseDouble(pieces[7]);
                double dep = Double.parseDouble(pieces[8]);

                RentalItem rentalobj = new RentalItem(
                        pieces[0], customer, pieces[2], address, id, pieces[5], days, fee, dep, "rented"
                );

                wannaReallySpend(rentalobj);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});

        return true;
    }

    private void wannaReallySpend(RentalItem givobj) {
        final RentalItem takobj = givobj;
        double price = takobj.getFee() + takobj.getDeposit();
        String body = getString(R.string.al1_msg) + price;

        custom_dialogCR cdcr = new custom_dialogCR(getActivity(), givobj, getString(R.string.al1_tit), body);
        cdcr.show();
    }

}