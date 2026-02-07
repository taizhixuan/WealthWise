package com.wealthwise.app.data.remote.dto;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.HashMap;
import java.util.Map;

public class TransactionDto {

    @DocumentId
    private String id;
    private String type;
    private double amount;
    private Long categoryId;
    private Long accountId;
    private Timestamp date;
    private String note;
    private String payee;
    private long localId;

    @ServerTimestamp
    private Timestamp createdAt;

    @ServerTimestamp
    private Timestamp updatedAt;

    public TransactionDto() {}

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("amount", amount);
        map.put("categoryId", categoryId);
        map.put("accountId", accountId);
        map.put("date", date);
        map.put("note", note);
        map.put("payee", payee);
        map.put("localId", localId);
        map.put("updatedAt", Timestamp.now());
        return map;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public Timestamp getDate() { return date; }
    public void setDate(Timestamp date) { this.date = date; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getPayee() { return payee; }
    public void setPayee(String payee) { this.payee = payee; }
    public long getLocalId() { return localId; }
    public void setLocalId(long localId) { this.localId = localId; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
