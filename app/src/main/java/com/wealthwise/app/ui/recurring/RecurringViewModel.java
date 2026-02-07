package com.wealthwise.app.ui.recurring;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.wealthwise.app.data.local.entity.AccountEntity;
import com.wealthwise.app.data.local.entity.CategoryEntity;
import com.wealthwise.app.data.local.entity.RecurringTransactionEntity;
import com.wealthwise.app.data.repository.AccountRepository;
import com.wealthwise.app.data.repository.CategoryRepository;
import com.wealthwise.app.data.repository.RecurringTransactionRepository;

import java.util.List;

public class RecurringViewModel extends AndroidViewModel {

    private final RecurringTransactionRepository recurringRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;

    private final LiveData<List<RecurringTransactionEntity>> allRecurring;
    private final LiveData<List<RecurringTransactionEntity>> activeRecurring;
    private final LiveData<List<CategoryEntity>> categories;
    private final LiveData<List<AccountEntity>> accounts;

    public RecurringViewModel(@NonNull Application application) {
        super(application);
        recurringRepository = new RecurringTransactionRepository(application);
        categoryRepository = new CategoryRepository(application);
        accountRepository = new AccountRepository(application);

        allRecurring = recurringRepository.getAll();
        activeRecurring = recurringRepository.getActive();
        categories = categoryRepository.getAll();
        accounts = accountRepository.getAll();
    }

    // ── CRUD operations ────────────────────────────────────────────────────

    public void insert(RecurringTransactionEntity entity) {
        recurringRepository.insert(entity);
    }

    public void update(RecurringTransactionEntity entity) {
        recurringRepository.update(entity);
    }

    public void softDelete(long id) {
        recurringRepository.softDelete(id);
    }

    // ── Getters ────────────────────────────────────────────────────────────

    public LiveData<List<RecurringTransactionEntity>> getAllRecurring() {
        return allRecurring;
    }

    public LiveData<List<RecurringTransactionEntity>> getActiveRecurring() {
        return activeRecurring;
    }

    public LiveData<List<CategoryEntity>> getCategories() {
        return categories;
    }

    public LiveData<List<AccountEntity>> getAccounts() {
        return accounts;
    }

    public LiveData<RecurringTransactionEntity> getRecurringById(long id) {
        return recurringRepository.getById(id);
    }
}
