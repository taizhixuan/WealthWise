package com.wealthwise.app.engine.forecast;

import java.util.List;

public class SeasonalityDetector {

    public boolean hasSeasonalPattern(List<Double> monthlyValues) {
        if (monthlyValues == null || monthlyValues.size() < 12) {
            return false;
        }

        // Simple coefficient of variation check
        double mean = 0;
        for (double val : monthlyValues) {
            mean += val;
        }
        mean /= monthlyValues.size();

        if (mean == 0) return false;

        double variance = 0;
        for (double val : monthlyValues) {
            variance += Math.pow(val - mean, 2);
        }
        variance /= monthlyValues.size();
        double cv = Math.sqrt(variance) / mean;

        // If CV > 0.3, there's likely seasonal variation
        return cv > 0.3;
    }

    public double getSeasonalFactor(List<Double> monthlyValues, int targetMonth) {
        if (monthlyValues == null || monthlyValues.size() < 12) {
            return 1.0;
        }

        double overallMean = 0;
        for (double val : monthlyValues) {
            overallMean += val;
        }
        overallMean /= monthlyValues.size();

        if (overallMean == 0) return 1.0;

        // Average of the same month across years
        double monthSum = 0;
        int monthCount = 0;
        for (int i = targetMonth; i < monthlyValues.size(); i += 12) {
            monthSum += monthlyValues.get(i);
            monthCount++;
        }

        if (monthCount == 0) return 1.0;
        double monthAvg = monthSum / monthCount;

        return monthAvg / overallMean;
    }
}
