package com.wealthwise.app.engine.recommendation;

import com.wealthwise.app.data.local.dao.TransactionDao;
import com.wealthwise.app.data.local.entity.BudgetEntity;
import com.wealthwise.app.data.local.relation.BudgetWithCategory;
import com.wealthwise.app.data.model.TransactionType;
import com.wealthwise.app.data.repository.BudgetRepository;
import com.wealthwise.app.data.repository.TransactionRepository;
import com.wealthwise.app.util.CurrencyFormatter;
import com.wealthwise.app.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RuleBasedAdvisor {

    private static final double BUDGET_WARNING_RATIO = 0.80;
    private static final double BUDGET_DANGER_RATIO = 1.0;
    private static final double LOW_SAVINGS_THRESHOLD = 0.20;

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    public RuleBasedAdvisor(TransactionRepository transactionRepository,
                            BudgetRepository budgetRepository) {
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
    }

    // Rule 1: Budget overruns (>80% warn, >100% alert)
    public List<Recommendation> checkBudgetOverruns() {
        List<Recommendation> recommendations = new ArrayList<>();
        int month = DateUtils.getCurrentMonth();
        int year = DateUtils.getCurrentYear();
        Date start = DateUtils.getStartOfMonth(month, year);
        Date end = DateUtils.getEndOfMonth(month, year);

        List<TransactionDao.CategorySummaryResult> summaries =
                transactionRepository.getMonthlyCategorySummary(start, end, TransactionType.EXPENSE);

        if (summaries == null) return recommendations;

        for (TransactionDao.CategorySummaryResult summary : summaries) {
            BudgetEntity budget = budgetRepository.getByCategoryAndMonth(
                    summary.categoryId, month, year);

            if (budget == null) continue;

            double ratio = summary.totalAmount / budget.getLimitAmount();

            if (ratio >= BUDGET_DANGER_RATIO) {
                double overAmount = summary.totalAmount - budget.getLimitAmount();
                Recommendation rec = new Recommendation();
                rec.setId("budget_over_" + summary.categoryId + "_" + month + "_" + year);
                rec.setType(RecommendationType.BUDGET_OVERRUN);
                rec.setPriority(RecommendationPriority.HIGH);
                rec.setTitle(summary.categoryName + " budget exceeded!");
                rec.setDescription(String.format("You've spent %s on %s, which is %s over your %s budget.",
                        CurrencyFormatter.format(summary.totalAmount),
                        summary.categoryName,
                        CurrencyFormatter.format(overAmount),
                        CurrencyFormatter.format(budget.getLimitAmount())));
                rec.setActionText("View Budget");
                rec.setCategoryId(summary.categoryId);
                rec.setAmount(overAmount);
                recommendations.add(rec);
            } else if (ratio >= BUDGET_WARNING_RATIO) {
                int percentUsed = (int) (ratio * 100);
                Recommendation rec = new Recommendation();
                rec.setId("budget_warn_" + summary.categoryId + "_" + month + "_" + year);
                rec.setType(RecommendationType.BUDGET_OVERRUN);
                rec.setPriority(RecommendationPriority.MEDIUM);
                rec.setTitle(summary.categoryName + " budget at " + percentUsed + "%");
                rec.setDescription(String.format("You've used %d%% of your %s budget for %s. " +
                                "Consider slowing down to stay within budget.",
                        percentUsed, CurrencyFormatter.format(budget.getLimitAmount()),
                        summary.categoryName));
                rec.setActionText("View Budget");
                rec.setCategoryId(summary.categoryId);
                recommendations.add(rec);
            }
        }
        return recommendations;
    }

    // Rule 6: Low savings rate (<20% income-expense ratio)
    public List<Recommendation> checkLowSavingsRate() {
        List<Recommendation> recommendations = new ArrayList<>();
        Date start = DateUtils.getStartOfCurrentMonth();
        Date end = DateUtils.getEndOfCurrentMonth();

        double income = transactionRepository.getMonthlyTotalByType(TransactionType.INCOME, start, end);
        double expense = transactionRepository.getMonthlyTotalByType(TransactionType.EXPENSE, start, end);

        if (income <= 0) return recommendations;

        double savingsRate = (income - expense) / income;

        if (savingsRate < LOW_SAVINGS_THRESHOLD) {
            int savingsPercent = (int) (savingsRate * 100);
            Recommendation rec = new Recommendation();
            rec.setId("low_savings_" + DateUtils.getCurrentMonth() + "_" + DateUtils.getCurrentYear());
            rec.setType(RecommendationType.LOW_SAVINGS_RATE);
            rec.setPriority(savingsRate < 0 ? RecommendationPriority.HIGH : RecommendationPriority.MEDIUM);
            rec.setTitle("Savings rate is only " + savingsPercent + "%");
            rec.setDescription(String.format("Your savings rate this month is %d%%. " +
                            "Aim for at least 20%% by reducing discretionary spending. " +
                            "Current income: %s, expenses: %s.",
                    savingsPercent, CurrencyFormatter.format(income), CurrencyFormatter.format(expense)));
            rec.setActionText("View Analytics");
            recommendations.add(rec);
        }
        return recommendations;
    }

    // Rule 4: Subscription creep (recurring costs growing)
    public List<Recommendation> checkSubscriptionCreep() {
        // Simplified: check if total recurring expense amount is significant
        List<Recommendation> recommendations = new ArrayList<>();
        // This would require comparing recurring totals over months
        // Placeholder for now - the RecurringTransactionRepository doesn't have
        // a sum method, so we skip the detailed implementation
        return recommendations;
    }
}
