package com.pjt.rebis.ui.bikes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
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
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.pjt.rebis.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class bikesFragment extends Fragment {
    private FirebaseRecyclerAdapter<Bicicletta, PassViewHolder> mBikesRVAdapter;
    private StorageReference mStorageRef;
    private Button ava, unava, all;
    private int querier=0;
    private Button addNew;

    public bikesFragment() {
    }

    public bikesFragment(int _querier) {
        this.querier = _querier;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_bikes, container, false);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
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

        FirebaseRecyclerOptions personsOptions = new FirebaseRecyclerOptions.Builder<Bicicletta>().setQuery(bikesQuery, Bicicletta.class).build();

        mBikesRVAdapter = new FirebaseRecyclerAdapter<Bicicletta, PassViewHolder>(personsOptions) {
            @Override
            protected void onBindViewHolder(PassViewHolder holder, final int position, final Bicicletta model) {
                try {
                    holder.setName(model.getName());
                    holder.setStatus(model.getStatus().toUpperCase());
                    holder.setModel(model.getBrand());
                    holder.setYear(model.getYear() + "");
                    holder.setValue("Value(ETH): " + model.getValue());
                    holder.setCounter("rented: " + model.getRentedCNT());
                    holder.setImage(model.getImage());
                    if (model.getStatus().equals("unavailable"))
                        holder.setCustomer(model.getCustomer());

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

        public void setCustomer(String bR) {
            TextView bk_customer = (TextView) mView.findViewById(R.id.bk_customer);
            String cippa[] = bR.split("#@&@#");
            String output = "[ "+cippa[0]+" - "+cippa[1]+" ]";
            bk_customer.setText(output);
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
        // Reload current fragment
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();
    }

    /* This method causes:
        'java.lang.NullPointerException: Attempt to invoke virtual method 'void androidx.fragment.app.Fragment.setNextAnim(int)'
            on a null object reference'
       Probably is useless.
    private void deleteOldBoys() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (querier == 1) {
            try {
                ft.remove(fm.findFragmentByTag("unava"));
                ft.remove(fm.findFragmentByTag("all"));
            } catch (Exception ennul) {
            }
            ft.commit();
        } else if (querier == 2) {
            try {
                ft.remove(fm.findFragmentByTag("ava"));
                ft.remove(fm.findFragmentByTag("all"));
            } catch (Exception ennul) {
            }
            ft.commit();
        } else {
            ft.remove(fm.findFragmentByTag("ava"));
            ft.remove(fm.findFragmentByTag("unava"));
        }
    }
    */

}
