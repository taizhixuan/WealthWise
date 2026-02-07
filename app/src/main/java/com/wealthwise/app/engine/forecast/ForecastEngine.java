package com.wealthwise.app.engine.forecast;

import com.wealthwise.app.data.local.dao.TransactionDao;
import com.wealthwise.app.data.model.TransactionType;
import com.wealthwise.app.data.repository.TransactionRepository;
import com.wealthwise.app.util.Constants;
import com.wealthwise.app.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ForecastEngine {

    private final TransactionRepository transactionRepository;
    private final MovingAverageCalculator maCalculator;
    private final LinearRegressionCalculator lrCalculator;
    private final WeightedAverageCalculator waCalculator;
    private final SeasonalityDetector seasonalityDetector;

    public ForecastEngine(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
        this.maCalculator = new MovingAverageCalculator();
        this.lrCalculator = new LinearRegressionCalculator();
        this.waCalculator = new WeightedAverageCalculator();
        this.seasonalityDetector = new SeasonalityDetector();
    }

    public ForecastResult forecast(int days) {
        // Get monthly snapshots for the last 12 months
        Date startDate = DateUtils.getMonthsAgo(12);
        List<TransactionDao.MonthlySnapshotResult> snapshots =
                transactionRepository.getMonthlySnapshots(startDate);

        if (snapshots == null || snapshots.isEmpty()) {
            return new ForecastResult(0, 0, 0);
        }

        // Build monthly expense totals
        List<Double> monthlyExpenses = new ArrayList<>();
        List<Double> monthlyIncomes = new ArrayList<>();
        for (TransactionDao.MonthlySnapshotResult snapshot : snapshots) {
            monthlyExpenses.add(snapshot.totalExpense);
            monthlyIncomes.add(snapshot.totalIncome);
        }

        // Ensemble forecast for expenses
        double maExpense = maCalculator.calculate(monthlyExpenses);
        double lrExpense = lrCalculator.calculate(monthlyExpenses);
        double waExpense = waCalculator.calculate(monthlyExpenses);
        double forecastedExpense = Constants.FORECAST_MA_WEIGHT * maExpense
                + Constants.FORECAST_LR_WEIGHT * lrExpense
                + Constants.FORECAST_WA_WEIGHT * waExpense;

        // Ensemble forecast for income
        double maIncome = maCalculator.calculate(monthlyIncomes);
        double lrIncome = new LinearRegressionCalculator().calculate(monthlyIncomes);
        double waIncome = waCalculator.calculate(monthlyIncomes);
        double forecastedIncome = Constants.FORECAST_MA_WEIGHT * maIncome
                + Constants.FORECAST_LR_WEIGHT * lrIncome
                + Constants.FORECAST_WA_WEIGHT * waIncome;

        // Scale to days
        double dailyFactor = days / 30.0;
        double projectedExpense = forecastedExpense * dailyFactor;
        double projectedIncome = forecastedIncome * dailyFactor;
        double projectedBalance = projectedIncome - projectedExpense;

        // Confidence interval: stddev of monthly net savings
        double stddev = calculateStddev(monthlyExpenses);
        double confidenceMargin = Constants.CONFIDENCE_Z_SCORE * stddev * dailyFactor;

        ForecastResult result = new ForecastResult(
                projectedBalance,
                projectedBalance - confidenceMargin,
                projectedBalance + confidenceMargin
        );

        // Current average
        double avgExpense = average(monthlyExpenses);
        result.setCurrentAverage(avgExpense);
        if (avgExpense > 0) {
            result.setChangePercentage(((forecastedExpense - avgExpense) / avgExpense) * 100);
        }

        // Daily projections
        List<Double> dailyProjections = new ArrayList<>();
        double dailyRate = projectedBalance / days;
        double runningBalance = 0;
        for (int i = 0; i < days; i++) {
            runningBalance += dailyRate;
            dailyProjections.add(runningBalance);
        }
        result.setDailyProjections(dailyProjections);

        return result;
    }

    public List<ForecastResult> forecastByCategory(int days) {
        Date start = DateUtils.getStartOfCurrentMonth();
        Date end = DateUtils.getEndOfCurrentMonth();
        List<TransactionDao.CategorySummaryResult> summaries =
                transactionRepository.getMonthlyCategorySummary(start, end, TransactionType.EXPENSE);

        List<ForecastResult> results = new ArrayList<>();
        if (summaries == null) return results;

        for (TransactionDao.CategorySummaryResult summary : summaries) {
            ForecastResult result = new ForecastResult();
            result.setCategoryId(summary.categoryId);
            result.setCategoryName(summary.categoryName);
            result.setProjectedAmount(summary.totalAmount * (days / 30.0));
            result.setCurrentAverage(summary.totalAmount);
            result.setChangePercentage(0);
            results.add(result);
        }

        return results;
    }

    private double calculateStddev(List<Double> values) {
        if (values.size() < 2) return 0;
        double mean = average(values);
        double sumSquares = 0;
        for (double val : values) {
            sumSquares += Math.pow(val - mean, 2);
        }
        return Math.sqrt(sumSquares / (values.size() - 1));
    }

    private double average(List<Double> values) {
        if (values.isEmpty()) return 0;
        double sum = 0;
        for (double val : values) {
            sum += val;
        }
        return sum / values.size();
    }
}
