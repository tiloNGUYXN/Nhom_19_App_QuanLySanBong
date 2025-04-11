package com.example.footballfieldbooking.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footballfieldbooking.R;
import com.example.footballfieldbooking.adapters.NotificationAdapter;
import com.example.footballfieldbooking.database.DatabaseHelper;
import com.example.footballfieldbooking.models.Notification;

import java.util.List;

public class NotificationsActivity extends AppCompatActivity {
    private RecyclerView recyclerViewNotifications;
    private NotificationAdapter notificationAdapter;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Ẩn cả ActionBar nếu có
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        // Get user id from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("football_booking_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        dbHelper = new DatabaseHelper(this);

        // Initialize RecyclerView
        recyclerViewNotifications = findViewById(R.id.recyclerViewNotifications);
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));

        loadNotifications();
    }

    private void loadNotifications() {
        List<Notification> notifications = dbHelper.getNotificationsByUser(userId);
        notificationAdapter = new NotificationAdapter(this, notifications, dbHelper);
        recyclerViewNotifications.setAdapter(notificationAdapter);
    }
}