package com.pjt.rebis.ui.booking;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pjt.rebis.R;
import com.pjt.rebis.ui.bikes.Bicicletta;
import com.pjt.rebis.utility.SaveSharedPreference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;


public class bike_booking extends Fragment {
    private FirebaseRecyclerAdapter<User, PassViewHolder> mPeopleRVAdapter;
    private FirebaseRecyclerAdapter<Booking, BookViewHolder> mBikeRVAdapter;
    private static int sadred, lightgreen, stronggreen, arancius, yellou;

    public bike_booking() {
        // Required empty public constructor
    }

    /*
    TextView timerTextView;
    long startTime = 0;

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerTextView.setText(String.format("%d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View dviu =  inflater.inflate(R.layout.fragment_bike_booking, container, false);

        Toolbar myToolbar = (Toolbar) getActivity().findViewById(R.id.myToolbar);
        TextView textView = (TextView) myToolbar.findViewById(R.id.toolbarTextView);
        textView.setText(getString(R.string.title_booking));

        sadred = ContextCompat.getColor(getContext(), R.color.sadred);
        arancius = ContextCompat.getColor(getContext(), R.color.arancius);
        yellou = ContextCompat.getColor(getContext(), R.color.yellou);
        lightgreen = ContextCompat.getColor(getContext(), R.color.lightgreen);
        stronggreen = ContextCompat.getColor(getContext(), R.color.strong_green);



        RecyclerView mBikeRV;

        mBikeRV = (RecyclerView) dviu.findViewById(R.id.bok_Recyc0);

        DatabaseReference mDbBook = FirebaseDatabase.getInstance().getReference().child("BOOKINGS");
        Query dbquery = mDbBook.orderByChild("customer").equalTo(SaveSharedPreference.getUserName(getContext()));
        mDbBook.keepSynced(true);

        LinearLayoutManager linearLayoutManager0 = new LinearLayoutManager(getActivity());

        mBikeRV.setLayoutManager(linearLayoutManager0);

        final FirebaseRecyclerOptions bikeOptions = new FirebaseRecyclerOptions.Builder<Booking>().setQuery(dbquery, Booking.class).build();

        mBikeRVAdapter = new FirebaseRecyclerAdapter<Booking, BookViewHolder>(bikeOptions) {
            @Override
            protected void onBindViewHolder(final BookViewHolder holder, final int position, final Booking model) {
                try {

                    holder.setUsers(model.getRenter());
                    holder.setTheBike(model.getBike().substring(2));

                    Date date = new Date();
                    Calendar current = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
                    current.setTime(date);
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss zz yyyy", Locale.ENGLISH);
                    Calendar old = Calendar.getInstance();
                    Date stringdata = sdf.parse(model.getTime());
                    old.setTime(stringdata);
                    old.add(Calendar.HOUR_OF_DAY, 3);
                    if (old.before(current)) {
                        DatabaseReference brob = FirebaseDatabase.getInstance().getReference().child("BOOKINGS").child(String.valueOf(model.getKey()));
                        brob.child("customer").removeValue();
                        brob.child("renter").removeValue();

                        DatabaseReference bbab = FirebaseDatabase.getInstance().getReference().child("USERS").child(model.getRenterUID())
                                .child("Bikes").child(model.getBike());
                        bbab.child("customer").setValue(" ");
                        bbab.child("status").setValue("available");
                    }
                    long timeinmillis = old.getTimeInMillis() - current.getTimeInMillis();
                    holder.setTime(timeinmillis);

                    holder.setRentData(model.getRenterUID());
                    dviu.findViewById(R.id.bkbk_nobkss).setVisibility(View.GONE);


                    holder.bView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                } catch (Exception crash) {
                    crash.printStackTrace();
                }
            }

            @Override
            public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.bookbike_item, parent, false);

                return new BookViewHolder(view);
            }
        };

        mBikeRV.setAdapter(mBikeRVAdapter);








        RecyclerView mPeopleRV;

        mPeopleRV = (RecyclerView) dviu.findViewById(R.id.bok_Recyc);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");
        final Query personsQuery = mDatabase.orderByChild("type").equalTo("renter");
        mDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        mPeopleRV.setLayoutManager(linearLayoutManager);

        final FirebaseRecyclerOptions personsOptions = new FirebaseRecyclerOptions.Builder<User>().setQuery(personsQuery, User.class).build();

        mPeopleRVAdapter = new FirebaseRecyclerAdapter<User, PassViewHolder>(personsOptions) {
            @Override
            protected void onBindViewHolder(final PassViewHolder holder, final int position, final User model) {
                try {
                    final Credentials credRef = model.getCredentials();

                    if (credRef.getUsertype().equals("renter")) {
                        PersonalData persRef = model.getPersonalData();
                        final Ratings rateRef = model.getRatings();
                        final HashMap<String, Bicicletta> biciRef = model.getBikes();

                        holder.setPersonalData(credRef.getUsername());
                        holder.setCredentials(persRef.getCity(), persRef.getAddress(), persRef.getPhonenumber());
                        if (rateRef == null)
                            holder.setRating(0, 1);
                        else
                            holder.setRating(rateRef.getSum(), rateRef.getVotes());
/*
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
*/
                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String key = getSnapshots().getSnapshot(position).getKey();
                                if (rateRef == null)
                                    openExpansionFragment(key, biciRef, credRef.getUsername(),0.0);
                                else
                                    openExpansionFragment(key, biciRef, credRef.getUsername(),
                                        round((rateRef.getSum())/(rateRef.getVotes()), 1));
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
        mBikeRVAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPeopleRVAdapter.stopListening();
        mBikeRVAdapter.stopListening();
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

