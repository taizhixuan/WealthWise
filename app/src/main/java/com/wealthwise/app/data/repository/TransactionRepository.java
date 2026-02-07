package com.wealthwise.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.wealthwise.app.data.local.AppDatabase;
import com.wealthwise.app.data.local.dao.TransactionDao;
import com.wealthwise.app.data.local.entity.TransactionEntity;
import com.wealthwise.app.data.local.relation.TransactionWithCategory;
import com.wealthwise.app.data.model.TransactionType;
import com.wealthwise.app.data.remote.SyncStatus;

import java.util.Date;
import java.util.List;

public class TransactionRepository {

    private final TransactionDao transactionDao;

    public TransactionRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        transactionDao = db.transactionDao();
    }

    // ── LiveData queries (observed on main thread) ─────────────────────────

    public LiveData<List<TransactionWithCategory>> getAllWithCategory() {
        return transactionDao.getAllWithCategory();
    }

    public LiveData<TransactionWithCategory> getByIdWithCategory(long id) {
        return transactionDao.getByIdWithCategory(id);
    }

    public LiveData<List<TransactionEntity>> getByDateRange(Date start, Date end) {
        return transactionDao.getByDateRange(start, end);
    }

    public LiveData<List<TransactionEntity>> getByType(TransactionType type) {
        return transactionDao.getByType(type);
    }

    public LiveData<List<TransactionEntity>> getByCategory(long categoryId) {
        return transactionDao.getByCategory(categoryId);
    }

    public LiveData<List<TransactionWithCategory>> getRecentTransactions(int limit) {
        return transactionDao.getRecentTransactions(limit);
    }

    public LiveData<List<TransactionWithCategory>> searchTransactions(String query) {
        return transactionDao.searchTransactions(query);
    }

    // ── Write operations (executed on background thread) ───────────────────

    public void insert(TransactionEntity entity) {
        AppDatabase.databaseWriteExecutor.execute(() -> transactionDao.insert(entity));
    }

    public void update(TransactionEntity entity) {
        entity.setUpdatedAt(new Date());
        entity.setSyncStatus(SyncStatus.PENDING);
        AppDatabase.databaseWriteExecutor.execute(() -> transactionDao.update(entity));
    }

    public void softDelete(long id) {
        AppDatabase.databaseWriteExecutor.execute(() -> transactionDao.softDelete(id, new Date()));
    }

    // ── Synchronous queries (called from background threads) ───────────────

    public double getMonthlyTotalByType(TransactionType type, Date start, Date end) {
        return transactionDao.getMonthlyTotalByType(type, start, end);
    }

    public List<TransactionDao.CategorySummaryResult> getMonthlyCategorySummary(
            Date start, Date end, TransactionType type) {
        return transactionDao.getMonthlyCategorySummary(start, end, type);
    }

    public List<TransactionDao.MonthlySnapshotResult> getMonthlySnapshots(Date startDate) {
        return transactionDao.getMonthlySnapshots(startDate);
    }

    public List<TransactionEntity> getPendingSync() {
        return transactionDao.getPendingSync();
    }

    public List<TransactionEntity> getDeletedPendingSync() {
        return transactionDao.getDeletedPendingSync();
    }

    public void updateSyncStatus(long id, SyncStatus status, String firebaseId) {
        transactionDao.updateSyncStatus(id, status, firebaseId);
    }

    public int getCountByCategory(long categoryId, Date start, Date end) {
        return transactionDao.getCountByCategory(categoryId, start, end);
    }

    public int getSmallTransactionCount(long categoryId, double maxAmount,
                                        Date start, Date end) {
        return transactionDao.getSmallTransactionCount(categoryId, maxAmount, start, end);
    }
}
