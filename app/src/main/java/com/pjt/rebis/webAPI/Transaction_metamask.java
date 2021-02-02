package com.pjt.rebis.webAPI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.database.Transaction;
import com.pjt.rebis.MainActivity;
import com.pjt.rebis.R;
import com.pjt.rebis.ui.history.RentalItem;

import org.json.JSONObject;
import static java.lang.Thread.sleep;

public class Transaction_metamask extends AppCompatActivity {
    private String DEEP_LINK_URL;
    private String[] PAYLOAD_OBJECT;
    private AnimationDrawable animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_metamask);

        //https://metamask.app.link/send/pay-PUT_TO_ADDRESS@3?value=PUT_VALUEe18
        DEEP_LINK_URL = getIntent().getStringExtra("DEEPLINK");
        PAYLOAD_OBJECT = getIntent().getStringArrayExtra("PAYLOAD_OBJECT");
        RentalItem rentobj = new RentalItem(
                PAYLOAD_OBJECT[0],
                PAYLOAD_OBJECT[1],
                PAYLOAD_OBJECT[2],
                PAYLOAD_OBJECT[3],
                Integer.parseInt(PAYLOAD_OBJECT[4]),
                PAYLOAD_OBJECT[5],
                Integer.parseInt(PAYLOAD_OBJECT[6]),
                Double.parseDouble(PAYLOAD_OBJECT[7]),
                Double.parseDouble(PAYLOAD_OBJECT[8]),
                PAYLOAD_OBJECT[9],
                PAYLOAD_OBJECT[10]
        );
        Payload payobj = new Payload(rentobj, PAYLOAD_OBJECT[11]);

        // send data to the server
        ImplementationAPI api = new ImplementationAPI();
        api.payTransaction(this, payobj);

        // DEEP LINK INTENT : START METAMASK TRANSACTION
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(DEEP_LINK_URL));
        startActivity(intent);
        Log.d("####", "DeepLink: "+DEEP_LINK_URL);

        Button menu = findViewById(R.id.txmm_toMenu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainmenu = new Intent(Transaction_metamask.this, MainActivity.class);
                startActivity(mainmenu);
                finish();
            }
        });

        Button retry = findViewById(R.id.txmm_tryagain);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent trxInt = new Intent(Transaction_metamask.this, Transaction_metamask.class);
                trxInt.putExtra("DEEPLINK", DEEP_LINK_URL);
                trxInt.putExtra("PAYLOAD_OBJECT", PAYLOAD_OBJECT);
                startActivity(trxInt);
                finish();
            }
        });

        ImageView loading = findViewById(R.id.txmm_imbuffer);
        animation =(AnimationDrawable) loading.getDrawable();
        animation.start();
    }



/*  THIS IS GOING TO BE DONE SERVER-SIDE
    private void getDataOnEtherscan(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        resText.setText("Response: " + response.toString());
                        setJsonResult(response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", "Error Response: "+error.toString());
                        resText.setText("Response: " + error.toString());
                        setJsonResult("ERROR");
                    }
                });
        //Transaction_metamask.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void setJsonResult(String res) {
        this.jsonresult = res;
    }

    private String getJsonResult() {
        if (this.jsonresult==null)
            this.jsonresult="";

        return this.jsonresult;
    }

    private void workingThread() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    sleep(10000);
                } catch (Exception e) {}
                getDataOnEtherscan(urlGetTrx);

                String res="";
                while (res.equals("")) {
                    try {
                        sleep(2000);
                    } catch (Exception e) {
                    }
                    res = getJsonResult();

                    if (true){

                    } else {

                    }
                }
                Log.d("@TRX@", "RES: " + res);
            }
        }).start();
    }
 */
}
