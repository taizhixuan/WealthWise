package com.wealthwise.app.ui.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wealthwise.app.data.local.AppDatabase;
import com.wealthwise.app.data.local.relation.BudgetWithCategory;
import com.wealthwise.app.data.local.relation.TransactionWithCategory;
import com.wealthwise.app.data.model.TransactionType;
import com.wealthwise.app.data.repository.AccountRepository;
import com.wealthwise.app.data.repository.BudgetRepository;
import com.wealthwise.app.data.repository.TransactionRepository;
import com.wealthwise.app.util.Constants;
import com.wealthwise.app.util.DateUtils;

import java.util.Date;
import java.util.List;

public class DashboardViewModel extends AndroidViewModel {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final AccountRepository accountRepository;

    private final LiveData<List<TransactionWithCategory>> recentTransactions;
    private final LiveData<List<BudgetWithCategory>> currentBudgets;
    private final LiveData<Double> totalBalance;
    private final MutableLiveData<Double> monthlyIncome = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> monthlyExpense = new MutableLiveData<>(0.0);

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        transactionRepository = new TransactionRepository(application);
        budgetRepository = new BudgetRepository(application);
        accountRepository = new AccountRepository(application);

        recentTransactions = transactionRepository.getRecentTransactions(Constants.MAX_RECENT_TRANSACTIONS);
        currentBudgets = budgetRepository.getByMonthYear(
                DateUtils.getCurrentMonth(), DateUtils.getCurrentYear());
        totalBalance = accountRepository.getTotalBalance();

        loadMonthlyTotals();
    }

    private void loadMonthlyTotals() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Date start = DateUtils.getStartOfCurrentMonth();
            Date end = DateUtils.getEndOfCurrentMonth();
            double income = transactionRepository.getMonthlyTotalByType(TransactionType.INCOME, start, end);
            double expense = transactionRepository.getMonthlyTotalByType(TransactionType.EXPENSE, start, end);
            monthlyIncome.postValue(income);
            monthlyExpense.postValue(expense);
        });
    }

    public void refresh() {
        loadMonthlyTotals();
    }

    public LiveData<List<TransactionWithCategory>> getRecentTransactions() {
        return recentTransactions;
    }

    public LiveData<List<BudgetWithCategory>> getCurrentBudgets() {
        return currentBudgets;
    }

    public LiveData<Double> getTotalBalance() {
        return totalBalance;
    }

    public LiveData<Double> getMonthlyIncome() {
        return monthlyIncome;
    }

    public LiveData<Double> getMonthlyExpense() {
        return monthlyExpense;
    }
}
