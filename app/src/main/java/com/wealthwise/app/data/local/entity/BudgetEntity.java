package com.wealthwise.app.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.wealthwise.app.data.remote.SyncStatus;

import java.util.Date;

@Entity(tableName = "budgets",
        foreignKeys = {
                @ForeignKey(entity = CategoryEntity.class,
                        parentColumns = "id",
                        childColumns = "category_id",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = "category_id"),
                @Index(value = {"category_id", "month", "year"}, unique = true)
        })
public class BudgetEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "firebase_id")
    private String firebaseId;

    @ColumnInfo(name = "category_id")
    private long categoryId;

    @ColumnInfo(name = "limit_amount")
    private double limitAmount;

    @ColumnInfo(name = "month")
    private int month;

    @ColumnInfo(name = "year")
    private int year;

    @ColumnInfo(name = "rollover")
    private boolean rollover;

    @ColumnInfo(name = "sync_status")
    private SyncStatus syncStatus;

    @ColumnInfo(name = "is_deleted")
    private boolean isDeleted;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    public BudgetEntity() {
        this.syncStatus = SyncStatus.PENDING;
        this.isDeleted = false;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getFirebaseId() { return firebaseId; }
    public void setFirebaseId(String firebaseId) { this.firebaseId = firebaseId; }
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
    public SyncStatus getSyncStatus() { return syncStatus; }
    public void setSyncStatus(SyncStatus syncStatus) { this.syncStatus = syncStatus; }
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
