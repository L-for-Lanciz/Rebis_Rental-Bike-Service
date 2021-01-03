package com.pjt.rebis.ui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pjt.rebis.R;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import static android.app.Activity.RESULT_OK;

    /* Fragment 3 out of 3 for the Online Identification of the customer. Asks for a photo of a valid document of the
       customer, to validate given data. */
public class online_identific_3rd extends Fragment {
    private TextView picStatus;
    private Button addImg, complete;
    private ImageView anteprima;
    public static final int PICK_IMAGE = 1;
    private String immaginestringata;
    private String[] firebaseData;

    public online_identific_3rd(String[] data) {
        this.firebaseData = data;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View tevist3 =  inflater.inflate(R.layout.fragment_online_identific_3rd, container, false);

        picStatus = (TextView) tevist3.findViewById(R.id.oi_imageastatus);
        picStatus.setText(getString(R.string.oi_statusx));

        addImg = (Button) tevist3.findViewById(R.id.oi_addIMG);
        complete = (Button) tevist3.findViewById(R.id.oi_complete);

        anteprima = (ImageView) tevist3.findViewById(R.id.oi_IDpicture);

        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDataAndEndFragmnet();
            }
        });

        return tevist3;
    }

    private void checkDataAndEndFragmnet() {
        if (immaginestringata != null && !immaginestringata.equals("") && immaginestringata.length() > 10) {
            String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference mReference = FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuser).child("PersonalData");
            mReference.child("name").setValue(firebaseData[0]);
            mReference.child("surname").setValue(firebaseData[1]);
            mReference.child("gender").setValue(firebaseData[2]);
            mReference.child("birthdate").setValue(firebaseData[3]);
            mReference.child("country").setValue(firebaseData[4]);
            mReference.child("city").setValue(firebaseData[5]);
            mReference.child("postalCode").setValue(firebaseData[6]);
            mReference.child("homeAddress").setValue(firebaseData[7]);
            mReference.child("phoneNumber").setValue(firebaseData[8]);
            mReference.child("imageID").setValue(immaginestringata);

            showDialogPopUp();
        } else
            Toast.makeText(getContext(), getString(R.string.oi_errorImg), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                String path = imageUri.getPath();
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                anteprima.setImageBitmap(selectedImage);
                immaginestringata = BitMapToString(selectedImage);
                picStatus.setText(getString(R.string.oi_statusv) + path);
                //picStatus.setHeight();
                picStatus.setTextColor(Color.parseColor("#28b028"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this.getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this.getActivity(), "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    private void showDialogPopUp() {
        custom_dialogOI cdoi = new custom_dialogOI(getActivity());
        cdoi.show();
    }

}
