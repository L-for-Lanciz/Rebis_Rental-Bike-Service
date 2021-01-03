package com.pjt.rebis.ui.history;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pjt.rebis.R;

    /* Fragment casted upon click on a rental item inside the recycler view. This, provides personal information about the
     * renter(store) of the selected rental.   */
public class expandRenterView extends Fragment{
    private String renter, custID;
    private TextView storename, fullname, country, city, zipcode, address, phone;
    private ImageView propic, loading;
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
        TextView title = (TextView) mView.findViewById(R.id.ecv_title);
        title.setText(getString(R.string.ecv_aboutRenter));

        storename = (TextView) mView.findViewById(R.id.ecv_name);
        fullname = (TextView) mView.findViewById(R.id.ecv_usern);
        country = (TextView) mView.findViewById(R.id.ecv_birth);
        city = (TextView) mView.findViewById(R.id.ecv_city);
        zipcode = (TextView) mView.findViewById(R.id.ecv_pcode);
        address = (TextView) mView.findViewById(R.id.ecv_address);
        phone = (TextView) mView.findViewById(R.id.ecv_phone);
        propic = (ImageView) mView.findViewById(R.id.ecv_propic);
        loading = (ImageView) mView.findViewById(R.id.ecv_loading);
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
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nome = dataSnapshot.child("fullname").getValue(String.class);
                storename.setText(renter);
                fullname.setText(nome);
                String paese = dataSnapshot.child("country").getValue(String.class);
                country.setText(paese);
                String citta = dataSnapshot.child("city").getValue(String.class);
                city.setText(citta);
                String codpost = dataSnapshot.child("postalcode").getValue(String.class);
                zipcode.setText(codpost);
                String indirizzo = dataSnapshot.child("address").getValue(String.class);
                address.setText(indirizzo);
                String telefono = dataSnapshot.child("phonenumber").getValue(String.class);
                phone.setText(telefono);
                String immagine = dataSnapshot.child("profileImage").getValue(String.class);
                propic.setImageBitmap(StringToBitMap(immagine));
                loading.setVisibility(View.INVISIBLE);
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

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

}