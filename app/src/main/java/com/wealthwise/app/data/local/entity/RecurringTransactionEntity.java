package com.wealthwise.app.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.wealthwise.app.data.model.RecurrenceInterval;
import com.wealthwise.app.data.model.TransactionType;
import com.wealthwise.app.data.remote.SyncStatus;

import java.util.Date;

@Entity(tableName = "recurring_transactions",
        foreignKeys = {
                @ForeignKey(entity = CategoryEntity.class,
                        parentColumns = "id",
                        childColumns = "category_id",
                        onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = AccountEntity.class,
                        parentColumns = "id",
                        childColumns = "account_id",
                        onDelete = ForeignKey.SET_NULL)
        },
        indices = {
                @Index(value = "category_id"),
                @Index(value = "account_id"),
                @Index(value = "next_occurrence")
        })
public class RecurringTransactionEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "firebase_id")
    private String firebaseId;

    @ColumnInfo(name = "type")
    private TransactionType type;

    @ColumnInfo(name = "amount")
    private double amount;

    @ColumnInfo(name = "category_id")
    private Long categoryId;

    @ColumnInfo(name = "account_id")
    private Long accountId;

    @ColumnInfo(name = "interval_type")
    private RecurrenceInterval interval;

    @ColumnInfo(name = "start_date")
    private Date startDate;

    @ColumnInfo(name = "end_date")
    private Date endDate;

    @ColumnInfo(name = "next_occurrence")
    private Date nextOccurrence;

    @ColumnInfo(name = "note")
    private String note;

    @ColumnInfo(name = "payee")
    private String payee;

    @ColumnInfo(name = "is_active")
    private boolean isActive;

    @ColumnInfo(name = "sync_status")
    private SyncStatus syncStatus;

    @ColumnInfo(name = "is_deleted")
    private boolean isDeleted;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    public RecurringTransactionEntity() {
        this.syncStatus = SyncStatus.PENDING;
        this.isDeleted = false;
        this.isActive = true;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getFirebaseId() { return firebaseId; }
    public void setFirebaseId(String firebaseId) { this.firebaseId = firebaseId; }
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public RecurrenceInterval getInterval() { return interval; }
    public void setInterval(RecurrenceInterval interval) { this.interval = interval; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public Date getNextOccurrence() { return nextOccurrence; }
    public void setNextOccurrence(Date nextOccurrence) { this.nextOccurrence = nextOccurrence; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getPayee() { return payee; }
    public void setPayee(String payee) { this.payee = payee; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public SyncStatus getSyncStatus() { return syncStatus; }
    public void setSyncStatus(SyncStatus syncStatus) { this.syncStatus = syncStatus; }
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
