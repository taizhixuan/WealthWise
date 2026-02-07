package com.wealthwise.app.data.model;

public class Budget {
    private long id;
    private long categoryId;
    private String categoryName;
    private String categoryColorHex;
    private String categoryIconName;
    private double limitAmount;
    private double spentAmount;
    private int month;
    private int year;
    private boolean rollover;

    public Budget() {}

    public Budget(long id, long categoryId, String categoryName, String categoryColorHex,
                  String categoryIconName, double limitAmount, double spentAmount,
                  int month, int year, boolean rollover) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryColorHex = categoryColorHex;
        this.categoryIconName = categoryIconName;
        this.limitAmount = limitAmount;
        this.spentAmount = spentAmount;
        this.month = month;
        this.year = year;
        this.rollover = rollover;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getCategoryId() { return categoryId; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getCategoryColorHex() { return categoryColorHex; }
    public void setCategoryColorHex(String categoryColorHex) { this.categoryColorHex = categoryColorHex; }
    public String getCategoryIconName() { return categoryIconName; }
    public void setCategoryIconName(String categoryIconName) { this.categoryIconName = categoryIconName; }
    public double getLimitAmount() { return limitAmount; }
    public void setLimitAmount(double limitAmount) { this.limitAmount = limitAmount; }
    public double getSpentAmount() { return spentAmount; }
    public void setSpentAmount(double spentAmount) { this.spentAmount = spentAmount; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public boolean isRollover() { return rollover; }
    public void setRollover(boolean rollover) { this.rollover = rollover; }

    public double getPercentUsed() {
        if (limitAmount <= 0) return 0;
        return (spentAmount / limitAmount) * 100.0;
    }

    public double getRemainingAmount() {
        return limitAmount - spentAmount;
    }
}
