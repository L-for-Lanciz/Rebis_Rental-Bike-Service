package com.pjt.rebis.WebAPI;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.pjt.rebis.R;
import com.pjt.rebis.ui.history.RentalItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

    /* Class which handles and allows the communication between the API and the mobile application. */
public class ImplementationAPI {
    private InterfaceAPI api;

    public ImplementationAPI() {
        //Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.49:3100/") //local url ipv4: http://192.168.1.x:3000
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(InterfaceAPI.class);
    }

    public void post(Context _ctx, RentalItem item) {
        final RentalItem rentalobj = item;
        final Context ctx = _ctx;

        Call<RentalItem> call = api.createRental(rentalobj);
        call.enqueue(new Callback<RentalItem>() {
            @Override
            public void onResponse(Call<RentalItem> call, Response<RentalItem> response) {
                RentalItem itm = response.body();
                int status = response.code();
                Log.e("STATUS_CODE", status+"");
                if (itm != null && status==200) {
                    double payed = (itm.getFee()) + (itm.getDeposit());
                    Log.i("RESPONSE", "OUTPUT: " + payed);
                    String trxresult = ctx.getString(R.string.trx_suxes) +" "+ payed + " ETH";
                    Toast.makeText(ctx, trxresult, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ctx, ctx.getString(R.string.trx_failed), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RentalItem> call, Throwable t) {
                Log.e("FAILURE", "ERROR: " + t.toString());
                Toast.makeText(ctx, ctx.getString(R.string.trx_failed), Toast.LENGTH_LONG).show();
            }

        });
    }

}
