package com.pjt.rebis.ui.history;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pjt.rebis.R;
import com.pjt.rebis.utility.AES;
import com.pjt.rebis.utility.InternalStorage;
import com.pjt.rebis.utility.SaveSharedPreference;
import com.pjt.rebis.webAPI.ImplementationAPI;
import com.pjt.rebis.webAPI.Payload;
import com.pjt.rebis.webAPI.Transaction_metamask;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;

/* Fragment casted upon click on a rental item inside the recycler view. This, provides personal information about the
    *  customer of the selected rental. */
public class expandCustomerView extends Fragment {
    private String customer, custID, bike;
    private int rID;
    private TextView fullname, username, birth, country, city, zipcode, address, phone;
    private ImageView propic;
    private Button close, endr;
    private String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private RentalItem ritm;

    public expandCustomerView() {}

    public expandCustomerView(String _customer, int _id, String _bike) {
        String[] tmp = _customer.split("#@&@#");
        this.customer = tmp[0];
        this.custID = tmp[1];
        this.rID =_id;
        this.bike = _bike;
    }

    public expandCustomerView(RentalItem _ritm) {
        this.ritm = _ritm;
        String[] tmp = ritm.getCustomer().split("#@&@#");
        this.customer = tmp[0];
        this.custID = tmp[1];
        this.rID = ritm.getID();
        this.bike = ritm.getBike();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView =  inflater.inflate(R.layout.fragment_expand_customer_view, container, false);

        fullname = (TextView) mView.findViewById(R.id.ecv_name);
        username = (TextView) mView.findViewById(R.id.ecv_usern);
        birth = (TextView) mView.findViewById(R.id.ecv_birth);
            birth.setPaintFlags(birth.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        country = (TextView) mView.findViewById(R.id.ecv_country);
            country.setPaintFlags(country.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        city = (TextView) mView.findViewById(R.id.ecv_city);
            city.setPaintFlags(city.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        zipcode = (TextView) mView.findViewById(R.id.ecv_pcode);
            zipcode.setPaintFlags(zipcode.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        address = (TextView) mView.findViewById(R.id.ecv_address);
            address.setPaintFlags(address.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        phone = (TextView) mView.findViewById(R.id.ecv_phone);
            phone.setPaintFlags(phone.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        propic = (ImageView) mView.findViewById(R.id.ecv_propic);
        close = (Button) mView.findViewById(R.id.ecv_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFrag();
            }
        });
        endr = (Button) mView.findViewById(R.id.ecv_ENDRENTAL);
        endr.setVisibility(View.VISIBLE);

        if (rID == 0)
            endr.setVisibility(View.INVISIBLE);
        else {
            endr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   endOfTransaction();
                }
            });
        }

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(custID).child("PersonalData");
        DatabaseReference nomeRef = mRef.child("name");
        DatabaseReference cognomeRef = mRef.child("surname");
        DatabaseReference etaRef = mRef.child("birthdate");
        DatabaseReference paeseRef = mRef.child("country");
        DatabaseReference cittaRef = mRef.child("city");
        DatabaseReference codeRef = mRef.child("postalCode");
        DatabaseReference addressRef = mRef.child("homeAddress");
        DatabaseReference numberRef = mRef.child("phoneNumber");
        DatabaseReference imageRef = mRef.child("profileImage");

        nomeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nome = dataSnapshot.getValue(String.class);
                fullname.setText(nome);
                username.setText(customer);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});

        cognomeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nomecomp = fullname.getText() +" "+dataSnapshot.getValue(String.class);
                fullname.setText(nomecomp);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});

        etaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nascita = dataSnapshot.getValue(String.class);
                birth.setText(nascita);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});

        paeseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String paese = dataSnapshot.getValue(String.class);
                country.setText(paese);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});

        cittaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String citta = dataSnapshot.getValue(String.class);
                city.setText(citta);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});

        codeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String codpost = dataSnapshot.getValue(String.class);
                zipcode.setText(codpost);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});

        addressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String indirizzo = dataSnapshot.getValue(String.class);
                address.setText(indirizzo);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});

        numberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String telefono = dataSnapshot.getValue(String.class);
                phone.setText(telefono);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});

        imageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String immagine = dataSnapshot.getValue(String.class);
                Uri tmp = Uri.parse(immagine);
                Picasso.get().load(tmp).resize(600,600).onlyScaleDown().into(propic);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});

        return mView;
    }

    private void endOfTransaction() {
        ImplementationAPI api = new ImplementationAPI();
        String mnemo =""; //getMnemonic(ritm.getAddressRenter());

        Payload pitm = new Payload(ritm, mnemo);

        if (ritm.getDeposit() == 0)
            api.endTransaction(getContext(), pitm, currentuser);
        else {
            //METAMASK DEEPLINK
            String contract_address = "0xB5b86278B448922CfA09C11B6a27B22c27Aa80b3";
            double value = pitm.getRentalItem().getDeposit();
            String DEEP_LINK_URL = "https://metamask.app.link/send/pay-" + contract_address + "@3?value=SPLIT" + value;// + "e18";

            String[] array = new String[] {
                    pitm.getRentalItem().getRenter(),
                    pitm.getRentalItem().getCustomer(),
                    pitm.getRentalItem().getAddressRenter(),
                    pitm.getRentalItem().getAddressCustomer(),
                    pitm.getRentalItem().getID()+"",
                    pitm.getRentalItem().getDate(),
                    pitm.getRentalItem().getDays()+"",
                    pitm.getRentalItem().getFee()+"",
                    pitm.getRentalItem().getDeposit()+"",
                    pitm.getRentalItem().getState(),
                    pitm.getRentalItem().getBike(),
                    pitm.getMnemonic()
            };

            Intent trxInt = new Intent(getContext(), Transaction_metamask.class);
            trxInt.putExtra("DEEPLINK", DEEP_LINK_URL);
            trxInt.putExtra("PAYLOAD_OBJECT", array);
            trxInt.putExtra("TYPE", "END");
            getActivity().startActivity(trxInt);
            getActivity().finish();
        }

        closeFrag();
    }

    private void closeFrag() {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.navigation_history);
    }

/*
    private String getMnemonic(String address) {
        String encryptedKey = InternalStorage.getWalletKey(getContext(), currentuser);
        String walletKey = AES.decrypt(encryptedKey, InternalStorage.layendarmal);
        String encryptedMnemo = InternalStorage.getWalletList(getContext(), currentuser).get(address);
        String mnemo = AES.decrypt(encryptedMnemo, walletKey);
        return mnemo;
    }
*/
}
