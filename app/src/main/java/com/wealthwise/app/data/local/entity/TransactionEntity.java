package com.wealthwise.app.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.wealthwise.app.data.model.TransactionType;
import com.wealthwise.app.data.remote.SyncStatus;

import java.util.Date;

@Entity(tableName = "transactions",
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
                @Index(value = "date"),
                @Index(value = "type")
        })
public class TransactionEntity {

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

    @ColumnInfo(name = "date")
    private Date date;

    @ColumnInfo(name = "note")
    private String note;

    @ColumnInfo(name = "payee")
    private String payee;

    @ColumnInfo(name = "sync_status")
    private SyncStatus syncStatus;

    @ColumnInfo(name = "is_deleted")
    private boolean isDeleted;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    public TransactionEntity() {
        this.syncStatus = SyncStatus.PENDING;
        this.isDeleted = false;
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
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getPayee() { return payee; }
    public void setPayee(String payee) { this.payee = payee; }
    public SyncStatus getSyncStatus() { return syncStatus; }
    public void setSyncStatus(SyncStatus syncStatus) { this.syncStatus = syncStatus; }
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
