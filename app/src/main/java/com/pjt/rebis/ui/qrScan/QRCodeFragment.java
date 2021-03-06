package com.pjt.rebis.ui.qrScan;

import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pjt.rebis.utility.AES;
import com.pjt.rebis.utility.InternalStorage;
import com.pjt.rebis.utility.SaveSharedPreference;
import com.pjt.rebis.R;
import com.pjt.rebis.webAPI.Payload;
import com.pjt.rebis.ui.history.RentalItem;
import com.pjt.rebis.ui.profile.custom_dialogOK;

import org.web3j.contracts.token.ERC20BasicInterface;
import org.web3j.protocol.core.methods.response.EthGetBalance;

import java.math.BigDecimal;
import java.math.BigInteger;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

    /* 'main' fragment for the transaction section for a renter. Here, a renter can insert information about a new rental.
    *   Once the renter select the corresponding button, a QR-Code is immediately spawned for the customer to scan.
    *   In the backend, a rentalitem object is built with information provided, and saved in the database. */
public class QRCodeFragment extends Fragment {
    private String currentuser;
    private boolean ok1, ok2;
    private TextView address;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_qrscan, container, false);

        currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        address = root.findViewById(R.id.qrc_address);
        setCurrent();

        areYouOK1();
        areYouOK2();

        Toolbar myToolbar = (Toolbar) getActivity().findViewById(R.id.myToolbar);
        TextView textView = (TextView) myToolbar.findViewById(R.id.toolbarTextView);
        textView.setText(getString(R.string.title_QRScan));

        ImageView image = root.findViewById(R.id.qrc_image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getOK1() && getOK2()) {
                    try {
                        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                        intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
                        startActivityForResult(intent, 0);

                    } catch (Exception e) {
                        Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                        startActivity(marketIntent);

                    }

                } else if (!getOK1()) {
                    String error = getString(R.string.cdOK1_msg);
                    custom_dialogOK cdok = new custom_dialogOK(getActivity(), 1, error, getString(R.string.cdOK_title));
                    cdok.show();
                } else if (!getOK2()) {
                    String error = getString(R.string.cdOK2_msg);
                    custom_dialogOK cdok = new custom_dialogOK(getActivity(), 2, error, getString(R.string.cdOK_title));
                    cdok.show();
                } else {
                    String error = getString(R.string.cdOK3_msg);
                    custom_dialogOK cdok = new custom_dialogOK(getActivity(), 3, error, getString(R.string.cdOK_title));
                    cdok.show();
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
                String bike = pieces[10];

                RentalItem rentalobj = new RentalItem (
                        pieces[0], customer, pieces[2], address, id, pieces[5], days, fee, dep, "rented", bike
                );

                wannaReallySpend(rentalobj);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});

        return true;
    }

    private void areYouOK1() {
        DatabaseReference curadrRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuser).child("Wallets");
        curadrRef.child("Current").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String addrxcur = dataSnapshot.getValue(String.class);
                if (addrxcur.length()>0)
                    setOK1(true);
                else
                    setOK1(false);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});
    }

    private void areYouOK2() {
        String sspOID = SaveSharedPreference.getIdentified(getContext());
        if (sspOID.equals("")) {
            DatabaseReference curadrRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuser).child("Credentials");
            curadrRef.child("identification").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String oidgone = dataSnapshot.getValue(String.class);
                    try {
                        if (oidgone.length() > 0)
                            setOK2(true);
                        else
                            setOK2(false);
                    } catch (Exception edfef) {
                        setOK2(false);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else {
            setOK2(true);
        }

    }

    private void wannaReallySpend(RentalItem givobj) {
        final RentalItem takobj = givobj;
        double price = takobj.getFee() + takobj.getDeposit();
        String body = getString(R.string.al1_msg) + price;

        String mnemo = "bip";//getMnemonic(givobj.getAddressCustomer());
        Payload payloadobj = new Payload(givobj, mnemo);
        custom_dialogCR cdcr = new custom_dialogCR(getActivity(), payloadobj, getString(R.string.al1_tit), body);
        cdcr.show();
    }

    private void setCurrent() {
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference currentRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuser).child("Wallets").child("Current");
        currentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    String add = dataSnapshot.getValue(String.class);
                    String conctn = (add.substring(0, 8)).concat("...").concat(add.substring(34, 42));
                    if (add != null)
                        address.setText(conctn);
                    else
                        address.setText(getString(R.string.emptyadd));
                } catch(Exception e) {
                    address.setText(getString(R.string.emptyadd));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});
    }

    private void setOK1(boolean puffo) {
        this.ok1 = puffo;
    }

    private boolean getOK1() {
        return this.ok1;
    }

    private void setOK2(boolean puffo) {
        this.ok2 = puffo;
    }

    private boolean getOK2() {
        return this.ok2;
    }

}