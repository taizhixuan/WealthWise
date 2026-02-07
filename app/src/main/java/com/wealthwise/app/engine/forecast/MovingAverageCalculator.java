package com.wealthwise.app.engine.forecast;

import java.util.List;

public class MovingAverageCalculator {

    private static final int DEFAULT_WINDOW = 3;

    public double calculate(List<Double> monthlyValues) {
        return calculate(monthlyValues, DEFAULT_WINDOW);
    }

    public double calculate(List<Double> monthlyValues, int window) {
        if (monthlyValues == null || monthlyValues.isEmpty()) {
            return 0;
        }

        int size = monthlyValues.size();
        int effectiveWindow = Math.min(window, size);
        double sum = 0;

        for (int i = size - effectiveWindow; i < size; i++) {
            sum += monthlyValues.get(i);
        }

        return sum / effectiveWindow;
    }
}
