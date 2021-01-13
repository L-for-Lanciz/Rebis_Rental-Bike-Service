package com.pjt.rebis.ui.qrScan;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.pjt.rebis.ui.profile.custom_dialogOK;

import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Queue;
import java.util.Random;
import java.util.TimeZone;

    /* 'main' fragment for the transaction section. Allows customers to open a qr-code scan activity, which can
    *   read a qr code and call operation to the restAPI.
    *   In the backend, information about the customer are also updated on the database for the corresponding instance. */
public class QRcodeRenter extends Fragment implements AdapterView.OnItemSelectedListener{
    private TextView tdesc, tbike, tfee, tdepo, tdays;
    private EditText etinpfee, etinpdepo, etinpdays;
    private Spinner spinner;
    private ArrayAdapter<String> adapterSpin;
    private Button cmd;
    private ImageView genert;
    private String curAdr;
    private String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private ArrayList<String> bikes;
    private String _parent;

    public QRcodeRenter() {
        bikes = new ArrayList<String>();
        bikes.add("Choose a bike");
        DatabaseReference bikesRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuser).child("Bikes");
        bikesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    if (childSnapshot.child("status").getValue().equals("available")) {
                        String parent = childSnapshot.getKey();
                        _parent = parent;
                        bikes.add(parent.substring(3));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ruttino = inflater.inflate(R.layout.fragment_qrcode_renter, container, false);

        tdesc = ruttino.findViewById(R.id.qrre_desc);
        tbike = ruttino.findViewById(R.id.qrre_bike);
        spinner = ruttino.findViewById(R.id.qrre_spinBike);
        tfee =ruttino.findViewById(R.id.qrre_fee);
        etinpfee = ruttino.findViewById(R.id.qrre_inputfee);
        tdepo =ruttino.findViewById(R.id.qrre_deposit);
        etinpdepo = ruttino.findViewById(R.id.qrre_inputdeposit);
        tdays =ruttino.findViewById(R.id.qrre_days);
        etinpdays = ruttino.findViewById(R.id.qrre_inputdays);
        genert = ruttino.findViewById(R.id.qrre_generator);
        cmd = ruttino.findViewById(R.id.qrre_commnand);

        adapterSpin = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, bikes);
        spinner.setAdapter(adapterSpin);

        cmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            boolean bip = spaceToCreation();
            boolean bop;
            try {
                bop = (getCurrentAddress().length() > 0);
            } catch (Exception dsd) {
                bop = false;
            }
            if (bip && bop)
                generateQR();
            else if (!bop) {
                String error = getString(R.string.cdOK1_msg);
                custom_dialogOK cdok = new custom_dialogOK(getActivity(), 1, error, getString(R.string.cdOK_title));
                cdok.show();
            } else
                Toast.makeText(getContext(), getString(R.string.core_fail), Toast.LENGTH_LONG).show();

            }
        });

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuser).child("Wallets").child("Current");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Addressrenter = dataSnapshot.getValue(String.class);
                setCurrentAddress(Addressrenter);
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
                        "pending",
                        _parent
                );

                String output = obj.getRenter()+"§"+obj.getCustomer()+"§"+obj.getAddressRenter()+"§"+obj.getAddressCustomer()+"§"+obj.getID()+"§"+
                        obj.getDate()+"§"+obj.getDays()+"§"+obj.getFee()+"§"+obj.getDeposit()+"§"+obj.getState()+"§"+obj.getBike()+"§ ";

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

        spinner.setOnItemSelectedListener(this);
        int choice = spinner.getSelectedItemPosition();
        String inpBIKE = bikes.get(choice);

        //String inpBIKE = etinpbike.getText().toString();
        String tempFEE = etinpfee.getText().toString();
            try {
                inpFEE = Double.parseDouble(tempFEE);
            } catch (Exception e) {
                etinpfee.setError(getString(R.string.invalid_number));
                return false;
            }
        String tempDEPO = etinpdepo.getText().toString();
            try {
                inpDEPO = Double.parseDouble(tempDEPO);
            } catch (Exception e) {
                etinpdepo.setError(getString(R.string.invalid_deposit));
            }
        String tempDAYS = etinpdays.getText().toString();
            try {
                inpDAYS = Integer.parseInt(tempDAYS);
            } catch (Exception e) {
                etinpdays.setError(getString(R.string.invalid_number));
                return false;
            }

        if ( choice!=0 && inpFEE >0 && inpDEPO >=0 && inpDAYS >0 ) {
            tdesc.setVisibility(View.INVISIBLE);
            tbike.setVisibility(View.INVISIBLE);
            spinner.setVisibility(View.INVISIBLE);
            tfee.setVisibility(View.INVISIBLE);
            etinpfee.setVisibility(View.INVISIBLE);
            tdepo.setVisibility(View.INVISIBLE);
            etinpdepo.setVisibility(View.INVISIBLE);
            tdays.setVisibility(View.INVISIBLE);
            etinpdays.setVisibility(View.INVISIBLE);
            cmd.setVisibility(View.INVISIBLE);

            genert.setVisibility(View.VISIBLE);

        } else if (choice==0) {
           Toast.makeText(getContext(), "Input a valid bike", Toast.LENGTH_SHORT).show();
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
        dbInp.child("Bike").setValue(obj.getBike());

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

    private void setCurrentAddress(String adr) {
        this.curAdr = adr;
    }

    private String getCurrentAddress() {
        return this.curAdr;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        spinner.getItemAtPosition(pos);
    }

    public void onNothingSelected(AdapterView<?> parent) { // Another interface callback
    }

}