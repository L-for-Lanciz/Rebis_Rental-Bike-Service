package com.pjt.rebis.ui.bikes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
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

public class bikesFragment extends Fragment {
    private FirebaseRecyclerAdapter<Bicicletta, PassViewHolder> mBikesRVAdapter;
    private StorageReference mStorageRef;

    public bikesFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_bikes, container, false);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        RecyclerView mBikesRV = (RecyclerView) root.findViewById(R.id.bk_Recyc);

        DatabaseReference bikesRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuser).child("Bikes");
        bikesRef.keepSynced(true);
        Query bikesQuery = bikesRef.orderByChild("name");

        mBikesRV.hasFixedSize();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getActivity(), 2);
        mBikesRV.setLayoutManager(gridLayoutManager);

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

        Button addNew = (Button) root.findViewById(R.id.bk_addBike);
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
            bk_name.setText(bN);
        }

        public void setStatus(String bS) {
            TextView bk_status = (TextView) mView.findViewById(R.id.bk_status);
            bk_status.setText(bS);
            if (bS.equals("AVAILABLE"))
                bk_status.setTextColor(Color.GREEN);
            else
                bk_status.setTextColor(Color.RED);
        }

        public void setImage(String bI) {
            ImageView bk_image = (ImageView) mView.findViewById(R.id.bk_image);
            Uri bikeImage = Uri.parse(bI);
            Picasso.get().load(bikeImage).into(bk_image);
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
    }


}
