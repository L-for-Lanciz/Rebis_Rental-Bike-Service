package com.pjt.rebis.ui.booking;

import android.app.Activity;
import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.pjt.rebis.R;
import com.pjt.rebis.ui.bikes.Bicicletta;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.pjt.rebis.MainActivity.current_location;

public class bike_booking extends Fragment {
    private FirebaseRecyclerAdapter<User, PassViewHolder> mPeopleRVAdapter;
    private static int sadred, lightgreen, stronggreen, arancius, yellou;

    public bike_booking() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View dviu =  inflater.inflate(R.layout.fragment_bike_booking, container, false);

        Toolbar myToolbar = (Toolbar) getActivity().findViewById(R.id.myToolbar);
        TextView textView = (TextView) myToolbar.findViewById(R.id.toolbarTextView);
        textView.setText(getString(R.string.title_booking));

        sadred = ContextCompat.getColor(getContext(), R.color.sadred);
        arancius = ContextCompat.getColor(getContext(), R.color.arancius);
        yellou = ContextCompat.getColor(getContext(), R.color.yellou);
        lightgreen = ContextCompat.getColor(getContext(), R.color.lightgreen);
        stronggreen = ContextCompat.getColor(getContext(), R.color.strong_green);

        RecyclerView mPeopleRV;

        mPeopleRV = (RecyclerView) dviu.findViewById(R.id.bok_Recyc);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");
        Query personsQuery = mDatabase.orderByValue();
        mDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        mPeopleRV.setLayoutManager(linearLayoutManager);

        final FirebaseRecyclerOptions personsOptions = new FirebaseRecyclerOptions.Builder<User>().setQuery(personsQuery, User.class).build();

        mPeopleRVAdapter = new FirebaseRecyclerAdapter<User, PassViewHolder>(personsOptions) {
            @Override
            protected void onBindViewHolder(final PassViewHolder holder, final int position, final User model) {
                try {
                    Credentials credRef = model.getCredentials();

                    if (credRef.getUsertype().equals("renter")) {
                        PersonalData persRef = model.getPersonalData();
                        Ratings rateRef = model.getRatings();
                        HashMap<String, Bicicletta> biciRef = model.getBikes();

                        holder.setPersonalData(credRef.getUsername());
                        holder.setCredentials(persRef.getCity(), persRef.getAddress(), persRef.getPhonenumber());

                        String _locationname = persRef.getCity().concat(" ").concat(persRef.getAddress());
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        try {
                            List<android.location.Address> indirizziPossibili = geocoder.getFromLocationName(_locationname, 1);
                            final Location locationTo = new Location(LocationManager.GPS_PROVIDER);
                            locationTo.setLatitude(indirizziPossibili.get(0).getLatitude());
                            locationTo.setLongitude(indirizziPossibili.get(0).getLongitude());
                            float distx = current_location.distanceTo(locationTo);
                            holder.setDistance(distx);

                       } catch (Exception ecs) {
                           ecs.printStackTrace();
                       }

                        holder.setRating(rateRef.getSum(), rateRef.getVotes());

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //todo
                            }
                        });

                    } else {
                        holder.itemRemoveVisibility();
                    }
                } catch (Exception crash) {
                    crash.printStackTrace();
                }
            }

            @Override
            public PassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.booking_item, parent, false);

                return new PassViewHolder(view);
            }
        };

        mPeopleRV.setAdapter(mPeopleRVAdapter);

        return dviu;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPeopleRVAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPeopleRVAdapter.stopListening();
    }

    public static class PassViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public PassViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setCredentials(String _city, String _addrx, String _phone) {
            TextView city = (TextView) mView.findViewById(R.id.bok_city);
            TextView addrx = (TextView) mView.findViewById(R.id.bok_addrx);
            TextView phone = (TextView) mView.findViewById(R.id.bok_phone);
            city.setText(_city);
            addrx.setText(_addrx);
            phone.setText(_phone);
        }

        public void setRating(double sum, int numb) {
            TextView rating = (TextView) mView.findViewById(R.id.bok_currating);
            double totalrate = round(sum/numb, 1);

            if (totalrate < 2) {
                rating.setTextColor(sadred);
                rating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_lilr, 0, 0, 0);
            } else if (totalrate < 3) {
                rating.setTextColor(arancius);
                rating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_lila, 0, 0, 0);
            } else if (totalrate < 4) {
                rating.setTextColor(yellou);
                rating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_lily, 0, 0, 0);
            } else if (totalrate < 4.5) {
                rating.setTextColor(lightgreen);
                rating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_lillg, 0, 0, 0);
            } else if (totalrate <= 5) {
                rating.setTextColor(stronggreen);
                rating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_lilsg, 0, 0, 0);
            }

            rating.setText(String.valueOf(totalrate));
        }

        public void setPersonalData(String name) {
            TextView tvname = (TextView) mView.findViewById(R.id.bok_name);
            tvname.setText(name);
        }

        public void setDistance(float _dist) {
            TextView distTV = (TextView) mView.findViewById(R.id.bok_distance);
            double dist = round(_dist/1000, 1);
            String distance = String.valueOf(dist).concat("km");
            distTV.setText(distance);
        }

        public void itemRemoveVisibility() {
            mView.setVisibility(View.GONE);
        }

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

}
