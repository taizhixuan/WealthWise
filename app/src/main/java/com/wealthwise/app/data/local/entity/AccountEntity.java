package com.wealthwise.app.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.wealthwise.app.data.remote.SyncStatus;

import java.util.Date;

@Entity(tableName = "accounts")
public class AccountEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "firebase_id")
    private String firebaseId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "account_type")
    private String accountType;

    @ColumnInfo(name = "balance")
    private double balance;

    @ColumnInfo(name = "initial_balance")
    private double initialBalance;

    @ColumnInfo(name = "currency_code")
    private String currencyCode;

    @ColumnInfo(name = "sync_status")
    private SyncStatus syncStatus;

    @ColumnInfo(name = "is_deleted")
    private boolean isDeleted;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    public AccountEntity() {
        this.syncStatus = SyncStatus.PENDING;
        this.isDeleted = false;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.currencyCode = "USD";
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getFirebaseId() { return firebaseId; }
    public void setFirebaseId(String firebaseId) { this.firebaseId = firebaseId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public double getInitialBalance() { return initialBalance; }
    public void setInitialBalance(double initialBalance) { this.initialBalance = initialBalance; }
    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    public SyncStatus getSyncStatus() { return syncStatus; }
    public void setSyncStatus(SyncStatus syncStatus) { this.syncStatus = syncStatus; }
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
