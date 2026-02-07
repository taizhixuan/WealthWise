package com.wealthwise.app.data.model;

import java.util.Date;

public class Transaction {
    private long id;
    private TransactionType type;
    private double amount;
    private String categoryName;
    private String categoryIconName;
    private String categoryColorHex;
    private long categoryId;
    private long accountId;
    private String accountName;
    private Date date;
    private String note;
    private String payee;

    public Transaction() {}

    public Transaction(long id, TransactionType type, double amount, String categoryName,
                       String categoryIconName, String categoryColorHex, long categoryId,
                       long accountId, String accountName, Date date, String note, String payee) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.categoryName = categoryName;
        this.categoryIconName = categoryIconName;
        this.categoryColorHex = categoryColorHex;
        this.categoryId = categoryId;
        this.accountId = accountId;
        this.accountName = accountName;
        this.date = date;
        this.note = note;
        this.payee = payee;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getCategoryIconName() { return categoryIconName; }
    public void setCategoryIconName(String categoryIconName) { this.categoryIconName = categoryIconName; }
    public String getCategoryColorHex() { return categoryColorHex; }
    public void setCategoryColorHex(String categoryColorHex) { this.categoryColorHex = categoryColorHex; }
    public long getCategoryId() { return categoryId; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }
    public long getAccountId() { return accountId; }
    public void setAccountId(long accountId) { this.accountId = accountId; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getPayee() { return payee; }
    public void setPayee(String payee) { this.payee = payee; }
}
