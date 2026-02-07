package com.wealthwise.app.engine.recommendation;

import com.wealthwise.app.data.local.dao.TransactionDao;
import com.wealthwise.app.data.repository.TransactionRepository;
import com.wealthwise.app.util.CurrencyFormatter;
import com.wealthwise.app.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrendDetector {

    private static final int CONSECUTIVE_MONTHS_THRESHOLD = 3;

    private final TransactionRepository transactionRepository;

    public TrendDetector(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // Rule 5: Upward spending trends (3+ consecutive months increasing)
    public List<Recommendation> checkUpwardTrends() {
        List<Recommendation> recommendations = new ArrayList<>();

        Date sixMonthsAgo = DateUtils.getMonthsAgo(6);
        List<TransactionDao.MonthlySnapshotResult> snapshots =
                transactionRepository.getMonthlySnapshots(sixMonthsAgo);

        if (snapshots == null || snapshots.size() < CONSECUTIVE_MONTHS_THRESHOLD + 1) {
            return recommendations;
        }

        // Check for consecutive increasing expense months
        int consecutiveIncreases = 0;
        for (int i = 1; i < snapshots.size(); i++) {
            if (snapshots.get(i).totalExpense > snapshots.get(i - 1).totalExpense) {
                consecutiveIncreases++;
            } else {
                consecutiveIncreases = 0;
            }

            if (consecutiveIncreases >= CONSECUTIVE_MONTHS_THRESHOLD) {
                double firstExpense = snapshots.get(i - consecutiveIncreases).totalExpense;
                double lastExpense = snapshots.get(i).totalExpense;
                double increasePercent = firstExpense > 0 ?
                        ((lastExpense - firstExpense) / firstExpense) * 100 : 0;

                Recommendation rec = new Recommendation();
                rec.setId("upward_trend_" + DateUtils.getCurrentMonth() + "_" + DateUtils.getCurrentYear());
                rec.setType(RecommendationType.UPWARD_TREND);
                rec.setPriority(RecommendationPriority.MEDIUM);
                rec.setTitle("Spending has increased for " + (consecutiveIncreases + 1) + " months");
                rec.setDescription(String.format("Your total expenses have risen %.0f%% over the last %d months, " +
                                "from %s to %s. Review your spending categories to identify areas to cut back.",
                        increasePercent, consecutiveIncreases + 1,
                        CurrencyFormatter.format(firstExpense),
                        CurrencyFormatter.format(lastExpense)));
                rec.setActionText("View Analytics");
                recommendations.add(rec);
                break;
            }
        }

        return recommendations;
    }
}
