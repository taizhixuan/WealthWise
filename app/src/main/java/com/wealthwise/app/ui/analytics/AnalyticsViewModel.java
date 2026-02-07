package com.wealthwise.app.ui.analytics;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wealthwise.app.data.local.AppDatabase;
import com.wealthwise.app.data.local.dao.TransactionDao;
import com.wealthwise.app.data.model.TransactionType;
import com.wealthwise.app.data.repository.TransactionRepository;
import com.wealthwise.app.util.DateUtils;

import java.util.Date;
import java.util.List;

public class AnalyticsViewModel extends AndroidViewModel {

    private final TransactionRepository transactionRepository;

    private final MutableLiveData<List<TransactionDao.CategorySummaryResult>> categorySummaries =
            new MutableLiveData<>();
    private final MutableLiveData<List<TransactionDao.MonthlySnapshotResult>> monthlySnapshots =
            new MutableLiveData<>();
    private final MutableLiveData<Integer> selectedPeriodMonths = new MutableLiveData<>(6);

    public AnalyticsViewModel(@NonNull Application application) {
        super(application);
        transactionRepository = new TransactionRepository(application);

        // Load defaults for current month
        loadCategorySummary(DateUtils.getCurrentMonth(), DateUtils.getCurrentYear());
        loadMonthlySnapshots(6);
    }

    // ── Category breakdown for a given month ───────────────────────────────

    public void loadCategorySummary(int month, int year) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Date start = DateUtils.getStartOfMonth(month, year);
            Date end = DateUtils.getEndOfMonth(month, year);
            List<TransactionDao.CategorySummaryResult> results =
                    transactionRepository.getMonthlyCategorySummary(start, end, TransactionType.EXPENSE);
            categorySummaries.postValue(results);
        });
    }

    // ── Monthly income/expense snapshots ───────────────────────────────────

    public void loadMonthlySnapshots(int monthsBack) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Date startDate = DateUtils.getMonthsAgo(monthsBack);
            List<TransactionDao.MonthlySnapshotResult> results =
                    transactionRepository.getMonthlySnapshots(startDate);
            monthlySnapshots.postValue(results);
        });
    }

    // ── Balance trend (running balance from snapshots) ─────────────────────

    public void loadBalanceTrend(int months) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Date startDate = DateUtils.getMonthsAgo(months);
            List<TransactionDao.MonthlySnapshotResult> results =
                    transactionRepository.getMonthlySnapshots(startDate);
            // The UI layer calculates running balance from income - expense per month
            monthlySnapshots.postValue(results);
        });
    }

    // ── Period selection ───────────────────────────────────────────────────

    public void setPeriod(int months) {
        selectedPeriodMonths.setValue(months);
        loadMonthlySnapshots(months);
    }

    // ── Getters ────────────────────────────────────────────────────────────

    public LiveData<List<TransactionDao.CategorySummaryResult>> getCategorySummaries() {
        return categorySummaries;
    }

    public LiveData<List<TransactionDao.MonthlySnapshotResult>> getMonthlySnapshots() {
        return monthlySnapshots;
    }

    public LiveData<Integer> getSelectedPeriodMonths() {
        return selectedPeriodMonths;
    }
}
