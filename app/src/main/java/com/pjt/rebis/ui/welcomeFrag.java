package com.pjt.rebis.ui;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pjt.rebis.Authentication.SaveSharedPreference;
import com.pjt.rebis.R;

import org.w3c.dom.Text;

    /* Just an easy welcome fragment which shows the account the user is logged in with, and asks the user
    *  to pick an action.  */
public class welcomeFrag extends Fragment {


    public welcomeFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View lilrut = inflater.inflate(R.layout.fragment_welcome, container, false);

        TextView user = lilrut.findViewById(R.id.welc_user);
        TextView type = lilrut.findViewById(R.id.welc_type);

        String _user = SaveSharedPreference.getUserName(this.getActivity());
        String _type = SaveSharedPreference.getUserType(this.getActivity());

        String pot = getString(R.string.welc_desc) + " " + _user;
        String ato = getString(R.string.welc_type) + " " + _type;

        user.setText(pot);
        type.setText(ato);

        return lilrut;
    }

}