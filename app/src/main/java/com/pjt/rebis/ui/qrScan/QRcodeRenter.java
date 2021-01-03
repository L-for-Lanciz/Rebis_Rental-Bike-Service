package com.pjt.rebis.ui.qrScan;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.pjt.rebis.Authentication.SaveSharedPreference;
import com.pjt.rebis.R;
import com.pjt.rebis.ui.history.RentalItem;

import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

    /* 'main' fragment for the transaction section. Allows customers to open a qr-code scan activity, which can
    *   read a qr code and call operation to the restAPI.
    *   In the backend, information about the customer are also updated on the database for the corresponding instance. */
public class QRcodeRenter extends Fragment {
    private TextView tdesc, tbike, tfee, tdepo, tdays;
    private EditText etinpbike, etinpfee, etinpdepo, etinpdays;
    private Button cmd;
    private ImageView genert;
    private String curAdr;
    private String currentuser;

    public QRcodeRenter() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ruttino = inflater.inflate(R.layout.fragment_qrcode_renter, container, false);

        tdesc = ruttino.findViewById(R.id.qrre_desc);
        tbike = ruttino.findViewById(R.id.qrre_bike);
        etinpbike = ruttino.findViewById(R.id.qrre_inputbike);
        tfee =ruttino.findViewById(R.id.qrre_fee);
        etinpfee = ruttino.findViewById(R.id.qrre_inputfee);
        tdepo =ruttino.findViewById(R.id.qrre_deposit);
        etinpdepo = ruttino.findViewById(R.id.qrre_inputdeposit);
        tdays =ruttino.findViewById(R.id.qrre_days);
        etinpdays = ruttino.findViewById(R.id.qrre_inputdays);
        genert = ruttino.findViewById(R.id.qrre_generator);
        cmd = ruttino.findViewById(R.id.qrre_commnand);

        cmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            boolean bip = spaceToCreation();
            if (bip)
                generateQR();
            else
                Toast.makeText(getContext(), getString(R.string.core_fail), Toast.LENGTH_LONG);

            }
        });

        currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuser).child("Wallets").child("Current");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Addressrenter = dataSnapshot.getValue(String.class);
                getCurrentAddress(Addressrenter);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});

        return ruttino;
    }

    private void generateQR() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("RENTALS");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int xy = (int) dataSnapshot.getChildrenCount();

                QRCodeWriter writer = new QRCodeWriter();
                String randombefore = randomString(15);
                String randomafter = randomString(15);

                Date date = new Date();
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
                cal.setTime(date);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH)+1;
                int day = cal.get(Calendar.DAY_OF_MONTH);
                String data = year+"/"+month+"/"+day;

                int days;
                double fee;
                double depo;

                try {
                    days = Integer.parseInt(etinpdays.getText().toString());
                    fee = Double.parseDouble(etinpfee.getText().toString());
                    if (etinpdepo.getText().toString().equals(""))
                        depo = 0;
                    else
                        depo = Double.parseDouble(etinpdepo.getText().toString());
                } catch (Exception eddd) {
                    eddd.printStackTrace();
                    days=0;
                    fee=0;
                    depo=0;
                }

                RentalItem obj = new RentalItem(
                        SaveSharedPreference.getUserName(getActivity())+"#@&@#"+currentuser,
                        "void",
                        curAdr,
                        "void",
                        xy+1,
                        data,
                        days,
                        fee,
                        depo,
                        "pending"
                );

                String output = obj.getRenter()+"§"+obj.getCustomer()+"§"+obj.getAddressRenter()+"§"+obj.getAddressCustomer()+"§"+obj.getID()+"§"+
                        obj.getDate()+"§"+obj.getDays()+"§"+obj.getFee()+"§"+obj.getDeposit()+"§"+obj.getState()+" ";

                createRentalOnDB(obj);

                try {
                    BitMatrix bitMatrix = writer.encode( randombefore+output+randomafter, BarcodeFormat.QR_CODE, 512, 512);
                    int width = bitMatrix.getWidth();
                    int height = bitMatrix.getHeight();
                    Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                        }
                    }
                    genert.setImageBitmap(bmp);
                } catch (WriterException e) { }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});

    }

    private boolean spaceToCreation() {
        double inpFEE=0, inpDEPO=0;
        int inpDAYS=0;

        String inpBIKE = etinpbike.getText().toString();
        String tempFEE = etinpfee.getText().toString();
            try {
                inpFEE = Double.parseDouble(tempFEE);
            } catch (Exception e) {
                etinpfee.setError("Input a valid number");
                return false;
            }
        String tempDEPO = etinpdepo.getText().toString();
            try {
                inpDEPO = Double.parseDouble(tempDEPO);
            } catch (Exception e) {
                etinpdepo.setError("Input a valid number");
            }
        String tempDAYS = etinpdays.getText().toString();
            try {
                inpDAYS = Integer.parseInt(tempDAYS);
            } catch (Exception e) {
                etinpdays.setError("Input a valid number");
                return false;
            }

        if ( inpBIKE.length()>2 && inpFEE >0 && inpDEPO >=0 && inpDAYS >0 ) {
            tdesc.setVisibility(View.INVISIBLE);
            tbike.setVisibility(View.INVISIBLE);
            etinpbike.setVisibility(View.INVISIBLE);
            tfee.setVisibility(View.INVISIBLE);
            etinpfee.setVisibility(View.INVISIBLE);
            tdepo.setVisibility(View.INVISIBLE);
            etinpdepo.setVisibility(View.INVISIBLE);
            tdays.setVisibility(View.INVISIBLE);
            etinpdays.setVisibility(View.INVISIBLE);
            cmd.setVisibility(View.INVISIBLE);

            genert.setVisibility(View.VISIBLE);

        } else if (inpBIKE.length()<= 2) {
            etinpbike.setError("Input a valid name");
            return false;
        } else if (inpFEE <= 0) {
            etinpfee.setError("Input a valid number");
            return false;
        } else if (inpDEPO < 0) {
            etinpdepo.setError("Input a valid number");
            return false;
        } else if (inpDAYS <= 0) {
            etinpdays.setError("Input a valid number");
            return false;
        }
        return true;
    }

    private void createRentalOnDB(RentalItem obj) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("RENTALS");

        DatabaseReference dbInp = mDatabase.child(obj.getID()+"");
        dbInp.child("Renter").setValue(obj.getRenter());
        dbInp.child("Customer").setValue(obj.getCustomer());
        dbInp.child("Addressrenter").setValue(obj.getAddressRenter());
        dbInp.child("Addresscustomer").setValue(obj.getAddressCustomer());
        dbInp.child("ID").setValue(obj.getID());
        dbInp.child("Date").setValue(obj.getDate());
        dbInp.child("Days").setValue(obj.getDays());
        dbInp.child("Fee").setValue(obj.getFee());
        dbInp.child("Deposit").setValue(obj.getDeposit());
        dbInp.child("State").setValue(obj.getState());

    }

    public String randomString(int cap) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmonpqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < cap) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    private void getCurrentAddress(String adr) {
        curAdr = adr;
    }

}