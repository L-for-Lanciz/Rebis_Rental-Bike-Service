package com.pjt.rebis.ui.profile;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pjt.rebis.R;

import java.util.ArrayList;

    /* Fragment which allows the user to give an address which will be added to his list. */
public class wallet_adder extends Fragment {
    private EditText et_address;
    private DatabaseReference mRef, listRef;

    public wallet_adder() {
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuser).child("Wallets");
        listRef = mRef.child("List");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_wallet_adder, container, false);
        et_address = vista.findViewById(R.id.adwa_address);

        Button bt_exit = vista.findViewById(R.id.adwa_exit);
        bt_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });

        Button bt_add = vista.findViewById(R.id.adwa_add);
        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNew();
            }
        });

        return vista;
    }

    private void addNew() {
        String input = et_address.getText().toString();

        if (input.equals("") || input.equals(" ") || input.contains(" ")) {
            et_address.setError("Input a valid address");
        } else if (input.length() < 40) {
            et_address.setError("Input a valid address");
        } else {
            // Add the given address to the db
            listRef.child(input).child("address").setValue(input);
            exit();
        }
    }

    private void exit() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag1 = new ProfileFragment();
        ft.replace(R.id.prf_constr, frag1, "proffy");
        ft.commit();
    }


}
