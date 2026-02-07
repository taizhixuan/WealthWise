package com.wealthwise.app.ui.budget;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.wealthwise.app.data.local.AppDatabase;
import com.wealthwise.app.data.local.entity.BudgetEntity;
import com.wealthwise.app.data.local.entity.CategoryEntity;
import com.wealthwise.app.data.local.relation.BudgetWithCategory;
import com.wealthwise.app.data.model.TransactionType;
import com.wealthwise.app.data.repository.BudgetRepository;
import com.wealthwise.app.data.repository.CategoryRepository;
import com.wealthwise.app.data.repository.TransactionRepository;
import com.wealthwise.app.util.DateUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BudgetViewModel extends AndroidViewModel {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    private final MutableLiveData<Integer> selectedMonth = new MutableLiveData<>();
    private final MutableLiveData<Integer> selectedYear = new MutableLiveData<>();
    private final MutableLiveData<Map<Long, Double>> spentAmounts = new MutableLiveData<>(new HashMap<>());

    private final LiveData<List<CategoryEntity>> categories;
    private LiveData<List<BudgetWithCategory>> budgets;

    public BudgetViewModel(@NonNull Application application) {
        super(application);
        budgetRepository = new BudgetRepository(application);
        categoryRepository = new CategoryRepository(application);
        transactionRepository = new TransactionRepository(application);

        categories = categoryRepository.getByType(TransactionType.EXPENSE);

        // Initialize to current month/year
        int currentMonth = DateUtils.getCurrentMonth();
        int currentYear = DateUtils.getCurrentYear();
        selectedMonth.setValue(currentMonth);
        selectedYear.setValue(currentYear);

        budgets = budgetRepository.getByMonthYear(currentMonth, currentYear);
        loadSpentAmounts();
    }

    // ── Load budgets for a given month/year ────────────────────────────────

    public void loadBudgets(int month, int year) {
        selectedMonth.setValue(month);
        selectedYear.setValue(year);
        budgets = budgetRepository.getByMonthYear(month, year);
        loadSpentAmounts();
    }

    // ── Calculate spent amounts per category for selected month ────────────

    public void loadSpentAmounts() {
        Integer month = selectedMonth.getValue();
        Integer year = selectedYear.getValue();
        if (month == null || year == null) return;

        AppDatabase.databaseWriteExecutor.execute(() -> {
            Date start = DateUtils.getStartOfMonth(month, year);
            Date end = DateUtils.getEndOfMonth(month, year);

            List<com.wealthwise.app.data.local.dao.TransactionDao.CategorySummaryResult> summaries =
                    transactionRepository.getMonthlyCategorySummary(start, end, TransactionType.EXPENSE);

            Map<Long, Double> amounts = new HashMap<>();
            for (com.wealthwise.app.data.local.dao.TransactionDao.CategorySummaryResult summary : summaries) {
                amounts.put(summary.categoryId, summary.totalAmount);
            }
            spentAmounts.postValue(amounts);
        });
    }

    // ── CRUD operations ────────────────────────────────────────────────────

    public void insert(BudgetEntity budget) {
        budgetRepository.insert(budget);
    }

    public void update(BudgetEntity budget) {
        budgetRepository.update(budget);
    }

    public void softDelete(long id) {
        budgetRepository.softDelete(id);
    }

    public LiveData<BudgetEntity> getBudgetById(long id) {
        return budgetRepository.getById(id);
    }

    // ── Month navigation ───────────────────────────────────────────────────

    public void nextMonth() {
        Integer month = selectedMonth.getValue();
        Integer year = selectedYear.getValue();
        if (month == null || year == null) return;

        if (month == 12) {
            loadBudgets(1, year + 1);
        } else {
            loadBudgets(month + 1, year);
        }
    }

    public void previousMonth() {
        Integer month = selectedMonth.getValue();
        Integer year = selectedYear.getValue();
        if (month == null || year == null) return;

        if (month == 1) {
            loadBudgets(12, year - 1);
        } else {
            loadBudgets(month - 1, year);
        }
    }

    // ── Getters ────────────────────────────────────────────────────────────

    public LiveData<List<BudgetWithCategory>> getBudgets() {
        return budgets;
    }

    public LiveData<Integer> getSelectedMonth() {
        return selectedMonth;
    }

    public LiveData<Integer> getSelectedYear() {
        return selectedYear;
    }

    public LiveData<Map<Long, Double>> getSpentAmounts() {
        return spentAmounts;
    }

    public LiveData<List<CategoryEntity>> getCategories() {
        return categories;
    }
}
