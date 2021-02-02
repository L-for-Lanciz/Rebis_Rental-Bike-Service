package com.pjt.rebis.authentication;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.pjt.rebis.R;

public class forgot_YourPassword extends Fragment {

    /* Fragment, accessed from the Login Activity, which allows user to reset their password
    *   in case they lost it. They will need to give their email, and they will receive an email for reset. */
    public forgot_YourPassword() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fypView = inflater.inflate(R.layout.fragment_forgot_your_password, container, false);

        final EditText email = (EditText) fypView.findViewById(R.id.fyp_email);
        Button exit = (Button) fypView.findViewById(R.id.fyp_close);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });

        Button sendMail = (Button) fypView.findViewById(R.id.fyp_reset);
        sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), getString(R.string.fyp_success), Toast.LENGTH_LONG);
                                    exit();
                                } else {
                                    Toast.makeText(getContext(), getString(R.string.fyp_failed), Toast.LENGTH_LONG);
                                }
                            }
                        });
            }
        });

        return fypView;
    }

    private void exit() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

}
