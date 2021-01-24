package com.pjt.rebis.Notification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pjt.rebis.Authentication.SaveSharedPreference;
import com.pjt.rebis.MainActivity;
import com.pjt.rebis.R;
import com.pjt.rebis.ui.history.RentalItem;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

public class Notification_RecyclerViewAdapter extends RecyclerView.Adapter<Notification_RecyclerViewAdapter.ViewHolder> {
    private ArrayList<RentalItem> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private String type;
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("USERS");
    private DatabaseReference phoneRef;
    private Activity act;
    private Context ctx;
    private View rootView;
    private int ping = 0;

    // data is passed into the constructor
    Notification_RecyclerViewAdapter(Activity _act, Context context, ArrayList<RentalItem> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.act = _act;
        this.ctx = context;
        type = SaveSharedPreference.getUserType(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        rootView = mInflater.inflate(R.layout.notification_item, viewGroup, false);
        return new ViewHolder(rootView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        try {
            final ViewHolder viewtmp = viewHolder;
            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            final RentalItem robj = mData.get(position);
            String[] username;
            if (type.equals("renter")) {
                username = robj.getCustomer().split("#@&@#");
                phoneRef = mRef.child(username[1]).child("PersonalData").child("phoneNumber");
            } else {
                username = robj.getRenter().split("#@&@#");
                phoneRef = mRef.child(username[1]).child("PersonalData").child("phonenumber");
            }
            String depo = robj.getDeposit()+"";
            String date = robj.getDate();

            int days = robj.getDays();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Calendar c = Calendar.getInstance();
            c.setTime(sdf.parse(date));
            c.add(Calendar.DATE, days);  // number of days to add

            String expiry = sdf.format(c.getTime());

            phoneRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String numbertmp = dataSnapshot.getValue(String.class);
                    viewtmp.setNumber(numbertmp);
                    Button call = viewHolder.onClickCall();
                    call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialComposer(numbertmp);
                        }
                    });
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }});

            String topper = username[0] + " ● " + robj.getBike().substring(3);
            if (topper.length() > 22)
                topper = topper.substring(0,20) + "…";
            viewHolder.setUsername(topper);
            viewHolder.setDate(date);
            viewHolder.setExpiry(expiry);
            viewHolder.setDeposit(depo);

            final Button expand  = viewHolder.onClickExpand();
            final View forexpansionView = viewHolder.expanserView();
            final View fullView = viewHolder.fullView();
            expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expansionEr(forexpansionView, fullView, robj, expand);
                }
            });

        } catch (Exception dds) {
            dds.printStackTrace();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUsername(String usernm) {
            TextView ni_username = (TextView) mView.findViewById(R.id.ni_username);
            ni_username.setText(usernm);
        }

        public void setNumber(String number) {
            TextView ni_numb = (TextView) mView.findViewById(R.id.ni_number);
            ni_numb.setText(number);
        }

        public void setDeposit(String depo) {
            if (depo.equals("0.0"))
                depo="No";
            TextView ni_deposit = (TextView) mView.findViewById(R.id.ni_deposit);
            String gp = ni_deposit.getText().toString() + " " + depo;
            ni_deposit.setText(gp);
        }

        public void setDate(String date) {
            TextView ni_date = (TextView) mView.findViewById(R.id.ni_date);
            String gp = ni_date.getText().toString() + " " + date;
            ni_date.setText(gp);
        }

        public void setExpiry(String exp) {
            TextView ni_days = (TextView) mView.findViewById(R.id.ni_expiry);
            String gp = ni_days.getText().toString() + " " + exp;
            ni_days.setText(gp);
        }

        public Button onClickCall() {
            Button call = (Button) mView.findViewById(R.id.ni_call);
            return call;
        }

        public Button onClickExpand() {
            Button expand = (Button) mView.findViewById(R.id.ni_expand);
            return expand;
        }

        public View expanserView() {
            View expanser = (View) mView.findViewById(R.id.ni_expansionView);
            return expanser;
        }

        public View fullView() {
            View fullconstr = (View) mView.findViewById(R.id.ni_constr);
            return fullconstr;
        }

    }

    // convenience method for getting data at click position
    RentalItem getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mData.size();
    }

    private void dialComposer(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number.substring(4)));
        act.startActivity(intent);
    }

    private void expansionEr(View expanser, View full, RentalItem obj, Button button) {
        int width = full.getLayoutParams().width;
        if (ping == 0) {
            expanser.setVisibility(View.VISIBLE);
            completeViewData(obj);
            button.setBackgroundResource(R.drawable.dropdown_iconrib);
            int heigth = 650;
            full.setLayoutParams(new FrameLayout.LayoutParams(width, heigth));
            ping = 1;
        } else {
            expanser.setVisibility(View.GONE);
            button.setBackgroundResource(R.drawable.dropdown_icon2);
            int heigth = 320;
            full.setLayoutParams(new FrameLayout.LayoutParams(width, heigth));
            ping = 0;
        }
    }

    private void completeViewData(RentalItem robj) {
        final TextView tvfullname = rootView.findViewById(R.id.eni_fullname);
        final TextView tvcity = rootView.findViewById(R.id.eni_city);
        final TextView tvpcode = rootView.findViewById(R.id.eni_pcode);
        final TextView tvcountry = rootView.findViewById(R.id.eni_country);
        final TextView tvaddress = rootView.findViewById(R.id.eni_address);
        final ImageView imimage = rootView.findViewById(R.id.eni_propic);

        DatabaseReference dataRef;
        if (type.equals("renter")) {
            String[] user = robj.getCustomer().split("#@&@#");
            dataRef= mRef.child(user[1]).child("PersonalData");
        } else {
            String[] user = robj.getRenter().split("#@&@#");
            dataRef= mRef.child(user[1]).child("PersonalData");
        }

        dataRef.child("city").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String city = dataSnapshot.getValue(String.class);
                tvcity.setText(city);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});

        dataRef.child("country").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String country = dataSnapshot.getValue(String.class);
                tvcountry.setText(country);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});

        if (type.equals("customer")) {
            dataRef.child("fullname").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    tvfullname.setText(name);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            dataRef.child("address").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String address = dataSnapshot.getValue(String.class);
                    tvaddress.setText(address);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }});

            dataRef.child("postalcode").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String code = dataSnapshot.getValue(String.class);
                    tvpcode.setText(code);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }});

        } else {
            dataRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    tvfullname.setText(name);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            dataRef.child("surname").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String surname = dataSnapshot.getValue(String.class);
                    String fullname = tvfullname.getText().toString() + " " +surname;
                    tvfullname.setText(fullname);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            dataRef.child("homeAddress").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String address = dataSnapshot.getValue(String.class);
                    tvaddress.setText(address);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }});

            dataRef.child("postalCode").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String code = dataSnapshot.getValue(String.class);
                    tvpcode.setText(code);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }});
        }

        dataRef.child("profileImage").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String image = dataSnapshot.getValue(String.class);
                Uri imagination = Uri.parse(image);
                Picasso.get().load(imagination).into(imimage);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});

    }

}
