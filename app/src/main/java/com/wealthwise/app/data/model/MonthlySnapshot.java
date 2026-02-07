package com.wealthwise.app.data.model;

public class MonthlySnapshot {
    private int month;
    private int year;
    private double totalIncome;
    private double totalExpense;

    public MonthlySnapshot() {}

    public MonthlySnapshot(int month, int year, double totalIncome, double totalExpense) {
        this.month = month;
        this.year = year;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
    }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public double getTotalIncome() { return totalIncome; }
    public void setTotalIncome(double totalIncome) { this.totalIncome = totalIncome; }
    public double getTotalExpense() { return totalExpense; }
    public void setTotalExpense(double totalExpense) { this.totalExpense = totalExpense; }

    public double getNetSavings() {
        return totalIncome - totalExpense;
    }

    public double getSavingsRate() {
        if (totalIncome <= 0) return 0;
        return ((totalIncome - totalExpense) / totalIncome) * 100.0;
    }
}
