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
import com.pjt.rebis.utility.AES;
import com.pjt.rebis.utility.InternalStorage;

import java.util.Random;

/* Fragment which allows the user to give an address which will be added to his list. */
public class wallet_adder extends Fragment {
    private EditText et_address, et_mnemonic;
    private DatabaseReference mRef, listRef;
    private String currentuser;

    public wallet_adder() {
        currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuser).child("Wallets");
        listRef = mRef.child("List");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_wallet_adder, container, false);
        et_address = vista.findViewById(R.id.adwa_address);

        et_mnemonic = vista.findViewById(R.id.adwa_inputMnemonic);

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
        String mnemo = et_mnemonic.getText().toString();

        if (input.length() < 40) {
            et_address.setError("Input a valid address");
        } else if (mnemo.length() < 40) {
            et_mnemonic.setError("Input a valid mnemonic passphrase");
        } else {
            // Add the given address to the db
            listRef.child(input).child("address").setValue(input);
            checkWalletStatus();
            String encryptedMnemo = encryptTheMnemonic(mnemo);
            WalletItem walItem = new WalletItem(input, encryptedMnemo);
            InternalStorage.addToWalletList(getContext(), currentuser, walItem);

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

    private void checkWalletStatus() {
        if (!InternalStorage.doesWalletKeyExist(getContext(), currentuser)) {
            String random = randomString(30);
            String encryptedKey = AES.encrypt(random, InternalStorage.layendarmal);
            InternalStorage.setWalletKey(getContext(), currentuser, encryptedKey);
        }
    }

    private String encryptTheMnemonic(String mnemo) {
        String walletKey = InternalStorage.getWalletKey(getContext(), currentuser);
        String plainKey = AES.decrypt(walletKey, InternalStorage.layendarmal);
        String mnemonic = AES.encrypt(mnemo, plainKey);
        return mnemonic;
    }

    public String randomString(int cap) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmonpqrstuvwxyz1234567890@#$%!?',";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < cap) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }
}
