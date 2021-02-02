package com.pjt.rebis.notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pjt.rebis.MainActivity;
import com.pjt.rebis.R;
import com.pjt.rebis.ui.history.RentalItem;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity implements Notification_RecyclerViewAdapter.ItemClickListener {
    private ArrayList<RentalItem> notificationList = new ArrayList<RentalItem>();
    private Notification_RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Button exit = (Button) findViewById(R.id.notf_drop);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMain();
            }
        });


        notificationList = NotifySender.notificationList;
        RecyclerView recyclerView = findViewById(R.id.notf_Recyc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Notification_RecyclerViewAdapter(this, this, notificationList);
        adapter.setClickListener(this);

        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    private void backToMain() {
        Intent login = new Intent(NotificationActivity.this, MainActivity.class);
        startActivity(login);
        finish();
    }

}
