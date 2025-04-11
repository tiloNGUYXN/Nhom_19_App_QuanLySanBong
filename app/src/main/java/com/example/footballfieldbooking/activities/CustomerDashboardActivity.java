package com.example.footballfieldbooking.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footballfieldbooking.R;
import com.example.footballfieldbooking.database.DatabaseHelper;

public class CustomerDashboardActivity extends AppCompatActivity {
    private TextView tvWelcome, tvNotificationBadge;
    private Button btnViewFields, btnBookingHistory, btnNotifications, btnLogout;
    private SharedPreferences sharedPreferences;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Ẩn cả ActionBar nếu có
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Get shared preferences
        sharedPreferences = getSharedPreferences("football_booking_prefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        userId = sharedPreferences.getInt("user_id", -1);

        // Initialize views
        tvWelcome = findViewById(R.id.tvWelcome);
        tvNotificationBadge = findViewById(R.id.tvNotificationBadge);
        btnViewFields = findViewById(R.id.btnViewFields);
        btnBookingHistory = findViewById(R.id.btnBookingHistory);
        btnNotifications = findViewById(R.id.btnNotifications);
        btnLogout = findViewById(R.id.btnLogout);

        // Set welcome text using string format
        tvWelcome.setText(String.format(getString(R.string.welcome), username));
        updateNotificationBadge();

        // Set click listeners
        btnViewFields.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerDashboardActivity.this, FieldListActivity.class));
            }
        });

        btnBookingHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerDashboardActivity.this, BookingHistoryActivity.class));
            }
        });

        btnNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerDashboardActivity.this, NotificationsActivity.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNotificationBadge();
    }

    private void updateNotificationBadge() {
        int unreadCount = dbHelper.getUnreadNotificationCount(userId);
        if (unreadCount > 0) {
            tvNotificationBadge.setVisibility(View.VISIBLE);
            tvNotificationBadge.setText(String.valueOf(unreadCount));
        } else {
            tvNotificationBadge.setVisibility(View.GONE);
        }
    }

    private void logout() {
        // Clear shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Redirect to main activity
        startActivity(new Intent(CustomerDashboardActivity.this, MainActivity.class));
        finish();
    }
}