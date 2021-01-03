package com.pjt.rebis.ui.profile;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pjt.rebis.R;

    /* Fragment which allows the user to pick a "current address" from the list of the given addresses. */
public class walletCurrent extends Fragment {
    private TextView corrente;
    private FirebaseRecyclerAdapter<WalletItem, PassViewHolder> mPeopleRVAdapter;

    public walletCurrent() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vistus = inflater.inflate(R.layout.fragment_wallet_current, container, false);

        corrente = vistus.findViewById(R.id.wacu_address);

        Button bt_exit = vistus.findViewById(R.id.wacu_exit);
        bt_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });

        RecyclerView mPeopleRV;
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuser).child("Wallets");
        final DatabaseReference currentRef = mDatabase.child("Current");
        mDatabase.keepSynced(true);
        mPeopleRV = (RecyclerView) vistus.findViewById(R.id.wacu_Recyc);

        DatabaseReference personsRef = mDatabase.child("List");
        Query personsQuery = personsRef.orderByValue();

        mPeopleRV.hasFixedSize();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mPeopleRV.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions personsOptions = new FirebaseRecyclerOptions.Builder<WalletItem>().setQuery(personsQuery, WalletItem.class).build();

        currentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                corrente.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});

        mPeopleRVAdapter = new FirebaseRecyclerAdapter<WalletItem, PassViewHolder>(personsOptions) {
            @Override
            protected void onBindViewHolder(PassViewHolder holder, final int position, final WalletItem model) {
                holder.setWallet(model.getAddress());
                if (model.getAddress().equals(corrente.getText().toString()))
                    holder.mView.findViewById(R.id.itemcu_star).setBackgroundResource(android.R.drawable.star_big_on);
                else
                    holder.mView.findViewById(R.id.itemcu_star).setBackgroundResource(android.R.drawable.star_big_off);

                (holder.mView.findViewById(R.id.itemcu_star)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String nowchanged = model.getAddress();
                        currentRef.setValue(nowchanged);
                        corrente.setText(nowchanged);
                        reloadFrag();
                    }
                });

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String nowchanged = model.getAddress();
                        currentRef.setValue(nowchanged);
                        corrente.setText(nowchanged);
                        reloadFrag();
                    }
                });
            }

            @Override
            public PassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.walletcurrent_item, parent, false);
                return new PassViewHolder(view);
            }
        };

        mPeopleRV.setAdapter(mPeopleRVAdapter);

        return vistus;
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

        public void setWallet (String _wallet) {
            TextView wallet = (TextView) mView.findViewById(R.id.itemcu_address);
            wallet.setText(_wallet);
        }

        public void starIsOn() {
            Button stella = (Button) mView.findViewById(R.id.itemcu_star);
            stella.setBackgroundResource(android.R.drawable.star_big_on);
        }
    }

    private void exit() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag1 = new ProfileFragment();
        ft.replace(R.id.prf_constr, frag1, "proffy");
        ft.commit();
    }

    private void reloadFrag() {
        // Reload current fragment
        Fragment frg = null;
        frg = getActivity().getSupportFragmentManager().findFragmentByTag("currenty");
        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }

}
