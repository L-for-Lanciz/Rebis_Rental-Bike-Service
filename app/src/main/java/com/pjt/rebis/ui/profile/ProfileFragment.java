package com.pjt.rebis.ui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pjt.rebis.Authentication.Login;
import com.pjt.rebis.Authentication.SaveSharedPreference;
import com.pjt.rebis.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

    /* 'main' fragment for the profile section. Provides information about the logged in account, like username, current address
    *   profile image, and a list of buttons to handle his wallet, change his picture, identify himself, or logout. */
public class ProfileFragment extends Fragment {
    public static final int PICK_IMAGE = 1;
    private ImageView propic;
    private TextView username, address, addW, changeW;
    private Button bt_wallets, bt_identification, bt_logout, bt_addimg;
    private String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private Uri profilepicture;
    private String cacheImg;
    private StorageReference mStorageRef;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        propic = root.findViewById(R.id.prf_imageView);
        bt_wallets = root.findViewById(R.id.but_wallets);
        bt_identification = root.findViewById(R.id.but_identif);
        bt_logout = root.findViewById(R.id.prf_logout);
        bt_addimg = root.findViewById(R.id.prf_plusimg);

        mStorageRef = FirebaseStorage.getInstance().getReference().child("USERS").child(currentuser).child("Bikes");

        if (SaveSharedPreference.getUserType(this.getActivity()).equals("renter"))
            bt_identification.setVisibility(View.INVISIBLE);

        bt_wallets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walletMag();
            }
        });

        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        bt_identification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identification();
            }
        });

        oiddedOrNot();

        cacheImg = SaveSharedPreference.getUserPropic(this.getActivity());
        propic.setImageBitmap(StringToBitMap(cacheImg));

        username = root.findViewById(R.id.prf_username);
        address = root.findViewById(R.id.prf_address);
        addW = root.findViewById(R.id.prf_addwall);
        changeW = root.findViewById(R.id.prf_changewall);

        username.setText(SaveSharedPreference.getUserName(this.getActivity()));

        addW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addaWallet();
            }
        });

        bt_addimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        changeW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walletCurrent();
            }
        });

        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference currentRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuser).child("Wallets").child("Current");
        currentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    String add = dataSnapshot.getValue(String.class);
                    if (add.length() > 0)
                        address.setText(add);
                    else
                        address.setText(getString(R.string.emptyadd));
                } catch(Exception e) {
                    address.setText(getString(R.string.emptyadd));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});

        return root;
    }

    private void walletMag() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag1 = new walletMagnifier();
        ft.replace(R.id.prf_constr, frag1, "magnify");
        ft.commit();
    }

    private void walletCurrent() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag1 = new walletCurrent();
        ft.replace(R.id.prf_constr, frag1, "currenty");
        ft.commit();
    }

    private void addaWallet() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag2 = new wallet_adder();
        ft.replace(R.id.prf_constr, frag2, "addy");
        ft.commit();
    }

    private void identification() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag3 = new online_identification();
        ft.replace(R.id.prf_constr, frag3, "identy");
        ft.commit();
    }

    private void oiddedOrNot() {
        DatabaseReference oiddedRef = FirebaseDatabase.getInstance().getReference()
                .child("USERS").child(currentuser).child("PersonalData").child("imageID");
        oiddedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    String ruo = dataSnapshot.getValue(String.class);
                    if (ruo.length() > 0)
                        bt_identification.setText(getString(R.string.oid_ruoidded));
                } catch(Exception e) { }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }});
    }

    private void logout() {
        Intent loggo = new Intent(this.getActivity(), Login.class);
        startActivity(loggo);
        FirebaseAuth.getInstance().signOut();
        SaveSharedPreference.clearPreferences(getActivity());
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                //Uri bikeImage = Uri.parse(bI);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                propic.setImageBitmap(bitmap);
                SaveSharedPreference.setUserPropic(this.getActivity(), BitMapToString(bitmap));
                StorageReference propicRef = mStorageRef.child("PersonalData").child("profileImage");
                uploadImage(imageUri, propicRef);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this.getActivity(), getString(R.string.generic_error), Toast.LENGTH_LONG).show();
            }

        } else {
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
                              .child(currentuser).child("PersonalData");
                      bikeRef.child("profileImage").setValue(downloadUri.toString());
                  } else {
                      // Handle failures
                  }
              }
            });
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    private void reloadFrag() {
        // Reload current fragment
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();
    }

}