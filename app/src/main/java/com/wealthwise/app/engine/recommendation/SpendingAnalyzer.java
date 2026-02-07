package com.wealthwise.app.engine.recommendation;

import com.wealthwise.app.data.local.dao.TransactionDao;
import com.wealthwise.app.data.model.TransactionType;
import com.wealthwise.app.data.repository.TransactionRepository;
import com.wealthwise.app.util.CurrencyFormatter;
import com.wealthwise.app.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpendingAnalyzer {

    private static final double HIGH_SPEND_THRESHOLD = 200.0;
    private static final double SPIKE_MULTIPLIER = 1.30;
    private static final int SMALL_TXN_COUNT_THRESHOLD = 15;
    private static final double SMALL_TXN_MAX_AMOUNT = 10.0;

    private final TransactionRepository transactionRepository;

    public SpendingAnalyzer(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // Rule 2: High-spend categories with no budget set (>$200/month avg)
    public List<Recommendation> checkHighSpendNoBudget(
            List<TransactionDao.CategorySummaryResult> currentSummaries,
            List<Long> budgetedCategoryIds) {

        List<Recommendation> recommendations = new ArrayList<>();
        for (TransactionDao.CategorySummaryResult summary : currentSummaries) {
            if (summary.totalAmount > HIGH_SPEND_THRESHOLD
                    && !budgetedCategoryIds.contains(summary.categoryId)) {
                Recommendation rec = new Recommendation();
                rec.setId("no_budget_" + summary.categoryId);
                rec.setType(RecommendationType.NO_BUDGET_HIGH_SPEND);
                rec.setPriority(RecommendationPriority.MEDIUM);
                rec.setTitle("Set a budget for " + summary.categoryName);
                rec.setDescription(String.format("You spent %s on %s this month with no budget set. " +
                                "Consider adding a budget to track this spending.",
                        CurrencyFormatter.format(summary.totalAmount), summary.categoryName));
                rec.setActionText("Add Budget");
                rec.setCategoryId(summary.categoryId);
                rec.setAmount(summary.totalAmount);
                recommendations.add(rec);
            }
        }
        return recommendations;
    }

    // Rule 3: Spending spikes (>130% of 3-month average)
    public List<Recommendation> checkSpendingSpikes(
            List<TransactionDao.CategorySummaryResult> currentSummaries) {

        List<Recommendation> recommendations = new ArrayList<>();
        Date threeMonthsAgo = DateUtils.getMonthsAgo(3);
        Date lastMonthEnd = DateUtils.getEndOfMonth(
                DateUtils.getCurrentMonth() == 1 ? 12 : DateUtils.getCurrentMonth() - 1,
                DateUtils.getCurrentMonth() == 1 ? DateUtils.getCurrentYear() - 1 : DateUtils.getCurrentYear());

        List<TransactionDao.CategorySummaryResult> historicalSummaries =
                transactionRepository.getMonthlyCategorySummary(threeMonthsAgo, lastMonthEnd, TransactionType.EXPENSE);

        for (TransactionDao.CategorySummaryResult current : currentSummaries) {
            double historicalAvg = 0;
            int count = 0;
            if (historicalSummaries != null) {
                for (TransactionDao.CategorySummaryResult hist : historicalSummaries) {
                    if (hist.categoryId == current.categoryId) {
                        historicalAvg += hist.totalAmount;
                        count++;
                    }
                }
            }
            if (count > 0) {
                historicalAvg /= count;
            }

            if (historicalAvg > 0 && current.totalAmount > historicalAvg * SPIKE_MULTIPLIER) {
                double increase = ((current.totalAmount - historicalAvg) / historicalAvg) * 100;
                Recommendation rec = new Recommendation();
                rec.setId("spike_" + current.categoryId);
                rec.setType(RecommendationType.SPENDING_SPIKE);
                rec.setPriority(RecommendationPriority.HIGH);
                rec.setTitle("Spending spike in " + current.categoryName);
                rec.setDescription(String.format("Your %s spending is %.0f%% above your 3-month average. " +
                                "Review recent transactions to identify the cause.",
                        current.categoryName, increase));
                rec.setActionText("View Transactions");
                rec.setCategoryId(current.categoryId);
                recommendations.add(rec);
            }
        }
        return recommendations;
    }

    // Rule 7: Latte factor (>15 small transactions <$10 in a category)
    public List<Recommendation> checkLatteFactor() {
        List<Recommendation> recommendations = new ArrayList<>();
        Date start = DateUtils.getStartOfCurrentMonth();
        Date end = DateUtils.getEndOfCurrentMonth();

        List<TransactionDao.CategorySummaryResult> summaries =
                transactionRepository.getMonthlyCategorySummary(start, end, TransactionType.EXPENSE);

        if (summaries == null) return recommendations;

        for (TransactionDao.CategorySummaryResult summary : summaries) {
            int smallCount = transactionRepository.getSmallTransactionCount(
                    summary.categoryId, SMALL_TXN_MAX_AMOUNT, start, end);

            if (smallCount > SMALL_TXN_COUNT_THRESHOLD) {
                double estimatedSaving = smallCount * 5.0 * 0.5; // rough savings estimate
                Recommendation rec = new Recommendation();
                rec.setId("latte_" + summary.categoryId);
                rec.setType(RecommendationType.LATTE_FACTOR);
                rec.setPriority(RecommendationPriority.LOW);
                rec.setTitle("Small purchases adding up in " + summary.categoryName);
                rec.setDescription(String.format("You made %d small transactions (under $10) in %s this month. " +
                                "These add up to %s. Consider reducing frequency to save more.",
                        smallCount, summary.categoryName, CurrencyFormatter.format(summary.totalAmount)));
                rec.setCategoryId(summary.categoryId);
                rec.setAmount(summary.totalAmount);
                recommendations.add(rec);
            }
        }
        return recommendations;
    }
}
