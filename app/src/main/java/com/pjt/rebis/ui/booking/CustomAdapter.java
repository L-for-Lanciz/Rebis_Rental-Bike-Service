package com.pjt.rebis.ui.booking;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.pjt.rebis.R;
import com.pjt.rebis.ui.bikes.Bicicletta;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private String key, owner;
    private ArrayList<Bicicletta> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Activity act;

    // data is passed into the constructor
    CustomAdapter(Activity act, Context context, ArrayList<Bicicletta> data, String key, String owner) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.key = key;
        this.act = act;
        this.owner = owner;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.bike_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mData.size() > 0) {
            Bicicletta bike = mData.get(position);
            holder.setBike(bike);
            holder.onClickList(bike);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View mView;

        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            itemView.setOnClickListener(this);
        }

        private void setBike(Bicicletta bk) {
            TextView modello = (TextView) mView.findViewById(R.id.bk_name);
            modello.setText(bk.getName());
            TextView brand = (TextView) mView.findViewById(R.id.bk_model);
            brand.setText(bk.getBrand());
            TextView year = (TextView) mView.findViewById(R.id.bk_yearp);
            year.setText(String.valueOf(bk.getYear()));
            TextView value = (TextView) mView.findViewById(R.id.bk_value);
            String valuer = bk.getValue() + " €";
            value.setText(valuer);
            TextView cnt = (TextView) mView.findViewById(R.id.bk_counter);
            String cnter = "rented: " + bk.getRentedCNT();
            cnt.setText(cnter);
            ImageView bar = (ImageView) mView.findViewById(R.id.bk_status);
            bar.setVisibility(View.GONE);
            ImageView img = (ImageView) mView.findViewById(R.id.bk_image);
            Picasso.get().load(bk.getImage()).into(img);
        }

        private void setBooker(String bkk) {
            TextView booker = (TextView) mView.findViewById(R.id.bk_customer);
            booker.setText(bkk);
        }

        private void onClickList(Bicicletta _bike) {
            final Bicicletta bike = _bike;
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    custom_dialogBK cdbk = new custom_dialogBK(act, key, bike, owner,
                            act.getString(R.string.adbo_title), act.getString(R.string.adbo_msg));
                    cdbk.show();
                }
            });
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Bicicletta getItem(int id) {
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
}