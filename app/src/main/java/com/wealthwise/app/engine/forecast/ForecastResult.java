package com.wealthwise.app.engine.forecast;

import java.util.List;

public class ForecastResult {

    private String categoryName;
    private long categoryId;
    private double projectedAmount;
    private double lowerBound;
    private double upperBound;
    private double currentAverage;
    private double changePercentage;
    private List<Double> dailyProjections;

    public ForecastResult() {}

    public ForecastResult(double projectedAmount, double lowerBound, double upperBound) {
        this.projectedAmount = projectedAmount;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public long getCategoryId() { return categoryId; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }
    public double getProjectedAmount() { return projectedAmount; }
    public void setProjectedAmount(double projectedAmount) { this.projectedAmount = projectedAmount; }
    public double getLowerBound() { return lowerBound; }
    public void setLowerBound(double lowerBound) { this.lowerBound = lowerBound; }
    public double getUpperBound() { return upperBound; }
    public void setUpperBound(double upperBound) { this.upperBound = upperBound; }
    public double getCurrentAverage() { return currentAverage; }
    public void setCurrentAverage(double currentAverage) { this.currentAverage = currentAverage; }
    public double getChangePercentage() { return changePercentage; }
    public void setChangePercentage(double changePercentage) { this.changePercentage = changePercentage; }
    public List<Double> getDailyProjections() { return dailyProjections; }
    public void setDailyProjections(List<Double> dailyProjections) { this.dailyProjections = dailyProjections; }

    public double getConfidenceRange() {
        return upperBound - lowerBound;
    }
}
