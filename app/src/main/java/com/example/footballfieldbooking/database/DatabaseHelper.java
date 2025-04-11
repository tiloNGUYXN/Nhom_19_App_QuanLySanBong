package com.example.footballfieldbooking.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.footballfieldbooking.models.Booking;
import com.example.footballfieldbooking.models.Field;
import com.example.footballfieldbooking.models.Notification;
import com.example.footballfieldbooking.models.Service;
import com.example.footballfieldbooking.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "football_booking.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_FIELDS = "fields";
    private static final String TABLE_BOOKINGS = "bookings";
    private static final String TABLE_SERVICES = "services";
    private static final String TABLE_BOOKING_SERVICES = "booking_services";
    private static final String TABLE_NOTIFICATIONS = "notifications";

    // Common column names
    private static final String KEY_ID = "id";

    // USER table columns
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_IS_ADMIN = "is_admin";

    // FIELD table columns
    private static final String KEY_NAME = "name";
    private static final String KEY_TYPE = "type";
    private static final String KEY_STATUS = "status";
    private static final String KEY_PRICE = "price";
    private static final String KEY_DESCRIPTION = "description";

    // BOOKING table columns
    private static final String KEY_FIELD_ID = "field_id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_BOOKING_DATE = "booking_date";
    private static final String KEY_START_TIME = "start_time";
    private static final String KEY_END_TIME = "end_time";
    private static final String KEY_TOTAL_PRICE = "total_price";

    // SERVICE table columns
    // KEY_NAME, KEY_PRICE, KEY_DESCRIPTION already defined

    // BOOKING_SERVICES table columns
    private static final String KEY_BOOKING_ID = "booking_id";
    private static final String KEY_SERVICE_ID = "service_id";

    // NOTIFICATIONS table columns
    private static final String KEY_TITLE = "title";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_IS_READ = "is_read";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USERNAME + " TEXT UNIQUE,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_FULL_NAME + " TEXT,"
                + KEY_PHONE + " TEXT,"
                + KEY_IS_ADMIN + " INTEGER DEFAULT 0"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Create Fields table
        String CREATE_FIELDS_TABLE = "CREATE TABLE " + TABLE_FIELDS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_TYPE + " INTEGER,"
                + KEY_STATUS + " INTEGER DEFAULT 0,"
                + KEY_PRICE + " REAL,"
                + KEY_DESCRIPTION + " TEXT"
                + ")";
        db.execSQL(CREATE_FIELDS_TABLE);

        // Create Bookings table
        String CREATE_BOOKINGS_TABLE = "CREATE TABLE " + TABLE_BOOKINGS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_FIELD_ID + " INTEGER,"
                + KEY_USER_ID + " INTEGER,"
                + KEY_BOOKING_DATE + " TEXT,"
                + KEY_START_TIME + " TEXT,"
                + KEY_END_TIME + " TEXT,"
                + KEY_STATUS + " INTEGER DEFAULT 0,"
                + KEY_TOTAL_PRICE + " REAL,"
                + "FOREIGN KEY(" + KEY_FIELD_ID + ") REFERENCES " + TABLE_FIELDS + "(" + KEY_ID + "),"
                + "FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + ")"
                + ")";
        db.execSQL(CREATE_BOOKINGS_TABLE);

        // Create Services table
        String CREATE_SERVICES_TABLE = "CREATE TABLE " + TABLE_SERVICES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_PRICE + " REAL,"
                + KEY_DESCRIPTION + " TEXT"
                + ")";
        db.execSQL(CREATE_SERVICES_TABLE);

        // Create Booking_Services join table
        String CREATE_BOOKING_SERVICES_TABLE = "CREATE TABLE " + TABLE_BOOKING_SERVICES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_BOOKING_ID + " INTEGER,"
                + KEY_SERVICE_ID + " INTEGER,"
                + "FOREIGN KEY(" + KEY_BOOKING_ID + ") REFERENCES " + TABLE_BOOKINGS + "(" + KEY_ID + "),"
                + "FOREIGN KEY(" + KEY_SERVICE_ID + ") REFERENCES " + TABLE_SERVICES + "(" + KEY_ID + ")"
                + ")";
        db.execSQL(CREATE_BOOKING_SERVICES_TABLE);

        // Create Notifications table
        String CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE " + TABLE_NOTIFICATIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER_ID + " INTEGER,"
                + KEY_TITLE + " TEXT,"
                + KEY_MESSAGE + " TEXT,"
                + KEY_TIMESTAMP + " TEXT,"
                + KEY_IS_READ + " INTEGER DEFAULT 0,"
                + "FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + ")"
                + ")";
        db.execSQL(CREATE_NOTIFICATIONS_TABLE);

        // Insert admin account
        ContentValues adminValues = new ContentValues();
        adminValues.put(KEY_USERNAME, "Admin");
        adminValues.put(KEY_PASSWORD, "123456");
        adminValues.put(KEY_FULL_NAME, "System Administrator");
        adminValues.put(KEY_PHONE, "");
        adminValues.put(KEY_IS_ADMIN, 1);
        db.insert(TABLE_USERS, null, adminValues);

        // Insert sample fields
        insertSampleFields(db);

        // Insert sample services
        insertSampleServices(db);
    }

    private void insertSampleFields(SQLiteDatabase db) {
        // 5-a-side fields
        insertField(db, "Field 5-A", Field.TYPE_5_ASIDE, Field.STATUS_AVAILABLE, 200000, "Standard 5-a-side field");
        insertField(db, "Field 5-B", Field.TYPE_5_ASIDE, Field.STATUS_AVAILABLE, 220000, "Premium 5-a-side field with better turf");

        // 7-a-side fields
        insertField(db, "Field 7-A", Field.TYPE_7_ASIDE, Field.STATUS_AVAILABLE, 300000, "Standard 7-a-side field");
        insertField(db, "Field 7-B", Field.TYPE_7_ASIDE, Field.STATUS_MAINTENANCE, 320000, "Premium 7-a-side field under maintenance");

        // 11-a-side fields
        insertField(db, "Field 11-A", Field.TYPE_11_ASIDE, Field.STATUS_AVAILABLE, 500000, "Full-size field with natural grass");
        insertField(db, "Field 11-B", Field.TYPE_11_ASIDE, Field.STATUS_AVAILABLE, 450000, "Full-size field with artificial turf");
    }

    private void insertField(SQLiteDatabase db, String name, int type, int status, double price, String description) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_TYPE, type);
        values.put(KEY_STATUS, status);
        values.put(KEY_PRICE, price);
        values.put(KEY_DESCRIPTION, description);
        db.insert(TABLE_FIELDS, null, values);
    }

    private void insertSampleServices(SQLiteDatabase db) {
        insertService(db, "Referee", 150000, "Professional referee for your match");
        insertService(db, "Water (10 bottles)", 100000, "Pack of 10 water bottles");
        insertService(db, "Equipment Rental", 50000, "Balls, bibs, and cones");
        insertService(db, "Lighting", 80000, "Field lighting for evening games");
        insertService(db, "Recording", 200000, "Match recording with 2 cameras");
    }

    private void insertService(SQLiteDatabase db, String name, double price, String description) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_PRICE, price);
        values.put(KEY_DESCRIPTION, description);
        db.insert(TABLE_SERVICES, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKING_SERVICES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FIELDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        // Create tables again
        onCreate(db);
    }

    // User methods
    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_FULL_NAME, user.getFullName());
        values.put(KEY_PHONE, user.getPhoneNumber());
        values.put(KEY_IS_ADMIN, user.isAdmin() ? 1 : 0);

        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    @SuppressLint("Range")
    public User getUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        String selectQuery = "SELECT * FROM " + TABLE_USERS +
                " WHERE " + KEY_USERNAME + " = ? AND " + KEY_PASSWORD + " = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[] {username, password});

        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndex(KEY_USERNAME)));
            user.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
            user.setFullName(cursor.getString(cursor.getColumnIndex(KEY_FULL_NAME)));
            user.setPhoneNumber(cursor.getString(cursor.getColumnIndex(KEY_PHONE)));
            user.setAdmin(cursor.getInt(cursor.getColumnIndex(KEY_IS_ADMIN)) == 1);
        }

        cursor.close();
        db.close();
        return user;
    }

    public boolean usernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_USERS + " WHERE " + KEY_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[] {username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Field methods
    @SuppressLint("Range")
    public List<Field> getAllFields() {
        List<Field> fields = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_FIELDS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Field field = new Field();
                field.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                field.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                field.setType(cursor.getInt(cursor.getColumnIndex(KEY_TYPE)));
                field.setStatus(cursor.getInt(cursor.getColumnIndex(KEY_STATUS)));
                field.setPrice(cursor.getDouble(cursor.getColumnIndex(KEY_PRICE)));
                field.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));

                fields.add(field);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return fields;
    }

    @SuppressLint("Range")
    public Field getFieldById(int fieldId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Field field = null;

        String selectQuery = "SELECT * FROM " + TABLE_FIELDS + " WHERE " + KEY_ID + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[] {String.valueOf(fieldId)});

        if (cursor.moveToFirst()) {
            field = new Field();
            field.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            field.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            field.setType(cursor.getInt(cursor.getColumnIndex(KEY_TYPE)));
            field.setStatus(cursor.getInt(cursor.getColumnIndex(KEY_STATUS)));
            field.setPrice(cursor.getDouble(cursor.getColumnIndex(KEY_PRICE)));
            field.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
        }

        cursor.close();
        db.close();
        return field;
    }

    @SuppressLint("Range")
    public List<Field> getFieldsByType(int type) {
        List<Field> fields = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_FIELDS + " WHERE " + KEY_TYPE + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {String.valueOf(type)});

        if (cursor.moveToFirst()) {
            do {
                Field field = new Field();
                field.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                field.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                field.setType(cursor.getInt(cursor.getColumnIndex(KEY_TYPE)));
                field.setStatus(cursor.getInt(cursor.getColumnIndex(KEY_STATUS)));
                field.setPrice(cursor.getDouble(cursor.getColumnIndex(KEY_PRICE)));
                field.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));

                fields.add(field);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return fields;
    }

    public long addField(Field field) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, field.getName());
        values.put(KEY_TYPE, field.getType());
        values.put(KEY_STATUS, field.getStatus());
        values.put(KEY_PRICE, field.getPrice());
        values.put(KEY_DESCRIPTION, field.getDescription());

        long id = db.insert(TABLE_FIELDS, null, values);
        db.close();
        return id;
    }

    public int updateField(Field field) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, field.getName());
        values.put(KEY_TYPE, field.getType());
        values.put(KEY_STATUS, field.getStatus());
        values.put(KEY_PRICE, field.getPrice());
        values.put(KEY_DESCRIPTION, field.getDescription());

        int rows = db.update(TABLE_FIELDS, values, KEY_ID + " = ?",
                new String[] {String.valueOf(field.getId())});
        db.close();
        return rows;
    }

    public int updateFieldStatus(int fieldId, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, status);

        int rows = db.update(TABLE_FIELDS, values, KEY_ID + " = ?",
                new String[] {String.valueOf(fieldId)});
        db.close();
        return rows;
    }

    // Booking methods
    public long addBooking(Booking booking) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FIELD_ID, booking.getFieldId());
        values.put(KEY_USER_ID, booking.getUserId());
        values.put(KEY_BOOKING_DATE, dateFormat.format(booking.getBookingDate()));
        values.put(KEY_START_TIME, dateFormat.format(booking.getStartTime()));
        values.put(KEY_END_TIME, dateFormat.format(booking.getEndTime()));
        values.put(KEY_STATUS, booking.getStatus());
        values.put(KEY_TOTAL_PRICE, booking.getTotalPrice());

        long id = db.insert(TABLE_BOOKINGS, null, values);

        // Add booking services if any
        if (booking.getServices() != null && !booking.getServices().isEmpty()) {
            for (Service service : booking.getServices()) {
                ContentValues serviceValues = new ContentValues();
                serviceValues.put(KEY_BOOKING_ID, id);
                serviceValues.put(KEY_SERVICE_ID, service.getId());
                db.insert(TABLE_BOOKING_SERVICES, null, serviceValues);
            }
        }

        // Update field status to booked
        ContentValues fieldValues = new ContentValues();
        fieldValues.put(KEY_STATUS, Field.STATUS_BOOKED);
        db.update(TABLE_FIELDS, fieldValues, KEY_ID + " = ?",
                new String[] {String.valueOf(booking.getFieldId())});

        db.close();
        return id;
    }

    @SuppressLint("Range")
    public List<Booking> getBookingsByUser(int userId) {
        List<Booking> bookings = new ArrayList<>();

        String selectQuery = "SELECT b.*, f.name as field_name, f.type as field_type FROM " + TABLE_BOOKINGS + " b " +
                "JOIN " + TABLE_FIELDS + " f ON b." + KEY_FIELD_ID + " = f." + KEY_ID +
                " WHERE b." + KEY_USER_ID + " = ? ORDER BY b." + KEY_BOOKING_DATE + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Booking booking = new Booking();
                booking.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                booking.setFieldId(cursor.getInt(cursor.getColumnIndex(KEY_FIELD_ID)));
                booking.setUserId(cursor.getInt(cursor.getColumnIndex(KEY_USER_ID)));

                try {
                    booking.setBookingDate(dateFormat.parse(cursor.getString(cursor.getColumnIndex(KEY_BOOKING_DATE))));
                    booking.setStartTime(dateFormat.parse(cursor.getString(cursor.getColumnIndex(KEY_START_TIME))));
                    booking.setEndTime(dateFormat.parse(cursor.getString(cursor.getColumnIndex(KEY_END_TIME))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                booking.setStatus(cursor.getInt(cursor.getColumnIndex(KEY_STATUS)));
                booking.setTotalPrice(cursor.getDouble(cursor.getColumnIndex(KEY_TOTAL_PRICE)));

                // Get field related data
                booking.setFieldName(cursor.getString(cursor.getColumnIndex("field_name")));
                booking.setFieldType(cursor.getInt(cursor.getColumnIndex("field_type")));

                // Get services for this booking
                booking.setServices(getBookingServices(booking.getId()));

                bookings.add(booking);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return bookings;
    }

    @SuppressLint("Range")
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();

        String selectQuery = "SELECT b.*, f.name as field_name, f.type as field_type, u.username as username FROM " +
                TABLE_BOOKINGS + " b " +
                "JOIN " + TABLE_FIELDS + " f ON b." + KEY_FIELD_ID + " = f." + KEY_ID +
                " JOIN " + TABLE_USERS + " u ON b." + KEY_USER_ID + " = u." + KEY_ID +
                " ORDER BY b." + KEY_BOOKING_DATE + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Booking booking = new Booking();
                booking.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                booking.setFieldId(cursor.getInt(cursor.getColumnIndex(KEY_FIELD_ID)));
                booking.setUserId(cursor.getInt(cursor.getColumnIndex(KEY_USER_ID)));

                try {
                    booking.setBookingDate(dateFormat.parse(cursor.getString(cursor.getColumnIndex(KEY_BOOKING_DATE))));
                    booking.setStartTime(dateFormat.parse(cursor.getString(cursor.getColumnIndex(KEY_START_TIME))));
                    booking.setEndTime(dateFormat.parse(cursor.getString(cursor.getColumnIndex(KEY_END_TIME))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                booking.setStatus(cursor.getInt(cursor.getColumnIndex(KEY_STATUS)));
                booking.setTotalPrice(cursor.getDouble(cursor.getColumnIndex(KEY_TOTAL_PRICE)));

                // Get field related data
                booking.setFieldName(cursor.getString(cursor.getColumnIndex("field_name")));
                booking.setFieldType(cursor.getInt(cursor.getColumnIndex("field_type")));

                // Get services for this booking
                booking.setServices(getBookingServices(booking.getId()));

                bookings.add(booking);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return bookings;
    }

    public int updateBookingStatus(int bookingId, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, status);

        int rows = db.update(TABLE_BOOKINGS, values, KEY_ID + " = ?",
                new String[] {String.valueOf(bookingId)});
        db.close();
        return rows;
    }

    @SuppressLint("Range")
    private List<Service> getBookingServices(int bookingId) {
        List<Service> services = new ArrayList<>();

        String selectQuery = "SELECT s.* FROM " + TABLE_SERVICES + " s " +
                "JOIN " + TABLE_BOOKING_SERVICES + " bs ON s." + KEY_ID + " = bs." + KEY_SERVICE_ID +
                " WHERE bs." + KEY_BOOKING_ID + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {String.valueOf(bookingId)});

        if (cursor.moveToFirst()) {
            do {
                Service service = new Service();
                service.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                service.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                service.setPrice(cursor.getDouble(cursor.getColumnIndex(KEY_PRICE)));
                service.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));

                services.add(service);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return services;
    }

    // Service methods
    @SuppressLint("Range")
    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SERVICES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Service service = new Service();
                service.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                service.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                service.setPrice(cursor.getDouble(cursor.getColumnIndex(KEY_PRICE)));
                service.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));

                services.add(service);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return services;
    }

    // Notification methods
    public long addNotification(Notification notification) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, notification.getUserId());
        values.put(KEY_TITLE, notification.getTitle());
        values.put(KEY_MESSAGE, notification.getMessage());
        values.put(KEY_TIMESTAMP, dateFormat.format(notification.getTimestamp()));
        values.put(KEY_IS_READ, notification.isRead() ? 1 : 0);

        long id = db.insert(TABLE_NOTIFICATIONS, null, values);
        db.close();
        return id;
    }

    @SuppressLint("Range")
    public List<Notification> getNotificationsByUser(int userId) {
        List<Notification> notifications = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_NOTIFICATIONS +
                " WHERE " + KEY_USER_ID + " = ? ORDER BY " + KEY_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Notification notification = new Notification();
                notification.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                notification.setUserId(cursor.getInt(cursor.getColumnIndex(KEY_USER_ID)));
                notification.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                notification.setMessage(cursor.getString(cursor.getColumnIndex(KEY_MESSAGE)));

                try {
                    notification.setTimestamp(dateFormat.parse(cursor.getString(cursor.getColumnIndex(KEY_TIMESTAMP))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                notification.setRead(cursor.getInt(cursor.getColumnIndex(KEY_IS_READ)) == 1);

                notifications.add(notification);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return notifications;
    }

    public int updateNotificationReadStatus(int notificationId, boolean isRead) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IS_READ, isRead ? 1 : 0);

        int rows = db.update(TABLE_NOTIFICATIONS, values, KEY_ID + " = ?",
                new String[] {String.valueOf(notificationId)});
        db.close();
        return rows;
    }

    public int getUnreadNotificationCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_NOTIFICATIONS +
                " WHERE " + KEY_USER_ID + " = ? AND " + KEY_IS_READ + " = 0";
        Cursor cursor = db.rawQuery(countQuery, new String[] {String.valueOf(userId)});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }
}