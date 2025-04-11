package com.example.footballfieldbooking.models;

import java.util.Date;
import java.util.List;

public class Booking {
    public static final int STATUS_NOT_PLAYED = 0;
    public static final int STATUS_PLAYING = 1;
    public static final int STATUS_BREAK = 2;
    public static final int STATUS_PLAYED = 3;

    private int id;
    private int fieldId;
    private int userId;
    private Date bookingDate;
    private Date startTime;
    private Date endTime;
    private int status; // 0-not played, 1-playing, 2-break, 3-played
    private double totalPrice;
    private List<Service> services;

    // Field data for display
    private String fieldName;
    private int fieldType;

    public Booking() {}

    public Booking(int id, int fieldId, int userId, Date bookingDate, Date startTime, Date endTime,
                   int status, double totalPrice) {
        this.id = id;
        this.fieldId = fieldId;
        this.userId = userId;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public int getFieldType() {
        return fieldType;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    public String getStatusString() {
        switch (status) {
            case STATUS_NOT_PLAYED: return "Chưa đá";
            case STATUS_PLAYING: return "Đang đá";
            case STATUS_BREAK: return "Đang nghỉ";
            case STATUS_PLAYED: return "Đã đá";
            default: return "Không xác định";
        }
    }
}