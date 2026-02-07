package com.wealthwise.app.engine.forecast;

import java.util.List;

public class LinearRegressionCalculator {

    private double slope;
    private double intercept;

    public double calculate(List<Double> monthlyValues) {
        if (monthlyValues == null || monthlyValues.size() < 2) {
            return monthlyValues != null && !monthlyValues.isEmpty() ? monthlyValues.get(monthlyValues.size() - 1) : 0;
        }

        int n = monthlyValues.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        for (int i = 0; i < n; i++) {
            double x = i + 1;
            double y = monthlyValues.get(i);
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double denominator = n * sumX2 - sumX * sumX;
        if (denominator == 0) {
            slope = 0;
            intercept = sumY / n;
        } else {
            slope = (n * sumXY - sumX * sumY) / denominator;
            intercept = (sumY - slope * sumX) / n;
        }

        // Predict next month (x = n + 1)
        return slope * (n + 1) + intercept;
    }

    public double getSlope() { return slope; }
    public double getIntercept() { return intercept; }

    public double predict(int monthIndex) {
        return slope * monthIndex + intercept;
    }
}
