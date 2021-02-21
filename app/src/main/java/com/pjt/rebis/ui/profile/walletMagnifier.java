package com.pjt.rebis.ui.profile;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.pjt.rebis.ui.welcomeFrag;

import org.web3j.crypto.WalletUtils;

/* Fragment which allows the user to view all the given addresses. Here, the user can also delete one. */
public class walletMagnifier extends Fragment {
    private TextView corrente;
    private Button addW;
    private EditText addressnew;
    private FirebaseRecyclerAdapter<WalletItem, PassViewHolder> mPeopleRVAdapter;
    private String currentuser;
    private int container;

    public walletMagnifier() {
    }
    public walletMagnifier(int _container) {
        container = _container;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_wallet_magnifier, container, false);

        corrente = fragmentView.findViewById(R.id.wama_address);
        addW = fragmentView.findViewById(R.id.adwa_add);
        addressnew = fragmentView.findViewById(R.id.adwa_address);

        Toolbar myToolbar = (Toolbar) getActivity().findViewById(R.id.myToolbar);
        ((TextView) myToolbar.findViewById(R.id.toolbarTextView)).setText(getString(R.string.wallets));

        addW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addaWallet();
            }
        });

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
        final DatabaseReference currentRef = mDatabase.child("Current");

        mDatabase.keepSynced(true);
        mPeopleRV = (RecyclerView) fragmentView.findViewById(R.id.wama_Recyc);

        DatabaseReference personsRef = mDatabase.child("List");
        Query personsQuery = personsRef.orderByValue();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

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

                String formatted = (model.getAddress()).substring(0,10).concat("...").concat((model.getAddress()).substring(32, 42));
                if (formatted.equals(corrente.getText().toString()))
                    holder.mView.findViewById(R.id.itemcu_star).setBackgroundResource(R.drawable.star1);
                else
                    holder.mView.findViewById(R.id.itemcu_star).setBackgroundResource(R.drawable.star0);

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
                        .inflate(R.layout.walletma_item, parent, false);

                return new PassViewHolder(view);
            }
        };

        mPeopleRV.setAdapter(mPeopleRVAdapter);

        currentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String full = dataSnapshot.getValue(String.class);
                String formatted = full.substring(0,10).concat("...").concat(full.substring(32, 42));
                corrente.setText(formatted);
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
            String wallet = _wallet.substring(0, 10).concat("...").concat(_wallet.substring(32, 42));
            walletgui.setText(wallet);
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

    private void addaWallet() {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuser).child("Wallets");
        DatabaseReference listRef = mRef.child("List");

        String input = addressnew.getText().toString();
        boolean isAddr = WalletUtils.isValidAddress(input);
        if (!isAddr) {
            addressnew.setError("Input a valid address");
        } else {
            listRef.child(input).child("address").setValue(input);
            addressnew.setText("");
        }
    }

    private void exit() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag1 = new welcomeFrag();
        ft.replace(container, frag1, "welcome").addToBackStack("welcome");
        ft.commit();
    }

    private void reloadFrag() {
        // Reload current fragment
        Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("magnify");
        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }

}
