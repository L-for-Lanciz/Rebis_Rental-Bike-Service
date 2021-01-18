package com.pjt.rebis.ui.history;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.squareup.picasso.Picasso;

/* Fragment casted upon click on a rental item inside the recycler view. This, provides personal information about the
    *  customer of the selected rental. */
public class expandCustomerView extends Fragment {
    private String customer, custID, bike;
    private int rID;
    private TextView fullname, username, birth, country, city, zipcode, address, phone;
    private ImageView propic;
    private Button close, endr;
    String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public expandCustomerView() {}

    public expandCustomerView(String _customer, int _id, String _bike) {
        String[] tmp = _customer.split("#@&@#");
        this.customer = tmp[0];
        this.custID = tmp[1];
        this.rID =_id;
        this.bike = _bike;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView =  inflater.inflate(R.layout.fragment_expand_customer_view, container, false);

        fullname = (TextView) mView.findViewById(R.id.ecv_name);
        username = (TextView) mView.findViewById(R.id.ecv_usern);
        birth = (TextView) mView.findViewById(R.id.ecv_birth);
        country = (TextView) mView.findViewById(R.id.ecv_country);
        city = (TextView) mView.findViewById(R.id.ecv_city);
        zipcode = (TextView) mView.findViewById(R.id.ecv_pcode);
        address = (TextView) mView.findViewById(R.id.ecv_address);
        phone = (TextView) mView.findViewById(R.id.ecv_phone);
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
                    DatabaseReference endRef = FirebaseDatabase.getInstance().getReference().child("RENTALS").child(rID + "");
                    endRef.child("State").setValue("ended");

                    DatabaseReference bikeRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuser)
                            .child("Bikes").child(bike);
                    bikeRef.child("status").setValue("available");
                    bikeRef.child("customer").setValue("");
                    closeFrag();
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

    private void closeFrag() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag4 = new HistoryFragment();
        ft.replace(R.id.his_const, frag4, "hisfrag");
        ft.commit();
    }
}
