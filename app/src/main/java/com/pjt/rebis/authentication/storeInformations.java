package com.pjt.rebis.authentication;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pjt.rebis.R;


    /* This fragment is casted after the RegisterRenter activity is submitted. Here, a renter can provide
    *  information about his store. */
public class storeInformations extends Fragment {
    private EditText owner, country, zip, address, city, phonen;

    public storeInformations() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View megarut = inflater.inflate(R.layout.fragment_store_informations, container, false);

        owner = megarut.findViewById(R.id.stin_inpown);
        country = megarut.findViewById(R.id.stin_inpcoun);
        zip = megarut.findViewById(R.id.stin_inpzip);
        address = megarut.findViewById(R.id.stin_inpaddr);
        city = megarut.findViewById(R.id.stin_inpcity);
        phonen = megarut.findViewById(R.id.stin_inphone);
        Button signup = megarut.findViewById(R.id.stin_signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             boolean goOn = checkValidity();
             if (goOn) {
                 storeDataInDB();
                 goToLogin();
             }

            }
        });

        return megarut;
    }


    private boolean checkValidity() {
        int formg=0;
        try {
            formg = Integer.parseInt(zip.getText().toString());
        } catch (Exception exsds) {
            zip.setError(getString(R.string.stin_errZI));
        }

        if (owner.getText().toString().length()>3 &&
            country.getText().toString().length()>3 &&
            address.getText().toString().length()>3 &&
            zip.getText().toString().length()>3 &&
            formg > 0
        ) {
            return true;
        } else if (owner.getText().toString().length()<3) {
            owner.setError(getString(R.string.stin_errNA));
            return false;
        } else if (country.getText().toString().length()<3) {
            country.setError(getString(R.string.stin_errCO));
            return false;
        } else if (address.getText().toString().length()<3) {
            address.setError(getString(R.string.stin_errAD));
            return false;
        } else if (city.getText().toString().length()<2) {
            city.setError(getString(R.string.stin_errCI));
            return false;
        } else if (phonen.getText().toString().length()<6) {
            phonen.setError(getString(R.string.stin_errNU));
            return false;
        } else if (formg <= 0) {
            zip.setError(getString(R.string.stin_errZI));
            return false;
        } else
            return false;
    }

    private void storeDataInDB() {
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference perdataRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(currentUser).child("PersonalData");
        perdataRef.child("fullname").setValue(owner.getText().toString());
        perdataRef.child("country").setValue(country.getText().toString());
        perdataRef.child("city").setValue(city.getText().toString());
        perdataRef.child("address").setValue(address.getText().toString());
        perdataRef.child("postalcode").setValue(zip.getText().toString());
        perdataRef.child("phonenumber").setValue(phonen.getText().toString());
    }

    private void goToLogin() {
        Intent loggo = new Intent(this.getActivity(), Login.class);
        startActivity(loggo);
        new RegisterRenter().proceedPlease(0);
        getActivity().finish();
    }

}
