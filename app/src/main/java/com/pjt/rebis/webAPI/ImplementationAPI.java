package com.pjt.rebis.webAPI;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pjt.rebis.R;
import com.pjt.rebis.ui.history.RentalItem;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

    /* Class which handles and allows the communication between the API and the mobile application. */
public class ImplementationAPI {
    private InterfaceAPI api;
    private Calendar cal = Calendar.getInstance();
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");


    public ImplementationAPI() {
        //Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.15:3100/") //local url ipv4: http://192.168.1.x:3000
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(InterfaceAPI.class);
    }

    public void payTransaction(Context _ctx, Payload item) {
        final Payload payloadobj = item;
        final RentalItem rentalobj = payloadobj.getRentalItem();
        final Context ctx = _ctx;

        Call<Payload> call = api.createRental(payloadobj);
        call.enqueue(new Callback<Payload>() {
            @Override
            public void onResponse(Call<Payload> call, Response<Payload> response) {
                Payload item = response.body();
                RentalItem itm = item.rentalItem;
                int status = response.code();
                Log.e("STATUS_CODE", status+"");
                if (itm != null && status==200) {
                    double payed = itm.getFee() + itm.getDeposit();
                    Log.i("RESPONSE", "OUTPUT: " + payed);
           //         String trxresult = ctx.getString(R.string.trx_suxes) +" "+ payed + " ETH";
           //         Toast.makeText(ctx, trxresult, Toast.LENGTH_LONG).show();
                    updRentalOnDatabase(rentalobj);
                } else {
                    Toast.makeText(ctx, ctx.getString(R.string.trx_failed), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Payload> call, Throwable t) {
                Log.e("FAILURE", "ERROR: " + t.toString());
                Toast.makeText(ctx, ctx.getString(R.string.trx_failed), Toast.LENGTH_LONG).show();
            }

        });
    }

    public void endTransaction(Context _ctx, Payload item, String _currentuser) {
        final Payload payloadobj = item;
        final RentalItem rentalobj = payloadobj.getRentalItem();
        final Context ctx = _ctx;
        final String __currentuser = _currentuser;
        Call<Payload> call = api.endingRental(payloadobj);
        call.enqueue(new Callback<Payload>() {
            @Override
            public void onResponse(Call<Payload> call, Response<Payload> response) {
                Payload item = response.body();
                RentalItem itm = item.getRentalItem();
                int status = response.code();
                Log.e("STATUS_CODE", status+"");
                if (itm != null && status==200) {
                    String endresult = ctx.getString(R.string.trx_endOK) + itm.getDeposit() + " ETH";
                    Toast.makeText(ctx, endresult, Toast.LENGTH_LONG).show();
                    updRentalOnEndingOnDatabase(rentalobj, __currentuser);
                } else {
                    Toast.makeText(ctx, ctx.getString(R.string.trx_failed), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Payload> call, Throwable t) {
                Log.e("FAILURE", "ERROR: " + t.toString());
                Toast.makeText(ctx, ctx.getString(R.string.trx_failed), Toast.LENGTH_LONG).show();
            }

        });
    }

    private void updRentalOnDatabase(RentalItem obj) {
        int rid = obj.getID();
        String customer[] = obj.getCustomer().split("#@&@#");
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("RENTALS").child(rid+"");
        mDatabase.child("State").setValue("rented");
        //mDatabase.child("Customer").setValue(obj.getCustomer());      NOW DONE AT SERVER SIDE
        mDatabase.child("Addresscustomer").setValue(obj.getAddressCustomer());

        String[] tmp = obj.getRenter().split("#@&@#");
        final DatabaseReference mBikeRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(tmp[1])
                .child("Bikes").child(obj.getBike());
        mBikeRef.child("status").setValue("unavailable");

        try {
            Date date1 = new SimpleDateFormat("yyyy/MM/dd").parse(obj.getDate());
            cal.setTime(date1);
            cal.add(Calendar.DAY_OF_MONTH, obj.getDays());
            Date finalDate = cal.getTime();
            mBikeRef.child("customer").setValue(customer[0]+"#@&@#"+dateFormat.format(finalDate));
        } catch (Exception e) {
        }

        mBikeRef.child("rentedCNT").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer scnt = dataSnapshot.getValue(Integer.class);
                try {
                    //int cnt = Integer.parseInt(scnt);
                    mBikeRef.child("rentedCNT").setValue(scnt+1);
                } catch (Exception fd) {
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});
    }

    private void updRentalOnEndingOnDatabase(RentalItem item, String currentuser) {
        //DatabaseReference endRef = FirebaseDatabase.getInstance().getReference().child("RENTALS").child(item.getID() + "");
        //endRef.child("State").setValue("ended");      NOW DONE AT SERVER SIDE

        DatabaseReference bikeRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuser)
                .child("Bikes").child(item.getBike());
        bikeRef.child("status").setValue("available");
        bikeRef.child("customer").setValue("");
    }

}