            if (totalrate >= 1 && totalrate < 2) {
                rating.setTextColor(sadred);
                rating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_lilr, 0, 0, 0);
            } else if (totalrate >= 2 && totalrate < 3) {
                rating.setTextColor(arancius);
                rating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_lila, 0, 0, 0);
            } else if (totalrate >= 3 && totalrate < 4) {
                rating.setTextColor(yellou);
                rating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_lily, 0, 0, 0);
            } else if (totalrate >= 4 && totalrate < 4.5) {
                rating.setTextColor(lightgreen);
                rating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_lillg, 0, 0, 0);
            } else if (totalrate >= 4.5 && totalrate <= 5) {
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



    public static class BookViewHolder extends RecyclerView.ViewHolder {
        View bView;

        public BookViewHolder(View itemView) {
            super(itemView);
            bView = itemView;
        }

        public void setUsers(String _rent) {
            TextView rent = (TextView) bView.findViewById(R.id.bkbk_renter);
            rent.setText(_rent);
        }

        public void setTime(long cal) {
            final TextView settime = (TextView) bView.findViewById(R.id.bkbk_time);
            new CountDownTimer(cal, 1000) {

                public void onTick(long millisUntilFinished) {

                    int seconds = (int) millisUntilFinished / 1000;
                    int minutes = seconds / 60;
                    seconds = seconds % 60;
                    int hours = minutes / 60;
                    minutes = minutes % 60;
                    hours = hours % 60;
                    String timerToBePrinted = hours + "h:" + minutes + "m:" + seconds + "s";
                    settime.setText(timerToBePrinted);
                }

                public void onFinish() {
                    settime.setText("expired");
                }
            }.start();

        }

        private void setTheBike(String bike) {
            TextView bkname = (TextView) bView.findViewById(R.id.bkbk_bike);
            bkname.setText(bike);
        }

        private void setRentData(String key) {
            final TextView rntAddrx = (TextView) bView.findViewById(R.id.bkbk_address);
            final TextView rntPhone = (TextView) bView.findViewById(R.id.bkbk_number);
            DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("USERS").child(key)
                    .child("PersonalData");
            userDB.child("city").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String dAddr = dataSnapshot.getValue(String.class);
                    rntAddrx.setText(dAddr);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }});
            userDB.child("phonenumber").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String dPhone = "TEL: " + dataSnapshot.getValue(String.class);
                    rntPhone.setText(dPhone);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }});
        }

    }



    private void openExpansionFragment(String key, HashMap<String, Bicicletta> bikes, String own, double stars) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag = new expandbookbike_frag(key, bikes, own, stars);
        ft.replace(R.id.constr_book, frag, "booxpand");
        ft.commit();
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

}
