package com.pjt.rebis.ui.bikes;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pjt.rebis.MainActivity;
import com.pjt.rebis.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class bikesFragment extends Fragment {
    private FirebaseRecyclerAdapter<Bicicletta, PassViewHolder> mBikesRVAdapter;
    private StorageReference mStorageRef;
    private Button ava, unava, all;
    private static int querier = 0;
    private Button addNew;
    private String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public bikesFragment() {
    }

    public bikesFragment(int _querier) {
        this.querier = _querier;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_bikes, container, false);

        Toolbar myToolbar = (Toolbar) getActivity().findViewById(R.id.myToolbar);
        TextView textView = (TextView) myToolbar.findViewById(R.id.toolbarTextView);
        textView.setText(getString(R.string.title_bikes));

        mStorageRef = FirebaseStorage.getInstance().getReference();
        final String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        RecyclerView mBikesRV = (RecyclerView) root.findViewById(R.id.bk_Recyc);

        ava = (Button) root.findViewById(R.id.bk_qrAVAB);
        all = (Button) root.findViewById(R.id.bk_qeALL);
        unava = (Button) root.findViewById(R.id.bk_qrUNAV);

        addNew = (Button) root.findViewById(R.id.bk_addBike);

        checkWhichConstructorIs();

        ava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                querier = 1;
                reloadFrag();
            }
        });

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                querier = 0;
                reloadFrag();
            }
        });

        unava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                querier = 2;
                reloadFrag();
            }
        });

        DatabaseReference bikesRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuser).child("Bikes");
        bikesRef.keepSynced(true);
        Query bikesQuery;
        if (querier==1) {
            bikesQuery = bikesRef.orderByChild("status").equalTo("available");
        } else if (querier==2) {
            bikesQuery = bikesRef.orderByChild("status").equalTo("unavailable");
        } else {
            bikesQuery = bikesRef.orderByChild("name");
        }

        mBikesRV.hasFixedSize();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        mBikesRV.setLayoutManager(linearLayoutManager);

        final FirebaseRecyclerOptions personsOptions = new FirebaseRecyclerOptions.Builder<Bicicletta>().setQuery(bikesQuery, Bicicletta.class).build();

        mBikesRVAdapter = new FirebaseRecyclerAdapter<Bicicletta, PassViewHolder>(personsOptions) {
            @Override
            protected void onBindViewHolder(PassViewHolder holder, final int position, final Bicicletta model) {
                try {
                    holder.setName(model.getName());
                    holder.setStatus(model.getStatus().toUpperCase());
                    holder.setModel(model.getBrand());
                    holder.setYear(model.getYear() + "");
                    holder.setValue("Value: " + model.getValue()+"€");
                    holder.setCounter("rented: " + model.getRentedCNT());
                    holder.setImage(model.getImage());
                    if (!model.getStatus().equals("available"))
                        holder.setCustomer(model.getCustomer(), model.getBikeIDRef(),currentuser);

                    holder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //do smth
                        }
                    });
                } catch (Exception szighi) {}
            }

            @Override
            public PassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.bike_item, parent, false);

                return new PassViewHolder(view);
            }
        };

        mBikesRV.setAdapter(mBikesRVAdapter);

        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragAB = new add_ABike();
                ft.replace(R.id.bk_constr, fragAB, "addBK");
                ft.commit();
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBikesRVAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mBikesRVAdapter.stopListening();
    }

    public static class PassViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public PassViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String bN) {
            TextView bk_name = (TextView) mView.findViewById(R.id.bk_name);
            bk_name.setText(bN.toLowerCase());
        }

        public void setStatus(String bS) {
            ImageView bk_status = (ImageView) mView.findViewById(R.id.bk_status);
            if (bS.equals("AVAILABLE"))
                bk_status.setBackgroundResource(R.color.strong_green);
            else if (bS.equals("BOOKED"))
                bk_status.setBackgroundResource(R.color.arancius);
            else
                bk_status.setBackgroundResource(R.color.strong_red);
        }

        public void setImage(String bI) {
            ImageView bk_image = (ImageView) mView.findViewById(R.id.bk_image);
            Uri bikeImage = Uri.parse(bI);
            Picasso.get().load(bikeImage).resize(750,500).onlyScaleDown().into(bk_image);
        }

        public void setModel(String bM) {
            TextView bk_model = (TextView) mView.findViewById(R.id.bk_model);
            bk_model.setText(bM);
        }

        public void setYear(String bY) {
            TextView bk_yearp = (TextView) mView.findViewById(R.id.bk_yearp);
            bk_yearp.setText(bY);
        }

        public void setValue(String bV) {
            TextView bk_value = (TextView) mView.findViewById(R.id.bk_value);
            bk_value.setText(bV);
        }

        public void setCounter(String bC) {
            TextView bk_counter = (TextView) mView.findViewById(R.id.bk_counter);
            bk_counter.setText(bC);
        }

        public void setCustomer(String bR, String bikeref, String rentRef) {
            final TextView bk_customer = (TextView) mView.findViewById(R.id.bk_customer);
            final String cippa[] = bR.split("#@&@#");
            String output;
            if (cippa[1].length() < 15) {
                output = "[ " + cippa[0] + " - " + cippa[1] + " ]";
                bk_customer.setText(output);
            }
            else {
                try {
                    Date date = new Date();
                    Calendar current = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
                    current.setTime(date);
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss zz yyyy", Locale.ENGLISH);
                    Calendar old = Calendar.getInstance();
                    Date stringdata = sdf.parse(cippa[1]);
                    old.setTime(stringdata);
                    old.add(Calendar.HOUR_OF_DAY, 3);

                    if (old.before(current)) {
                        DatabaseReference bbab = FirebaseDatabase.getInstance().getReference().child("USERS").child(rentRef)
                                .child("Bikes").child(bikeref);
                        bbab.child("customer").setValue(" ");
                        bbab.child("status").setValue("available");
                    }

                    long timeinmillis = old.getTimeInMillis() - current.getTimeInMillis();
                    new CountDownTimer(timeinmillis, 1000) {
                        public void onTick(long millisUntilFinished) {

                            int seconds = (int) millisUntilFinished / 1000;
                            int minutes = seconds / 60;
                            seconds = seconds % 60;
                            int hours = minutes / 60;
                            minutes = minutes % 60;
                            hours = hours % 60;
                            String timerToBePrinted = hours + "h:" + minutes + "m:" + seconds + "s";
                            bk_customer.setText("[ " + cippa[0] + " - " + timerToBePrinted);
                        }

                        public void onFinish() {
                            bk_customer.setText("[ " + cippa[0] + " - " + "expired");
                        }
                    }.start();

                } catch (Exception exs) {
                    exs.printStackTrace();
                }
            }

        }

    }

    private void checkWhichConstructorIs() {
        if (querier == 1) {
            // THIS IS THE 'AVAILABLE' QUERY
            ava.setBackgroundResource(R.drawable.buttonbg);
            unava.setBackgroundResource(R.color.transparent);
            all.setBackgroundResource(R.color.transparent);

            ava.setTextColor(getResources().getColor(R.color.colorAccent));
            unava.setTextColor(getResources().getColor(R.color.avorio));
            all.setTextColor(getResources().getColor(R.color.avorio));

            addNew.setVisibility(View.INVISIBLE);
        } else if (querier == 2) {
            // THIS IS THE 'UNAVAILABLE' QUERY
            unava.setBackgroundResource(R.drawable.buttonbg);
            ava.setBackgroundResource(R.color.transparent);
            all.setBackgroundResource(R.color.transparent);

            unava.setTextColor(getResources().getColor(R.color.colorAccent));
            ava.setTextColor(getResources().getColor(R.color.avorio));
            all.setTextColor(getResources().getColor(R.color.avorio));

            addNew.setVisibility(View.INVISIBLE);
        } else {
            // THIS IS THE 'ALL' BASIC QUERY
            all.setBackgroundResource(R.drawable.buttonbg);
            ava.setBackgroundResource(R.color.transparent);
            unava.setBackgroundResource(R.color.transparent);

            all.setTextColor(getResources().getColor(R.color.colorAccent));
            unava.setTextColor(getResources().getColor(R.color.avorio));
            ava.setTextColor(getResources().getColor(R.color.avorio));

            addNew.setVisibility(View.VISIBLE);
        }
    }

    private void reloadFrag() {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.navigation_bikes);
    }
}
