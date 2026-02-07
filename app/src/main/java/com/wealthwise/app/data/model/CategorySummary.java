package com.wealthwise.app.data.model;

public class CategorySummary {
    private long categoryId;
    private String categoryName;
    private String categoryColorHex;
    private String categoryIconName;
    private double totalAmount;
    private int transactionCount;
    private double percentage;

    public CategorySummary() {}

    public CategorySummary(long categoryId, String categoryName, String categoryColorHex,
                           String categoryIconName, double totalAmount, int transactionCount) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryColorHex = categoryColorHex;
        this.categoryIconName = categoryIconName;
        this.totalAmount = totalAmount;
        this.transactionCount = transactionCount;
    }

    public long getCategoryId() { return categoryId; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getCategoryColorHex() { return categoryColorHex; }
    public void setCategoryColorHex(String categoryColorHex) { this.categoryColorHex = categoryColorHex; }
    public String getCategoryIconName() { return categoryIconName; }
    public void setCategoryIconName(String categoryIconName) { this.categoryIconName = categoryIconName; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public int getTransactionCount() { return transactionCount; }
    public void setTransactionCount(int transactionCount) { this.transactionCount = transactionCount; }
    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }
}
