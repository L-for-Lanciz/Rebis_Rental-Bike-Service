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

    /* Fragment which allows the user to view all the given addresses. Here, the user can also delete one. */
public class walletMagnifier extends Fragment {
    private TextView corrente;
    private FirebaseRecyclerAdapter<WalletItem, PassViewHolder> mPeopleRVAdapter;
    private String currentuser;

    public walletMagnifier() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_wallet_magnifier, container, false);

        corrente = fragmentView.findViewById(R.id.wama_address);

        Button bt_exit = fragmentView.findViewById(R.id.wama_exit);
        bt_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });

        RecyclerView mPeopleRV;
        currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuser).child("Wallets");
        DatabaseReference currentRef = mDatabase.child("Current");

        mDatabase.keepSynced(true);
        mPeopleRV = (RecyclerView) fragmentView.findViewById(R.id.wama_Recyc);

        DatabaseReference personsRef = mDatabase.child("List");
        Query personsQuery = personsRef.orderByValue();

       // mPeopleRV.hasFixedSize();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        // linearLayoutManager.setStackFromEnd(true);
        mPeopleRV.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions personsOptions = new FirebaseRecyclerOptions.Builder<WalletItem>().setQuery(personsQuery, WalletItem.class).build();

        mPeopleRVAdapter = new FirebaseRecyclerAdapter<WalletItem, PassViewHolder>(personsOptions) {
            @Override
            protected void onBindViewHolder(PassViewHolder holder, final int position, final WalletItem model) {
                String nameRef = model.getAddress();
                holder.setWallet(nameRef);

                (holder.mView.findViewById(R.id.itemwa_delete)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteWallet(model.getAddress());
                    }
                });

            /*    holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });  */
            }

            @Override
            public PassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.walletma_item, parent, false);

                return new PassViewHolder(view);
            }
        };

        mPeopleRV.setAdapter(mPeopleRVAdapter);

        currentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                corrente.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});

        return fragmentView;
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

        public void setWallet(String _wallet) {
            TextView walletgui = (TextView) mView.findViewById(R.id.itemwa_address);
            walletgui.setText(_wallet);
        }

    }

    public void deleteWallet(String name) {
        final String rip=name;
        if (corrente.getText().toString().equals(rip)) {
            custom_dialogWM cddr = new custom_dialogWM(getActivity(), rip, getString(R.string.aldi_title),
                    getString(R.string.aldi_usure) + "\n" + rip, true);
            cddr.show();
        } else {
            custom_dialogWM cddr = new custom_dialogWM(getActivity(), rip, getString(R.string.aldi_title),
                    getString(R.string.aldi_usure) + "\n" + rip, false);
            cddr.show();
        }
    }


    private void exit() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag1 = new ProfileFragment();
        ft.replace(R.id.prf_constr, frag1, "proffy");
        ft.commit();
    }

}
