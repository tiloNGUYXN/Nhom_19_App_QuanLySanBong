package com.example.footballfieldbooking.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footballfieldbooking.R;
import com.example.footballfieldbooking.adapters.AdminBookingAdapter;
import com.example.footballfieldbooking.database.DatabaseHelper;
import com.example.footballfieldbooking.models.Booking;

import java.util.List;

public class AdminBookingsActivity extends AppCompatActivity {
    private RecyclerView recyclerViewBookings;
    private AdminBookingAdapter bookingAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_bookings);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Ẩn cả ActionBar nếu có
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        dbHelper = new DatabaseHelper(this);

        // Initialize RecyclerView
        recyclerViewBookings = findViewById(R.id.recyclerViewBookings);
        recyclerViewBookings.setLayoutManager(new LinearLayoutManager(this));

        loadBookings();
    }

    private void loadBookings() {
        List<Booking> bookings = dbHelper.getAllBookings();
        bookingAdapter = new AdminBookingAdapter(this, bookings, dbHelper);
        recyclerViewBookings.setAdapter(bookingAdapter);
    }
}