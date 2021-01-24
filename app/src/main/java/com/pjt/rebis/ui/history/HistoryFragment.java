package com.pjt.rebis.ui.history;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.pjt.rebis.Authentication.SaveSharedPreference;
import com.pjt.rebis.R;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/* This is the 'main' fragment for the History section. Here, a user (both renter and customer) can look at all his
    *  present and past rental activities.  */
public class HistoryFragment extends Fragment {
    static String c, d, f, g;
    private FirebaseRecyclerAdapter<RentalItem, PassViewHolder> mPeopleRVAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_history, container, false);

        c = getString(R.string.hsmd_num);
        f = getString(R.string.hsmd_fee);
        d = getString(R.string.hsmd_depo);
        g = getString(R.string.hsmd_days);

        DatabaseReference mDatabase;
        RecyclerView mPeopleRV;
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("RENTALS");
        mDatabase.keepSynced(true);
        mPeopleRV = (RecyclerView) root.findViewById(R.id.his_Recyc);

        String _user = SaveSharedPreference.getUserName(getActivity());
        final String _type = SaveSharedPreference.getUserType(getActivity());
        DatabaseReference personsRef = FirebaseDatabase.getInstance().getReference().child("RENTALS");
        Query personsQuery;
        if (_type.equals("renter"))
            personsQuery = personsRef.orderByChild("Renter").equalTo(_user+"#@&@#"+currentuser);
        else
            personsQuery = personsRef.orderByChild("Customer").equalTo(_user+"#@&@#"+currentuser);

        mPeopleRV.hasFixedSize();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        mPeopleRV.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions personsOptions = new FirebaseRecyclerOptions.Builder<RentalItem>().setQuery(personsQuery, RentalItem.class).build();

        mPeopleRVAdapter = new FirebaseRecyclerAdapter<RentalItem, PassViewHolder>(personsOptions) {
            @Override
            protected void onBindViewHolder(PassViewHolder holder, final int position, final RentalItem model) {
                String other, odress;
                if (_type.equals("renter")) {
                    String[] temp = model.getCustomer().split("#@&@#");
                    other = temp[0];
                    odress = model.getAddressCustomer();
                } else {
                    String[] temp = model.getRenter().split("#@&@#");
                    other = temp[0];
                    odress = model.getAddressRenter();
                }
                if (other.equals("void"))
                    holder.setUsername(getString(R.string.hire_empty));
                else
                    holder.setUsername(other);
                holder.setCounter(position+1+"");
                holder.setImage(model.getState());
                holder.setAddress(odress+"");
                holder.setFee(model.getFee()+"");
                holder.setDeposit(model.getDeposit()+"");
                holder.setDate(model.getDate());
                holder.setDays(model.getDays()+"");

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    Date date = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(sdf.parse(model.getDate()));
                    c.add(Calendar.DATE, model.getDays());  // number of days to add
                    String rendatetmp = sdf.format(c.getTime());
                    Date rendate = sdf.parse(rendatetmp);

                    if ((!model.getState().equals("ended")) && (date.compareTo(rendate) > 0))
                        holder.mView.setBackgroundResource(R.color.red_panned);
                } catch (Exception szig) {}

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (_type.equals("renter")) {
                            RentalItem ritm = new RentalItem(
                              model.getRenter(),
                              model.getCustomer(),
                              model.getAddressRenter(),
                              model.getAddressCustomer(),
                              model.getID(),
                              model.getDate(),
                              model.getDays(),
                              model.getFee(),
                              model.getDeposit(),
                              model.getState(),
                              model.getBike()
                            );
                            if (ritm.getCustomer().equals("void")) {
                                deleteNotRented(ritm.getID());
                            } else {
                                if(ritm.getState().equals("ended"))
                                    showCustomerInfo(ritm.getCustomer(), 0, "");
                                else
                                    showCustomerInfo(ritm);
                            }
                        } else {
                            showRenterInfo(model.getRenter());
                        }
                    }
                });
            }

            @Override
            public PassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cardhis_item, parent, false);

                return new PassViewHolder(view);
            }
        };

        mPeopleRV.setAdapter(mPeopleRVAdapter);

        return root;
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
        public View mView;

        public PassViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUsername(String title) {
            TextView cc_user = (TextView) mView.findViewById(R.id.cc_username);
            cc_user.setText(title);
        }

        public void setCounter(String cooun) {
            String cp = c +" "+ cooun;
            TextView cc_counter = (TextView) mView.findViewById(R.id.cc_rentalcounter);
            cc_counter.setText(cp);
        }

        public void setImage(String pass) {
            ImageView cc_image = (ImageView) mView.findViewById(R.id.cc_image);
            if (pass.equals("ended")) {
                mView.setBackgroundResource(R.color.green_panned);
                cc_image.setBackgroundResource(R.drawable.his_ended);
            } else {
                mView.setBackgroundResource(R.color.blue_panned);
                cc_image.setBackgroundResource(R.drawable.his_pending);
            }
        }

        public void setAddress(String pass) {
            TextView cc_address = (TextView) mView.findViewById(R.id.cc_address);
            cc_address.setText(pass);
        }

        public void setFee(String pass) {
            String fp = f+" "+pass;
            TextView cc_fee = (TextView) mView.findViewById(R.id.cc_fee);
            cc_fee.setText(fp);
        }

        public void setDeposit(String pass) {
            if (pass.equals("0.0"))
                pass="No";
            String dp = d+" "+pass;
            TextView cc_deposit = (TextView) mView.findViewById(R.id.cc_deposit);
            cc_deposit.setText(dp);
        }

        public void setDate(String pass) {
            TextView cc_date = (TextView) mView.findViewById(R.id.cc_date);
            cc_date.setText(pass);
        }

        public void setDays(String pass) {
            String gp = g+" "+pass;
            TextView cc_days = (TextView) mView.findViewById(R.id.cc_days);
            cc_days.setText(gp);
        }
    }

    private void deleteNotRented(int _id) {
        final int thing = _id;
        custom_dialogDR cddr = new custom_dialogDR(getActivity(), thing);
        cddr.show();
    }

    private void showRenterInfo(String renter) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag5 = new expandRenterView(renter);
        ft.replace(R.id.his_const, frag5, "expandCust");
        ft.commit();
    }

    private void showCustomerInfo(String cust, int _id, String bike) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag3 = new expandCustomerView(cust, _id, bike);
        ft.replace(R.id.his_const, frag3, "expandCust");
        ft.commit();
    }

    private void showCustomerInfo(RentalItem _ritm) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag3 = new expandCustomerView(_ritm);
        ft.replace(R.id.his_const, frag3, "expandCust");
        ft.commit();
    }

}