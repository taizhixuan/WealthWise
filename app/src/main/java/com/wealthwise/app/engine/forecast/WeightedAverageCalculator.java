package com.wealthwise.app.engine.forecast;

import java.util.List;

public class WeightedAverageCalculator {

    private static final double DECAY_FACTOR = 0.7;

    public double calculate(List<Double> monthlyValues) {
        if (monthlyValues == null || monthlyValues.isEmpty()) {
            return 0;
        }

        int n = monthlyValues.size();
        double weightedSum = 0;
        double weightSum = 0;

        for (int i = 0; i < n; i++) {
            // More recent months get higher weight via exponential decay
            double weight = Math.pow(DECAY_FACTOR, n - 1 - i);
            weightedSum += monthlyValues.get(i) * weight;
            weightSum += weight;
        }

        return weightSum > 0 ? weightedSum / weightSum : 0;
    }
}
