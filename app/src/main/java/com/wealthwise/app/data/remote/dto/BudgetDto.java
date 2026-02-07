package com.wealthwise.app.data.remote.dto;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.HashMap;
import java.util.Map;

public class BudgetDto {

    @DocumentId
    private String id;
    private long categoryId;
    private double limitAmount;
    private int month;
    private int year;
    private boolean rollover;
    private long localId;

    @ServerTimestamp
    private Timestamp createdAt;

    @ServerTimestamp
    private Timestamp updatedAt;

    public BudgetDto() {}

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("categoryId", categoryId);
        map.put("limitAmount", limitAmount);
        map.put("month", month);
        map.put("year", year);
        map.put("rollover", rollover);
        map.put("localId", localId);
        map.put("updatedAt", Timestamp.now());
        return map;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public long getCategoryId() { return categoryId; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }
    public double getLimitAmount() { return limitAmount; }
    public void setLimitAmount(double limitAmount) { this.limitAmount = limitAmount; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public boolean isRollover() { return rollover; }
    public void setRollover(boolean rollover) { this.rollover = rollover; }
    public long getLocalId() { return localId; }
    public void setLocalId(long localId) { this.localId = localId; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
