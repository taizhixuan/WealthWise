package com.wealthwise.app.worker;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.wealthwise.app.data.local.dao.TransactionDao;
import com.wealthwise.app.data.local.entity.BudgetEntity;
import com.wealthwise.app.data.model.TransactionType;
import com.wealthwise.app.data.repository.BudgetRepository;
import com.wealthwise.app.data.repository.TransactionRepository;
import com.wealthwise.app.util.DateUtils;
import com.wealthwise.app.util.NotificationHelper;

import java.util.Date;
import java.util.List;

public class BudgetAlertWorker extends Worker {

    public static final String WORK_NAME = "budget_alert_worker";

    public BudgetAlertWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Application app = (Application) getApplicationContext();
            TransactionRepository transactionRepo = new TransactionRepository(app);
            BudgetRepository budgetRepo = new BudgetRepository(app);

            int month = DateUtils.getCurrentMonth();
            int year = DateUtils.getCurrentYear();
            Date start = DateUtils.getStartOfMonth(month, year);
            Date end = DateUtils.getEndOfMonth(month, year);

            List<TransactionDao.CategorySummaryResult> summaries =
                    transactionRepo.getMonthlyCategorySummary(start, end, TransactionType.EXPENSE);

            if (summaries == null) return Result.success();

            Context ctx = getApplicationContext();
            for (TransactionDao.CategorySummaryResult summary : summaries) {
                BudgetEntity budget = budgetRepo.getByCategoryAndMonth(
                        summary.categoryId, month, year);

                if (budget == null) continue;

                double ratio = summary.totalAmount / budget.getLimitAmount();
                double percentUsed = ratio * 100;

                if (ratio >= 0.80) {
                    NotificationHelper.showBudgetAlert(ctx, summary.categoryName, percentUsed);
                }
            }

            return Result.success();
        } catch (Exception e) {
            return Result.retry();
        }
    }
}
