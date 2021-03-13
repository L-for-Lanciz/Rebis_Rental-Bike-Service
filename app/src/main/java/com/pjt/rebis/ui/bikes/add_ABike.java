package com.pjt.rebis.ui.bikes;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pjt.rebis.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class add_ABike extends Fragment {
    private EditText model, brand, year, value;
    private Button exit, confirm, image;
    private String modello, marca, anno, valore;
    private Uri _immagine;
    private int _year;
    private double _value;
    public static final int PICK_IMAGE = 1;
    private StorageReference mStorageRef;
    private String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String tmp;

    public add_ABike() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View frootto = inflater.inflate(R.layout.fragment_add__abike, container, false);

        mStorageRef = FirebaseStorage.getInstance().getReference().child("USERS").child(currentuser).child("Bikes");
        model = frootto.findViewById(R.id.adb_inpmodel);
        brand = frootto.findViewById(R.id.adb_inpbrand);
        year = frootto.findViewById(R.id.adb_inpyear);
        value = frootto.findViewById(R.id.adb_inpvalue);

        exit = frootto.findViewById(R.id.adb_exit);
        confirm = frootto.findViewById(R.id.adb_conf);
        image = frootto.findViewById(R.id.adb_uplpic);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImg();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInputValidity();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitFrg();
            }
        });

        return frootto;
    }

    private void addImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    private void saveBikeInstance() {
        try {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuser).child("Bikes");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int totalNumber = (int) dataSnapshot.getChildrenCount();
                    totalNumber = totalNumber+1;

                    tmp = totalNumber+ "- " + modello;
                    StorageReference picRef = mStorageRef.child(tmp);
                    uploadImage(getFirstURI(), picRef);

                    DatabaseReference bikeRef = FirebaseDatabase.getInstance().getReference().child("USERS")
                            .child(currentuser).child("Bikes").child(tmp);
                    bikeRef.child("brand").setValue(marca);
                    bikeRef.child("name").setValue(modello); //CORRESPONDS TO 'MODEL'
                    bikeRef.child("rentedCNT").setValue(0);
                    bikeRef.child("status").setValue("available");
                    bikeRef.child("value").setValue(_value);
                    bikeRef.child("year").setValue(_year);
                    bikeRef.child("bikeIDRef").setValue(tmp);

                    exitFrg();
               }
               @Override
               public void onCancelled(DatabaseError databaseError) {
               }});

        } catch (Exception szighi) {
            Toast.makeText(getContext(), getString(R.string.generic_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkInputValidity() {
        modello = model.getText().toString();
        marca = brand.getText().toString();
        anno = year.getText().toString();
        valore = value.getText().toString();

        boolean moCHECK = (modello.length() > 0);
        boolean forbiddenCHECK = (!modello.contains(".")) && (!modello.contains("$")) && (!modello.contains("#")) &&
                                (!modello.contains("[")) && (!modello.contains("]")) && (!modello.contains("/"));
        boolean maCHECK = (marca.length() > 0);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        try {
            _year = Integer.parseInt(anno);
        } catch (Exception fdffs) {
            _year = 0;
        }
        boolean anCHECK = (_year > 1950) && (_year <= currentYear);

        try {
            _value = Double.parseDouble(valore);
        } catch (Exception fndngfsd) {
            _value = 0;
        }
        boolean vaCHECK = (_value > 0);

        boolean imCHECK = (getFirstURI()!=null);

        if (moCHECK && maCHECK && anCHECK && vaCHECK && imCHECK && forbiddenCHECK)
            saveBikeInstance();
        else if (!moCHECK) {
            model.setError(getString(R.string.adb_errorMO));
        } else if (!maCHECK) {
            brand.setError(getString(R.string.adb_errorBR));
        } else if (!anCHECK) {
            year.setError(getString(R.string.adb_errorYE));
        } else if (!vaCHECK) {
            value.setError(getString(R.string.adb_errorVA));
        } else if (!imCHECK) {
            Toast.makeText(getContext(), getString(R.string.adb_errorIM), Toast.LENGTH_SHORT).show();
        } else if (!forbiddenCHECK) {
            Toast.makeText(getContext(), getString(R.string.adb_errorFORB), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), getString(R.string.adb_errorGE), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                image.setBackgroundResource(R.drawable.img_added);
                setFirstURI(imageUri);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this.getActivity(), getString(R.string.generic_error), Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this.getActivity(), "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    private void uploadImage(Uri _mguri, StorageReference mRef) {
        final StorageReference mInnerRef = mRef;
        UploadTask uploadTask = mRef.putFile(_mguri);
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
            }
        });

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return mInnerRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    DatabaseReference bikeRef = FirebaseDatabase.getInstance().getReference().child("USERS")
                            .child(currentuser).child("Bikes").child(tmp);
                    bikeRef.child("image").setValue(downloadUri.toString());
                } else {
                    // Handle failures
                }
            }
        });
    }

    private void setFirstURI(Uri imgg) {
        this._immagine = imgg;
    }

    private Uri getFirstURI() {
        return _immagine;
    }

    private void exitFrg() {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.navigation_bikes);
    }

}
