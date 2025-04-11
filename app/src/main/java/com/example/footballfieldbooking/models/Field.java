package com.example.footballfieldbooking.models;

import com.example.footballfieldbooking.FootballBookingApp;
import com.example.footballfieldbooking.R;

public class Field {
    public static final int TYPE_5_ASIDE = 5;
    public static final int TYPE_7_ASIDE = 7;
    public static final int TYPE_11_ASIDE = 11;

    public static final int STATUS_AVAILABLE = 0;
    public static final int STATUS_BOOKED = 1;
    public static final int STATUS_MAINTENANCE = 2;

    private int id;
    private String name;
    private int type; // 5, 7, or 11 aside
    private int status; // 0-available, 1-booked, 2-maintenance
    private double price;
    private String description;

    public Field() {}

    public Field(int id, String name, int type, int status, double price, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
        this.price = price;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTypeString() {
        switch (type) {
            case TYPE_5_ASIDE: return "Sân 5";
            case TYPE_7_ASIDE: return "Sân 7";
            case TYPE_11_ASIDE: return "Sân 11";
            default: return "Không xác định";
        }
    }

    public String getStatusString() {
        switch (status) {
            case STATUS_AVAILABLE: return "Trống";
            case STATUS_BOOKED: return "Đã đặt";
            case STATUS_MAINTENANCE: return "Đang bảo trì";
            default: return "Không xác định";
        }
    }
}