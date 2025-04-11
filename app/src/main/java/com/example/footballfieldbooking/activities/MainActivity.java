package com.example.footballfieldbooking.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footballfieldbooking.R;

public class MainActivity extends AppCompatActivity {
    private Button btnLogin, btnRegister;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Ẩn cả ActionBar nếu có
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        // Initialize views
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Check if already logged in
        sharedPreferences = getSharedPreferences("football_booking_prefs", MODE_PRIVATE);
        if (sharedPreferences.getInt("user_id", -1) != -1) {
            redirectBasedOnUserRole();
        }

        // Set click listeners
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check if already logged in when returning to this activity
        if (sharedPreferences.getInt("user_id", -1) != -1) {
            redirectBasedOnUserRole();
        }
    }

    private void redirectBasedOnUserRole() {
        boolean isAdmin = sharedPreferences.getBoolean("is_admin", false);

        if (isAdmin) {
            startActivity(new Intent(MainActivity.this, AdminDashboardActivity.class));
        } else {
            startActivity(new Intent(MainActivity.this, CustomerDashboardActivity.class));
        }
        finish();
    }
}