package com.pjt.rebis.ui.profile;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.pjt.rebis.R;
import java.util.Calendar;

    /* Fragment 1 out of 3 for the Online Identification of the customer. Asks for the customer personal data. */
public class online_identification extends Fragment implements AdapterView.OnItemSelectedListener {
    private EditText date, name, surname;
    private CheckBox male, female;
    private Spinner spinner;
    private Button prosid;
    private String[] countries;
    private ArrayAdapter<String>  adapterSpin;
    private String nome, cognome, genere, data, paese;

    public online_identification() {
        countries = new String[]{
                "Choose a country","Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda",
                "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus",
                "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegowina", "Botswana", "Bouvet Island", "Brazil",
                "British Indian Ocean Territory", "Brunei Darussalam", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada",
                "Cape Verde", "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island", "Cocos (Keeling) Islands",
                "Colombia", "Comoros", "Congo", "Congo, the Democratic Republic of the", "Cook Islands", "Costa Rica", "Cote d'Ivoire",
                "Croatia (Hrvatska)", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "East Timor",
                "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Falkland Islands (Malvinas)",
                "Faroe Islands", "Fiji", "Finland", "France", "France Metropolitan", "French Guiana", "French Polynesia",
                "French Southern Territories", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada",
                "Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Heard and Mc Donald Islands",
                "Holy See (Vatican City State)", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran (Islamic Republic of)",
                "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, Democratic People's Republic of",
                "Korea, Republic of", "Kuwait", "Kyrgyzstan", "Lao, People's Democratic Republic", "Latvia", "Lebanon", "Lesotho", "Liberia",
                "Libyan Arab Jamahiriya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia, The Former Yugoslav Republic of",
                "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania", "Mauritius",
                "Mayotte", "Mexico", "Micronesia, Federated States of", "Moldova, Republic of", "Monaco", "Mongolia", "Montserrat", "Morocco",
                "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand",
                "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Panama",
                "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania",
                "Russian Federation", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino",
                "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Seychelles", "Sierra Leone", "Singapore", "Slovakia (Slovak Republic)", "Slovenia",
                "Solomon Islands", "Somalia", "South Africa", "South Georgia and the South Sandwich Islands", "Spain", "Sri Lanka", "St. Helena",
                "St. Pierre and Miquelon", "Sudan", "Suriname", "Svalbard and Jan Mayen Islands", "Swaziland", "Sweden", "Switzerland", "Syrian Arab Republic",
                "Taiwan, Province of China", "Tajikistan", "Tanzania, United Republic of", "Thailand", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago",
                "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom",
                "United States", "United States Minor Outlying Islands", "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam", "Virgin Islands (British)",
                "Virgin Islands (U.S.)", "Wallis and Futuna Islands", "Western Sahara", "Yemen", "Yugoslavia", "Zambia", "Zimbabwe"
        };
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View tevist =  inflater.inflate(R.layout.fragment_online_identification, container, false);

        name = (EditText) tevist.findViewById(R.id.oi_inpname);
        surname = (EditText) tevist.findViewById(R.id.oi_inpsurname);

        male = (CheckBox) tevist.findViewById(R.id.oi_male);
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                female.setChecked(false);
            }
        });
        female = (CheckBox) tevist.findViewById(R.id.oi_female);
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                male.setChecked(false);
            }
        });

        date = (EditText) tevist.findViewById(R.id.oi_inpdate);
        date.addTextChangedListener(tw);

        spinner = tevist.findViewById(R.id.oi_spinner);
        adapterSpin = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, countries);
        spinner.setAdapter(adapterSpin);

        prosid = (Button) tevist.findViewById(R.id.oi_proceed);
        prosid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDataAndProceed();
            }
        });

        return tevist;
    }


    TextWatcher tw = new TextWatcher() {
        private String current = "";
        private String ddmmyyyy = "DDMMYYYY";
        private Calendar cal = Calendar.getInstance();

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals(current)) {
                String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                String cleanC = current.replaceAll("[^\\d.]|\\.", "");
                int cl = clean.length();
                int sel = cl;
                for (int i = 2; i <= cl && i < 6; i += 2) {
                    sel++;
                }
                //Fix for pressing delete next to a forward slash
                if (clean.equals(cleanC)) sel--;
                if (clean.length() < 8) {
                    clean = clean + ddmmyyyy.substring(clean.length());
                } else {
                    //This part makes sure that when we finish entering numbers
                    //the date is correct, fixing it otherwise
                    int day = Integer.parseInt(clean.substring(0, 2));
                    int mon = Integer.parseInt(clean.substring(2, 4));
                    int year = Integer.parseInt(clean.substring(4, 8));

                    mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                    cal.set(Calendar.MONTH, mon - 1);
                    year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                    cal.set(Calendar.YEAR, year);
                    // ^ first set year for the line below to work correctly
                    //with leap years - otherwise, date e.g. 29/02/2012
                    //would be automatically corrected to 28/02/2012

                    day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                    clean = String.format("%02d%02d%02d", day, mon, year);
                }
                clean = String.format("%s/%s/%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8));
                sel = sel < 0 ? 0 : sel;
                current = clean;
                date.setText(current);
                date.setSelection(sel < current.length() ? sel : current.length());
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private void checkDataAndProceed() {
        // Retrieve all data
        nome = name.getText().toString();
        cognome = surname.getText().toString();
        data = date.getText().toString();

        spinner.setOnItemSelectedListener(this);
        int choice = spinner.getSelectedItemPosition();
        paese = countries[choice];

        if (male.isChecked())
            genere = "male";
        else //if female is checked
            genere= "female";

        // Now check data validity
        boolean nomeOK =  (nome.length()>1) && (nome.length()<20);
        boolean cognomeOK = (cognome.length()>1) && (cognome.length()<20);
        boolean paeseOK = (choice != 0);
        int getYear=0;
        if (data.length()>6)
            getYear = Integer.parseInt(data.substring(6));
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        boolean dataOK = (getYear +9 < currentYear) && (getYear >= currentYear -100);

        if ( nomeOK && cognomeOK && paeseOK && dataOK ) {
            // Store given personal data
            String[] personData = new String[5];
            personData[0] = nome;
            personData[1] = cognome;
            personData[2] = genere;
            personData[3] = data;
            personData[4] = paese;

            goToTheSecondPart(personData);

        } else if (!nomeOK) {
            name.setError(getString(R.string.oi_errorName));
        } else if (!cognomeOK) {
            surname.setError(getString(R.string.oi_errorSurname));
        } else if (!paeseOK) {
            Toast.makeText(getContext(), getString(R.string.oi_errorCountry), Toast.LENGTH_SHORT).show();
        } else if (!dataOK) {
            date.setError(getString(R.string.oi_errorDate));
        } else
            Toast.makeText(getContext(), getString(R.string.oi_error), Toast.LENGTH_SHORT).show();

    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        spinner.getItemAtPosition(pos);
    }
    public void onNothingSelected(AdapterView<?> parent) { // Another interface callback
    }

    private void goToTheSecondPart(String[] data) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag1 = new online_identific_2nd(data);
        ft.replace(R.id.oi_constr, frag1, "second_oi");
        ft.commit();
    }

}
