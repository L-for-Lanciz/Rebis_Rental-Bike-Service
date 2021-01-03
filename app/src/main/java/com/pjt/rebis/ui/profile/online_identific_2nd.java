package com.pjt.rebis.ui.profile;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.pjt.rebis.R;
import java.util.Locale;

    /* Fragment 2 out of 3 for the Online Identification of the customer. Asks for other data. */
public class online_identific_2nd extends Fragment implements AdapterView.OnItemSelectedListener {
    private EditText city, code, address, noho, number;
    private Spinner SPprefix;
    private String[] pregress, prefixes;
    private ArrayAdapter<String> adapterSpin;
    private Button proceed;
    private String citta, postale, indirizzo, civico, prefisso, telefono;

    public online_identific_2nd(String[] datip) {
        this.pregress = datip;

        String localISO = Locale.getDefault().getCountry();

        prefixes = new String[]{
            "Choose", "AC: +247", "AD: +376", "AE: +971", "AF: +93", "AG: +1-268", "AI: +1-264", "AL: +355", "AM: +374", "AN: +599",
            "AO: +244", "AR: +54", "AS: +1-684", "AT: +43", "AU: +61", "AW: +297", "AX: +358-18", "AZ: +374-97", "AZ: +994", "BA: +387",
            "BB: +1-246", "BD: +880", "BE: +32", "BF: +226", "BG: +359", "BH: +973", "BI: +257", "BJ: +229", "BM: +1-441", "BN: +673",
            "BO: +591", "BR: +55", "BS: +1-242", "BT: +975", "BW: +267", "BY: +375", "BZ: +501", "CA: +1", "CC: +61", "CD: +243", "CF: +236",
            "CG: +242", "CH: +41", "CI: +225", "CK: +682", "CL: +56", "CM: +237", "CN: +86", "CO: +57", "CR: +506", "CS: +381", "CU: +53",
            "CV: +238", "CX: +61", "CY: +90-392", "CY: +357", "CZ: +420", "DE: +49", "DJ: +253", "DK: +45", "DM: +1-767",
            "DO: +1-809", "DZ: +213", "EC: +593", "EE: +372", "EG: +20", "EH: +212", "ER: +291", "ES: +34", "ET: +251", "FI: +358", "FJ: +679",
            "FK: +500", "FM: +691", "FO: +298", "FR: +33", "GA: +241", "GB: +44", "GD: +1-473", "GE: +995", "GF: +594", "GG: +44", "GH: +233",
            "GI: +350", "GL: +299", "GM: +220", "GN: +224", "GP: +590", "GQ: +240", "GR: +30", "GT: +502", "GU: +1-671", "GW: +245", "GY: +592",
            "HK: +852", "HN: +504", "HR: +385", "HT: +509", "HU: +36", "ID: +62", "IE: +353", "IL: +972", "IM: +44", "IN: +91", "IO: +246",
            "IQ: +964", "IR: +98", "IS: +354", "IT: +39", "JE: +44", "JM: +1-876", "JO: +962", "JP: +81", "KE: +254", "KG: +996", "KH: +855",
            "KI: +686", "KM: +269", "KN: +1-869", "KP: +850", "KR: +82", "KW: +965", "KY: +1-345", "KZ: +7", "LA: +856", "LB: +961", "LC: +1-758",
            "LI: +423", "LK: +94", "LR: +231", "LS: +266", "LT: +370", "LU: +352", "LV: +371", "LY: +218", "MA: +212", "MC: +377", "MD: +373-533",
            "MD: +373", "ME: +382", "MG: +261", "MH: +692", "MK: +389", "ML: +223", "MM: +95", "MN: +976", "MO: +853", "MP: +1-670", "MQ: +596",
            "MR: +222", "MS: +1-664", "MT: +356", "MU: +230", "MV: +960", "MW: +265", "MX: +52", "MY: +60", "MZ: +258", "NA: +264", "NC: +687",
            "NE: +227", "NF: +672", "NG: +234", "NI: +505", "NL: +31", "NO: +47", "NP: +977", "NR: +674", "NU: +683", "NZ: +64", "OM: +968",
            "PA: +507", "PE: +51", "PF: +689", "PG: +675", "PH: +63", "PK: +92", "PL: +48", "PM: +508", "PR: +1-787", "PS: +970", "PT: +351",
            "PW: +680", "PY: +595", "QA: +974", "RE: +262", "RO: +40", "RS: +381", "RU: +7", "RW: +250", "SA: +966", "SB: +677", "SC: +248",
            "SD: +249", "SE: +46", "SG: +65", "SH: +290", "SI: +386", "SJ: +47", "SK: +421", "SL: +232", "SM: +378", "SN: +221", "SO: +252",
            "SO: +252", "SR: +597", "ST: +239", "SV: +503", "SY: +963", "SZ: +268", "TA: +290", "TC: +1-649", "TD: +235", "TG: +228", "TH: +66",
            "TJ: +992", "TK: +690", "TL: +670", "TM: +993", "TN: +216", "TO: +676", "TR: +90", "TT: +1-868", "TV: +688", "TW: +886", "TZ: +255",
            "UA: +380", "UG: +256", "US: +1", "UY: +598", "UZ: +998", "VA: +379", "VC: +1-784", "VE: +58", "VG: +1-284", "VI: +1-340", "VN: +84",
            "VU: +678", "WF: +681", "WS: +685", "YE: +967", "YT: +262", "ZA: +27", "ZM: +260", "ZW: +263"
        };

        for (int i=1; i<prefixes.length; i++) {
            if (localISO.equals(prefixes[i].substring(0,2))) {
                prefixes[0] = prefixes[i];
                break;
            }
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View tevist2 =  inflater.inflate(R.layout.fragment_online_identific_2nd, container, false);

        city = (EditText) tevist2.findViewById(R.id.oi_inpcity);
        code = (EditText) tevist2.findViewById(R.id.oi_inppostal);
        address = (EditText) tevist2.findViewById(R.id.oi_inpaddress);
        noho = (EditText) tevist2.findViewById(R.id.oi_inpnoho);
        number = (EditText) tevist2.findViewById(R.id.oi_inpnumber);

        SPprefix = (Spinner) tevist2.findViewById(R.id.oi_prefixph);
        adapterSpin = new ArrayAdapter<>(getActivity(), R.layout.prefixspinner_item, prefixes);
        SPprefix.setAdapter(adapterSpin);

        proceed = (Button) tevist2.findViewById(R.id.oi_2NDproceed);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDataAndProceed();
            }
        });

        return tevist2;
    }

    private void checkDataAndProceed() {
        // Retrieve all data
        citta = city.getText().toString();
        postale = code.getText().toString();
        indirizzo = address.getText().toString();
        civico = noho.getText().toString();
        telefono = number.getText().toString();

        SPprefix.setOnItemSelectedListener(this);
        int scelta = SPprefix.getSelectedItemPosition();
        prefisso = prefixes[scelta];

        // Check data validity
        boolean cittaOK = (citta.length()>1) && (citta.length()<30);
        int mega=0;
        try {
            mega = Integer.parseInt(postale);
        } catch (Exception e) {}
        boolean postaleOK = mega >0;
        boolean indirizzoOK = indirizzo.length() > 3;
        boolean civicoOK = civico.length() >0;
        boolean telefonoOK = (telefono.length() > 3) && (telefono.length() < 20);

        if ( cittaOK && postaleOK && indirizzoOK && civicoOK && telefonoOK ) {
            String[] data = new String[9];
            // DATA = NAME, SURNAME, GENDER, BIRTH, COUNTRY, CITY, ZIPCODE, HOMEADDRESS, PHONENUMBER
            String homeaddress = indirizzo +" "+ civico;
            String phonenumber = prefisso +" "+ telefono;

            for (int i=0; i<pregress.length; i++)
                data[i] = pregress[i];

            data[5] = citta;
            data[6] = postale;
            data[7] = homeaddress;
            data[8] = phonenumber;

            goToTheThirdPart(data);
        } else if (!cittaOK) {
            city.setError(getString(R.string.oi_errorCity));
        } else if (!postaleOK) {
            code.setError(getString(R.string.oi_errorCode));
        } else if (!indirizzoOK) {
            address.setError(getString(R.string.oi_errorAddress));
        } else if (!civicoOK) {
            noho.setError(getString(R.string.oi_errorNoho));
        } else if (!telefonoOK) {
            number.setError(getString(R.string.oi_errorNumber));
        } else
            Toast.makeText(getContext(), getString(R.string.oi_error), Toast.LENGTH_SHORT).show();

    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        SPprefix.getItemAtPosition(pos);
    }
    public void onNothingSelected(AdapterView<?> parent) { // Another interface callback
    }

    private void goToTheThirdPart(String[] data) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag1 = new online_identific_3rd(data);
        ft.replace(R.id.oi2nd_constr, frag1, "third_oi");
        ft.commit();
    }

}
