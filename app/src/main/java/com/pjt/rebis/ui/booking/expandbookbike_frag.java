package com.pjt.rebis.ui.booking;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.pjt.rebis.R;
import com.pjt.rebis.ui.bikes.Bicicletta;
import com.pjt.rebis.utility.SaveSharedPreference;
import java.util.ArrayList;
import java.util.HashMap;

public class expandbookbike_frag extends Fragment {
    private String key, owner;
    private HashMap<String, Bicicletta> bikes;
    private Button exit;
    private CustomAdapter adapter;
    private TextView titolare, stelle;
    private double stars;

    public expandbookbike_frag() {
        // Required empty public constructor
    }

    public expandbookbike_frag(String key, HashMap<String, Bicicletta> bikes, String owner, double stars) {
        this.key = key;
        this.bikes = bikes;
        this.owner = owner;
        this.stars = stars;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragView = inflater.inflate(R.layout.fragment_expandbookbike_frag, container, false);

        titolare = fragView.findViewById(R.id.exbo_store);
        titolare.setText(owner);
        stelle = fragView.findViewById(R.id.exbo_rate);
        stelle.setText(String.valueOf(stars));

        exit = fragView.findViewById(R.id.exbo_close);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closefrag();
            }
        });

        final RatingBar ratingBar = fragView.findViewById(R.id.exbo_ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                double rat = ratingBar.getRating();
                updateRating(rat);
            }
        });

        ArrayList<Bicicletta> mData = new ArrayList<>(bikes.values());
        ArrayList<Bicicletta> data = new ArrayList<>();
        for (Bicicletta bici : mData) {
            if(bici.getStatus().equals("available"))
                data.add(bici);
        }

        RecyclerView recyclerView = fragView.findViewById(R.id.exbo_Recyc);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CustomAdapter(getActivity(), getContext(), data, key, owner);
        recyclerView.setAdapter(adapter);

        return fragView;
    }

    private void updateRating(double _rat) {
        final double rat = _rat;
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("USERS")
                .child(key).child("Ratings");
        final DatabaseReference datRef = databaseReference.child(SaveSharedPreference.getUserName(getActivity()));

        datRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Double isThere = dataSnapshot.getValue(Double.class);

                if (isThere==null) {

                    databaseReference.child("Votes").runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            Integer currentValue = mutableData.getValue(Integer.class);
                            if (currentValue == null) {
                                mutableData.setValue(1);
                            } else {
                                mutableData.setValue(currentValue + 1);
                            }
                            return Transaction.success(mutableData);
                        }
                        @Override
                        public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                            System.out.println("Transaction completed");
                        }
                    });

                    databaseReference.child("Sum").runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            Integer currentValue = mutableData.getValue(Integer.class);
                            if (currentValue == null) {
                                mutableData.setValue(rat);
                            } else {
                                double valueToStore = currentValue + rat;
                                if (valueToStore > 0)
                                    mutableData.setValue(valueToStore);
                                else
                                    mutableData.setValue(0);
                            }
                            return Transaction.success(mutableData);
                        }
                        @Override
                        public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                            //
                        }
                    });

                    datRef.setValue(rat);

                } else {

                    datRef.setValue(rat);

                    databaseReference.child("Sum").runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            Integer currentValue = mutableData.getValue(Integer.class);
                            if (currentValue == null) {
                                mutableData.setValue(rat);
                            } else {
                                double newValue = rat - isThere;
                                double valueToStore = currentValue + newValue;
                                if (valueToStore > 0)
                                    mutableData.setValue(valueToStore);
                                else
                                    mutableData.setValue(0);
                            }
                            return Transaction.success(mutableData);
                        }
                        @Override
                        public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                            //
                        }
                    });

                }

                Toast.makeText(getActivity(), getString(R.string.exbo_stars), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), getString(R.string.cdbk_nop), Toast.LENGTH_SHORT).show();
            }
        });


        //databaseReference.child(SaveSharedPreference.getUserName(getActivity())).setValue(rat);
    }

    private void closefrag() {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.navigation_booking);
    }

}
