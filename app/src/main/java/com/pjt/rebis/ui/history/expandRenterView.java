package com.pjt.rebis.ui.history;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pjt.rebis.R;
import com.squareup.picasso.Picasso;

/* Fragment casted upon click on a rental item inside the recycler view. This, provides personal information about the
     * renter(store) of the selected rental.   */
public class expandRenterView extends Fragment{
    private String renter, custID;
    private TextView storename, fullname, country, city, zipcode, address, phone;
    private ImageView propic;
    private Button close, endr;

    public expandRenterView(String _renter) {
        String[] tmp = _renter.split("#@&@#");
        this.renter = tmp[0];
        this.custID = tmp[1];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView =  inflater.inflate(R.layout.fragment_expand_customer_view, container, false);

        storename = (TextView) mView.findViewById(R.id.ecv_name);
        fullname = (TextView) mView.findViewById(R.id.ecv_usern);
        country = (TextView) mView.findViewById(R.id.ecv_birth);
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
        endr= (Button) mView.findViewById(R.id.ecv_ENDRENTAL);
        endr.setVisibility(View.INVISIBLE);

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(custID).child("PersonalData");
        DatabaseReference nomeRef = mRef.child("fullname");
        DatabaseReference paeseRef = mRef.child("country");
        DatabaseReference cittaRef = mRef.child("city");
        DatabaseReference codeRef = mRef.child("postalCode");
        DatabaseReference addressRef = mRef.child("address");
        DatabaseReference numberRef = mRef.child("phonenumber");
        DatabaseReference imageRef = mRef.child("profileImage");

        nomeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nome = dataSnapshot.getValue(String.class);
                fullname.setText(nome);
                storename.setText(renter);
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
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.navigation_history);
    }

}
