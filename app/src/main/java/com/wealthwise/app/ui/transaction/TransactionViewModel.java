package com.wealthwise.app.ui.transaction;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wealthwise.app.data.local.AppDatabase;
import com.wealthwise.app.data.local.entity.AccountEntity;
import com.wealthwise.app.data.local.entity.CategoryEntity;
import com.wealthwise.app.data.local.entity.RecurringTransactionEntity;
import com.wealthwise.app.data.local.entity.TransactionEntity;
import com.wealthwise.app.data.local.relation.TransactionWithCategory;
import com.wealthwise.app.data.repository.AccountRepository;
import com.wealthwise.app.data.repository.CategoryRepository;
import com.wealthwise.app.data.repository.RecurringTransactionRepository;
import com.wealthwise.app.data.repository.TransactionRepository;

import java.util.List;

public class TransactionViewModel extends AndroidViewModel {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;
    private final RecurringTransactionRepository recurringRepository;

    private final LiveData<List<TransactionWithCategory>> allTransactions;
    private final LiveData<List<CategoryEntity>> categories;
    private final LiveData<List<AccountEntity>> accounts;
    private final MutableLiveData<TransactionWithCategory> selectedTransaction = new MutableLiveData<>();
    private final MutableLiveData<List<TransactionWithCategory>> searchResults = new MutableLiveData<>();

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        transactionRepository = new TransactionRepository(application);
        categoryRepository = new CategoryRepository(application);
        accountRepository = new AccountRepository(application);
        recurringRepository = new RecurringTransactionRepository(application);

        allTransactions = transactionRepository.getAllWithCategory();
        categories = categoryRepository.getAll();
        accounts = accountRepository.getAll();
    }

    // ── Load single transaction for edit mode ──────────────────────────────

    public void loadTransaction(long id) {
        LiveData<TransactionWithCategory> source = transactionRepository.getByIdWithCategory(id);
        // Observe once and post to selectedTransaction
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // We use the LiveData source directly; the fragment should observe
            // selectedTransaction via getByIdWithCategory for reactive updates.
        });
        // For simplicity, expose the LiveData from repository directly
        selectedTransaction.postValue(null); // reset
    }

    public LiveData<TransactionWithCategory> getTransactionById(long id) {
        return transactionRepository.getByIdWithCategory(id);
    }

    // ── CRUD operations ────────────────────────────────────────────────────

    public void insert(TransactionEntity transaction) {
        transactionRepository.insert(transaction);
    }

    public void update(TransactionEntity transaction) {
        transactionRepository.update(transaction);
    }

    public void softDelete(long id) {
        transactionRepository.softDelete(id);
    }

    public void insertRecurring(RecurringTransactionEntity entity) {
        recurringRepository.insert(entity);
    }

    // ── Search ─────────────────────────────────────────────────────────────

    public void search(String query) {
        if (query == null || query.trim().isEmpty()) {
            searchResults.postValue(null);
            return;
        }
        // searchTransactions returns LiveData; the UI layer should observe it
        // For imperative search, we post the query and let the fragment switch
        // to observing the search LiveData from the repository.
        searchResults.postValue(null);
    }

    public LiveData<List<TransactionWithCategory>> getSearchResults(String query) {
        return transactionRepository.searchTransactions(query);
    }

    // ── Getters ────────────────────────────────────────────────────────────

    public LiveData<List<TransactionWithCategory>> getAllTransactions() {
        return allTransactions;
    }

    public LiveData<List<CategoryEntity>> getCategories() {
        return categories;
    }

    public LiveData<List<AccountEntity>> getAccounts() {
        return accounts;
    }

    public LiveData<TransactionWithCategory> getSelectedTransaction() {
        return selectedTransaction;
    }

    public MutableLiveData<List<TransactionWithCategory>> getSearchResultsLiveData() {
        return searchResults;
    }
}
