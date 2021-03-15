package com.pjt.rebis.webAPI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pjt.rebis.MainActivity;
import com.pjt.rebis.R;
import com.pjt.rebis.ui.history.RentalItem;

import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;


public class Transaction_metamask extends AppCompatActivity {
    private String DEEP_LINK_URL, DEEP_LINK_URL0;
    private String[] PAYLOAD_OBJECT;
    private AnimationDrawable animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_metamask);

        //https://metamask.app.link/send/pay-PUT_TO_ADDRESS@3?value=PUT_VALUEe18
        DEEP_LINK_URL0 = getIntent().getStringExtra("DEEPLINK");
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

        int cut;
        if ((rentobj.getID()+"").length() >= 4)
            cut = (rentobj.getID()+"").length() - 4;
        else
            cut = 0;

        String cutted = (rentobj.getID()+"").substring(cut);
        int cuttedInt = Integer.parseInt(cutted);

        String tempID = "";
        if (cuttedInt >= 0 && cuttedInt < 10)
            tempID = "000"+cutted;
        else if (cuttedInt >= 10 && cuttedInt < 100)
            tempID = "00"+cutted;
        else if (cuttedInt >= 100 && cuttedInt < 1000)
            tempID = "0"+cutted;
        else if (cuttedInt >= 1000 && cuttedInt < 10000)
            tempID = cutted;
        /*
        String[] linktemps = DEEP_LINK_URL.split("SPLIT");
        String valueandNonce = "";
        if (valueandNonce.contains("."))
            valueandNonce = linktemps[1]
        else
            valueandNonce = linktemps[1].concat(".00000000000000").concat(tempID);
        */
        String[] linktemps = DEEP_LINK_URL0.split("SPLIT");
        BigDecimal valueandNonce1 = Convert.toWei(linktemps[1], Convert.Unit.ETHER);
        String converted = valueandNonce1.toString();
Log.d("CNV1", "inWei: "+converted);
        String[] probfix = converted.split("\\.");
        String tmpval = (probfix[0].substring(0, probfix[0].length()-4)).concat(tempID);//.concat(".00");
Log.d("CNV2", "inWei+ID: "+tmpval);
        BigDecimal valueandNonce = (Convert.fromWei(tmpval, Convert.Unit.ETHER));
        BigDecimal valuenNonnDep = valueandNonce;//.add(new BigDecimal(rentobj.getDeposit()));
Log.d("CNV3", "inEth: "+valuenNonnDep);
        DEEP_LINK_URL = linktemps[0] + valuenNonnDep + "e18";
Log.d("DEEP", "LINK: "+DEEP_LINK_URL);

        Payload payobj = new Payload(rentobj, valuenNonnDep.toString());

        // send data to the server
        ImplementationAPI api = new ImplementationAPI();
        api.payTransaction(this, payobj);

        // DEEP LINK INTENT : START METAMASK TRANSACTION
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(DEEP_LINK_URL));
        startActivity(intent);

        Button menu = findViewById(R.id.txmm_toMenu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainmenu = new Intent(Transaction_metamask.this, MainActivity.class);
                startActivity(mainmenu);
                finish();
            }
        });
        menu.setHint(R.string.txmm_menubuthint);

        Button retry = findViewById(R.id.txmm_tryagain);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent trxInt = new Intent(Transaction_metamask.this, Transaction_metamask.class);
                trxInt.putExtra("DEEPLINK", DEEP_LINK_URL0);
                trxInt.putExtra("PAYLOAD_OBJECT", PAYLOAD_OBJECT);
                startActivity(trxInt);
                finish();
            }
        });
        retry.setHint(R.string.txmm_trybuthint);

        ImageView loading = findViewById(R.id.txmm_imbuffer);
        animation =(AnimationDrawable) loading.getDrawable();
        animation.start();
    }


}
