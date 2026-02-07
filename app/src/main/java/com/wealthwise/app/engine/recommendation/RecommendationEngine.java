package com.wealthwise.app.engine.recommendation;

import com.wealthwise.app.data.local.dao.TransactionDao;
import com.wealthwise.app.data.local.entity.BudgetEntity;
import com.wealthwise.app.data.local.relation.BudgetWithCategory;
import com.wealthwise.app.data.model.TransactionType;
import com.wealthwise.app.data.repository.BudgetRepository;
import com.wealthwise.app.data.repository.TransactionRepository;
import com.wealthwise.app.util.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RecommendationEngine {

    private final RuleBasedAdvisor ruleBasedAdvisor;
    private final SpendingAnalyzer spendingAnalyzer;
    private final TrendDetector trendDetector;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    public RecommendationEngine(TransactionRepository transactionRepository,
                                 BudgetRepository budgetRepository) {
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.ruleBasedAdvisor = new RuleBasedAdvisor(transactionRepository, budgetRepository);
        this.spendingAnalyzer = new SpendingAnalyzer(transactionRepository);
        this.trendDetector = new TrendDetector(transactionRepository);
    }

    public List<Recommendation> generate() {
        List<Recommendation> all = new ArrayList<>();

        // Get current month data for analyzers
        Date start = DateUtils.getStartOfCurrentMonth();
        Date end = DateUtils.getEndOfCurrentMonth();
        List<TransactionDao.CategorySummaryResult> currentSummaries =
                transactionRepository.getMonthlyCategorySummary(start, end, TransactionType.EXPENSE);

        // Get budgeted category IDs
        List<Long> budgetedCategoryIds = new ArrayList<>();
        int month = DateUtils.getCurrentMonth();
        int year = DateUtils.getCurrentYear();
        // Simple approach: check each category summary against budget
        if (currentSummaries != null) {
            for (TransactionDao.CategorySummaryResult summary : currentSummaries) {
                BudgetEntity budget = budgetRepository.getByCategoryAndMonth(
                        summary.categoryId, month, year);
                if (budget != null) {
                    budgetedCategoryIds.add(summary.categoryId);
                }
            }
        }

        // Rule 1: Budget overruns
        all.addAll(ruleBasedAdvisor.checkBudgetOverruns());

        // Rule 2: High-spend categories with no budget
        if (currentSummaries != null) {
            all.addAll(spendingAnalyzer.checkHighSpendNoBudget(currentSummaries, budgetedCategoryIds));
        }

        // Rule 3: Spending spikes
        if (currentSummaries != null) {
            all.addAll(spendingAnalyzer.checkSpendingSpikes(currentSummaries));
        }

        // Rule 4: Subscription creep
        all.addAll(ruleBasedAdvisor.checkSubscriptionCreep());

        // Rule 5: Upward trends
        all.addAll(trendDetector.checkUpwardTrends());

        // Rule 6: Low savings rate
        all.addAll(ruleBasedAdvisor.checkLowSavingsRate());

        // Rule 7: Latte factor
        all.addAll(spendingAnalyzer.checkLatteFactor());

        // Sort by priority (HIGH first)
        Collections.sort(all, (a, b) -> a.getPriority().ordinal() - b.getPriority().ordinal());

        return all;
    }
}
