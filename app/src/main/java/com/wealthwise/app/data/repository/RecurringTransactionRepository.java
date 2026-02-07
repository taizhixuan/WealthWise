package com.wealthwise.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.wealthwise.app.data.local.AppDatabase;
import com.wealthwise.app.data.local.dao.RecurringTransactionDao;
import com.wealthwise.app.data.local.entity.RecurringTransactionEntity;
import com.wealthwise.app.data.remote.SyncStatus;

import java.util.Date;
import java.util.List;

public class RecurringTransactionRepository {

    private final RecurringTransactionDao recurringTransactionDao;

    public RecurringTransactionRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        recurringTransactionDao = db.recurringTransactionDao();
    }

    // ── LiveData queries (observed on main thread) ─────────────────────────

    public LiveData<List<RecurringTransactionEntity>> getAll() {
        return recurringTransactionDao.getAll();
    }

    public LiveData<List<RecurringTransactionEntity>> getActive() {
        return recurringTransactionDao.getActive();
    }

    public LiveData<RecurringTransactionEntity> getById(long id) {
        return recurringTransactionDao.getById(id);
    }

    // ── Synchronous queries (called from background threads) ───────────────

    public List<RecurringTransactionEntity> getDueRecurring(Date now) {
        return recurringTransactionDao.getDueRecurring(now);
    }

    // ── Write operations (executed on background thread) ───────────────────

    public void insert(RecurringTransactionEntity entity) {
        AppDatabase.databaseWriteExecutor.execute(() -> recurringTransactionDao.insert(entity));
    }

    public void update(RecurringTransactionEntity entity) {
        entity.setUpdatedAt(new Date());
        entity.setSyncStatus(SyncStatus.PENDING);
        AppDatabase.databaseWriteExecutor.execute(() -> recurringTransactionDao.update(entity));
    }

    public void softDelete(long id) {
        AppDatabase.databaseWriteExecutor.execute(() ->
                recurringTransactionDao.softDelete(id, new Date()));
    }

    public void updateNextOccurrence(long id, Date nextOccurrence) {
        AppDatabase.databaseWriteExecutor.execute(() ->
                recurringTransactionDao.updateNextOccurrence(id, nextOccurrence, new Date()));
    }

    // ── Sync-related queries (called from background threads) ──────────────

    public List<RecurringTransactionEntity> getPendingSync() {
        return recurringTransactionDao.getPendingSync();
    }

    public void updateSyncStatus(long id, SyncStatus status, String firebaseId) {
        recurringTransactionDao.updateSyncStatus(id, status, firebaseId);
    }
}
