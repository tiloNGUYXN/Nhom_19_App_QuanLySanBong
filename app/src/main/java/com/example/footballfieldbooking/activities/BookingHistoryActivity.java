package com.example.footballfieldbooking.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footballfieldbooking.R;
import com.example.footballfieldbooking.adapters.BookingAdapter;
import com.example.footballfieldbooking.database.DatabaseHelper;
import com.example.footballfieldbooking.models.Booking;

import java.util.List;

public class BookingHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerViewBookings;
    private BookingAdapter bookingAdapter;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);
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
        recyclerViewBookings = findViewById(R.id.recyclerViewBookings);
        recyclerViewBookings.setLayoutManager(new LinearLayoutManager(this));

        loadBookingHistory();
    }

    private void loadBookingHistory() {
        List<Booking> bookings = dbHelper.getBookingsByUser(userId);
        bookingAdapter = new BookingAdapter(this, bookings);
        recyclerViewBookings.setAdapter(bookingAdapter);
    }
}