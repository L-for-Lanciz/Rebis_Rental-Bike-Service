package com.pjt.rebis.Authentication;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.pjt.rebis.Authentication.Login;
import com.pjt.rebis.Authentication.Register;
import com.pjt.rebis.R;

    /* Fragment which handles role selection. When a user asks for sign up, this fragment is casted.
    *   Here, he can choose if he needs a customer or a renter account, then, he will be redirected to the
    *   corresponding register activity. */
public class roleSelector extends Fragment {

    public roleSelector() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vistona = inflater.inflate(R.layout.fragment_role_selector, container, false);

        Button exit = vistona.findViewById(R.id.rose_exit);
        Button ch1 = vistona.findViewById(R.id.rose_ch1);
        Button ch2 = vistona.findViewById(R.id.rose_ch2);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });

        ch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected1();
                exit();
            }
        });

        ch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected2();
                exit();
            }
        });

        return vistona;
    }

    public void selected1() {
        Login.makethedifference = 1;
        Intent regiCus = new Intent(this.getActivity(), Register.class);
        startActivity(regiCus);
    }

    public void selected2() {
        Login.makethedifference = 2;
        Intent regiRen = new Intent(this.getActivity(), RegisterRenter.class);
        startActivity(regiRen);
    }

    private void exit() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

}
