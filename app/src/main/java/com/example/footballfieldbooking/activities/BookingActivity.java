package com.example.footballfieldbooking.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footballfieldbooking.R;
import com.example.footballfieldbooking.database.DatabaseHelper;
import com.example.footballfieldbooking.models.Booking;
import com.example.footballfieldbooking.models.Field;
import com.example.footballfieldbooking.models.Notification;
import com.example.footballfieldbooking.models.Service;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {
    private TextView tvFieldName, tvFieldType, tvFieldPrice;
    private TextView tvBookingDate, tvStartTime, tvEndTime, tvTotalPrice;
    private LinearLayout layoutServices;
    private Button btnBooking, btnBack;

    private DatabaseHelper dbHelper;
    private int fieldId;
    private double fieldPrice;
    private int userId;
    private Calendar bookingDate = Calendar.getInstance();
    private Calendar startTime = Calendar.getInstance();
    private Calendar endTime = Calendar.getInstance();
    private List<Service> allServices;
    private List<Service> selectedServices = new ArrayList<>();
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Ẩn cả ActionBar nếu có
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        // Get field info from intent
        fieldId = getIntent().getIntExtra("field_id", 0);
        String fieldName = getIntent().getStringExtra("field_name");
        int fieldType = getIntent().getIntExtra("field_type", 0);
        fieldPrice = getIntent().getDoubleExtra("field_price", 0);

        // Get user id from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("football_booking_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        dbHelper = new DatabaseHelper(this);
        allServices = dbHelper.getAllServices();

        // Set default times (current time + 1 hour for start, + 2 hours for end)
        startTime.add(Calendar.HOUR_OF_DAY, 1);
        startTime.set(Calendar.MINUTE, 0);

        endTime.add(Calendar.HOUR_OF_DAY, 2);
        endTime.set(Calendar.MINUTE, 0);

        // Initialize views
        initViews();

        // Set field info
        tvFieldName.setText(fieldName);
        tvFieldType.setText(getString(R.string.field_type) + ": " + getFieldTypeString(fieldType));
        tvFieldPrice.setText(getString(R.string.field_price) + " " + currencyFormat.format(fieldPrice) + "/giờ");

        // Initialize date and time displays
        updateDateTimeDisplay();

        // Setup service checkboxes
        setupServiceCheckboxes();

        // Calculate initial price
        calculateTotalPrice();

        // Setup click listeners
        setupClickListeners();
    }

    private void initViews() {
        tvFieldName = findViewById(R.id.tvFieldName);
        tvFieldType = findViewById(R.id.tvFieldType);
        tvFieldPrice = findViewById(R.id.tvFieldPrice);
        tvBookingDate = findViewById(R.id.tvBookingDate);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        layoutServices = findViewById(R.id.layoutServices);
        btnBooking = findViewById(R.id.btnBooking);
        btnBack = findViewById(R.id.btnBack);
    }

    private String getFieldTypeString(int type) {
        switch (type) {
            case Field.TYPE_5_ASIDE: return "Sân 5";
            case Field.TYPE_7_ASIDE: return "Sân 7";
            case Field.TYPE_11_ASIDE: return "Sân 11";
            default: return "Không xác định";
        }
    }

    private void setupClickListeners() {
        tvBookingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        tvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(true);
            }
        });

        tvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(false);
            }
        });

        btnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookField();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupServiceCheckboxes() {
        layoutServices.removeAllViews();

        for (final Service service : allServices) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(service.getName() + " - " + currencyFormat.format(service.getPrice()));
            checkBox.setTag(service);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    Service selectedService = (Service) cb.getTag();

                    if (cb.isChecked()) {
                        selectedServices.add(selectedService);
                    } else {
                        selectedServices.remove(selectedService);
                    }

                    calculateTotalPrice();
                }
            });

            layoutServices.addView(checkBox);
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        bookingDate.set(Calendar.YEAR, year);
                        bookingDate.set(Calendar.MONTH, month);
                        bookingDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateTimeDisplay();
                    }
                },
                bookingDate.get(Calendar.YEAR),
                bookingDate.get(Calendar.MONTH),
                bookingDate.get(Calendar.DAY_OF_MONTH)
        );

        // Set min date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePickerDialog(final boolean isStartTime) {
        Calendar calendar = isStartTime ? startTime : endTime;

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (isStartTime) {
                            startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            startTime.set(Calendar.MINUTE, minute);

                            // Auto-update end time if it's before start time
                            if (endTime.before(startTime)) {
                                Calendar newEndTime = (Calendar) startTime.clone();
                                newEndTime.add(Calendar.HOUR_OF_DAY, 1);
                                endTime = newEndTime;
                            }
                        } else {
                            endTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            endTime.set(Calendar.MINUTE, minute);

                            // Check if end time is after start time
                            if (endTime.before(startTime)) {
                                Toast.makeText(BookingActivity.this, "Giờ kết thúc phải sau giờ bắt đầu", Toast.LENGTH_SHORT).show();
                                Calendar newEndTime = (Calendar) startTime.clone();
                                newEndTime.add(Calendar.HOUR_OF_DAY, 1);
                                endTime = newEndTime;
                            }
                        }

                        updateDateTimeDisplay();
                        calculateTotalPrice();
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );

        timePickerDialog.show();
    }

    private void updateDateTimeDisplay() {
        tvBookingDate.setText(dateFormat.format(bookingDate.getTime()));
        tvStartTime.setText(timeFormat.format(startTime.getTime()));
        tvEndTime.setText(timeFormat.format(endTime.getTime()));
    }

    private void calculateTotalPrice() {
        // Calculate duration in hours
        long durationMillis = endTime.getTimeInMillis() - startTime.getTimeInMillis();
        double durationHours = durationMillis / (1000.0 * 60 * 60);

        // Calculate field price
        double totalFieldPrice = fieldPrice * durationHours;

        // Calculate services price
        double totalServicesPrice = 0;
        for (Service service : selectedServices) {
            totalServicesPrice += service.getPrice();
        }

        // Calculate total
        double total = totalFieldPrice + totalServicesPrice;

        tvTotalPrice.setText(currencyFormat.format(total));
    }

    private void bookField() {
        // Validate booking time
        Calendar now = Calendar.getInstance();

        if (bookingDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                bookingDate.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) &&
                startTime.before(now)) {
            Toast.makeText(this, "Không thể đặt sân với thời gian trong quá khứ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (startTime.equals(endTime) || endTime.before(startTime)) {
            Toast.makeText(this, "Thời gian không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate total price
        long durationMillis = endTime.getTimeInMillis() - startTime.getTimeInMillis();
        double durationHours = durationMillis / (1000.0 * 60 * 60);

        double totalFieldPrice = fieldPrice * durationHours;
        double totalServicesPrice = 0;
        for (Service service : selectedServices) {
            totalServicesPrice += service.getPrice();
        }
        double totalPrice = totalFieldPrice + totalServicesPrice;

        // Create booking
        Booking booking = new Booking();
        booking.setFieldId(fieldId);
        booking.setUserId(userId);

        // Set date by combining booking date with start/end times
        Calendar bookingStartDateTime = (Calendar) bookingDate.clone();
        bookingStartDateTime.set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY));
        bookingStartDateTime.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE));

        Calendar bookingEndDateTime = (Calendar) bookingDate.clone();
        bookingEndDateTime.set(Calendar.HOUR_OF_DAY, endTime.get(Calendar.HOUR_OF_DAY));
        bookingEndDateTime.set(Calendar.MINUTE, endTime.get(Calendar.MINUTE));

        booking.setBookingDate(bookingDate.getTime());
        booking.setStartTime(bookingStartDateTime.getTime());
        booking.setEndTime(bookingEndDateTime.getTime());
        booking.setStatus(Booking.STATUS_NOT_PLAYED);
        booking.setTotalPrice(totalPrice);
        booking.setServices(selectedServices);

        // Save booking to database
        long bookingId = dbHelper.addBooking(booking);

        if (bookingId > 0) {
            // Create notification
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setTitle("Xác nhận đặt sân");
            notification.setMessage("Đặt sân " + tvFieldName.getText() + " vào ngày " +
                    dateFormat.format(bookingDate.getTime()) + " từ " +
                    timeFormat.format(startTime.getTime()) + " đến " +
                    timeFormat.format(endTime.getTime()) + " đã được xác nhận.");
            notification.setTimestamp(new Date());
            notification.setRead(false);

            dbHelper.addNotification(notification);

            Toast.makeText(this, "Đặt sân thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Đặt sân thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}