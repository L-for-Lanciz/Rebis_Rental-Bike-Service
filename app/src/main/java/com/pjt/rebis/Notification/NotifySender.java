package com.pjt.rebis.Notification;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pjt.rebis.Utility.SaveSharedPreference;
import com.pjt.rebis.R;
import com.pjt.rebis.ui.history.RentalItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NotifySender {
    private String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public static ArrayList<RentalItem> notificationList = new ArrayList<RentalItem>();
    private Activity act;
    private Context ctx;
    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;
    public static boolean mutex = true;

    public NotifySender() {
    }

    public NotifySender(Activity _act, Context _ctx) {
        this.act = _act;
        this.ctx = _ctx;
        createNotificationChannel();
        setNotificationEvenHandler();
        notificationList.clear();
    }

    public void checkForNotificationToPrompt() {
            String _user = SaveSharedPreference.getUserName(ctx);
            final String _type = SaveSharedPreference.getUserType(ctx);
            DatabaseReference personsRef = FirebaseDatabase.getInstance().getReference().child("RENTALS");
            Query personsQuery;
            if (_type.equals("renter"))
                personsQuery = personsRef.orderByChild("Renter").equalTo(_user+"#@&@#"+currentuser);
            else
                personsQuery = personsRef.orderByChild("Customer").equalTo(_user+"#@&@#"+currentuser);

            personsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {

                        for (DataSnapshot rentalsnapshot : dataSnapshot.getChildren()) {
                            RentalItem rentalobj = rentalsnapshot.getValue(RentalItem.class);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                            Date date = new Date();
                            Calendar c = Calendar.getInstance();
                            c.setTime(sdf.parse(rentalobj.getDate()));
                            c.add(Calendar.DATE, rentalobj.getDays());  // number of days to add
                            String rendatetmp = sdf.format(c.getTime());
                            Date rendate = sdf.parse(rendatetmp);

                            if (_type.equals("renter") && (!rentalobj.getState().equals("ended"))) {
                                if (date.compareTo(rendate) > 0) {
                                    notificationList.add(rentalobj);
                                    createNewNotificationRENTER(ctx, rentalobj.getID(), rentalobj, true);
                                } else if (date.compareTo(rendate) == 0) {
                                    notificationList.add(rentalobj);
                                    createNewNotificationRENTER(ctx, rentalobj.getID(), rentalobj, false);
                                }

                            } else if (_type.equals("customer") && (!rentalobj.getState().equals("ended"))) {
                                if (date.compareTo(rendate) > 0) {
                                    notificationList.add(rentalobj);
                                    createNewNotificationCUSTOMER(ctx, rentalobj.getID(), rentalobj, true);
                                } else if (date.compareTo(rendate) == 0) {
                                    notificationList.add(rentalobj);
                                    createNewNotificationCUSTOMER(ctx, rentalobj.getID(), rentalobj, false);
                                }

                            }
                            mutex = false;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "bip"; //getString(R.string.channel_name);
            String description = "bop"; //getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 2000});
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = act.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNewNotificationRENTER(Context ctx, int id, RentalItem robj, boolean expired) {
        String[] customer = robj.getCustomer().split("#@&@#");
        String bike = robj.getBike().substring(3);
        String notificationTitle;
        if (expired) {
            notificationTitle = "A rental is already expired";
        } else {
            notificationTitle = "A Rental is expiring today";
        }
        String notificationBody;
        if (expired) {
            notificationBody = "The bike '"+bike+"' should have been already returned from '"+customer[0]+"'.";
        } else {
            notificationBody = "The bike '"+bike+"' should be returned from '"+customer[0]+"' today.";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, "CHANNEL_ID")
                .setSmallIcon(R.drawable.notifypic)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(notificationBody))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
               // .addAction(0, "DISMISS", dismissPendingIntent)
               //     .setAutoCancel(true);

        notificationManager.notify(id, builder.build());
    }

    private void createNewNotificationCUSTOMER(Context ctx, int id, RentalItem robj, boolean expired) {
        String[] renter = robj.getRenter().split("#@&@#");
        String bike = robj.getBike().substring(3);
        String notificationTitle;
        if (expired) {
            notificationTitle = "A rental is already expired";
        } else {
            notificationTitle = "A Rental is expiring today";
        }
        String notificationBody;
        if (expired) {
            notificationBody = "Your rental of '" + bike + "' from '" + renter[0] + "' is already expired." +
                                "\nPlease return it as soon as possible.";
        } else {
            notificationBody = "Your rental of '" + bike + "' from '" + renter[0] + "' ends today." +
                                "\nPlease return it as soon as possible.";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, "CHANNEL_ID")
                .setSmallIcon(R.drawable.notifypic)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(notificationBody))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
              //  .addAction(0, "DISMISS", dismissPendingIntent)
              //      .setAutoCancel(true);

        notificationManager.notify(id, builder.build());
    }

    private void setNotificationEvenHandler() {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(ctx, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent = PendingIntent.getActivity(ctx, 0, intent, 0);

      //  Intent dismissIntent = new Intent(ctx, NotificationActivity.class);
      //  dismissPendingIntent = PendingIntent.getActivity(ctx, 0, dismissIntent, 0);
    }

}
