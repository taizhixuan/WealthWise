package com.wealthwise.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.wealthwise.app.data.local.AppDatabase;
import com.wealthwise.app.data.local.dao.AccountDao;
import com.wealthwise.app.data.local.entity.AccountEntity;
import com.wealthwise.app.data.remote.SyncStatus;

import java.util.Date;
import java.util.List;

public class AccountRepository {

    private final AccountDao accountDao;

    public AccountRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        accountDao = db.accountDao();
    }

    // ── LiveData queries (observed on main thread) ─────────────────────────

    public LiveData<List<AccountEntity>> getAll() {
        return accountDao.getAll();
    }

    public LiveData<AccountEntity> getById(long id) {
        return accountDao.getById(id);
    }

    public LiveData<Double> getTotalBalance() {
        return accountDao.getTotalBalance();
    }

    // ── Synchronous queries (called from background threads) ───────────────

    public AccountEntity getByIdSync(long id) {
        return accountDao.getByIdSync(id);
    }

    // ── Write operations (executed on background thread) ───────────────────

    public void insert(AccountEntity entity) {
        AppDatabase.databaseWriteExecutor.execute(() -> accountDao.insert(entity));
    }

    public void update(AccountEntity entity) {
        entity.setUpdatedAt(new Date());
        entity.setSyncStatus(SyncStatus.PENDING);
        AppDatabase.databaseWriteExecutor.execute(() -> accountDao.update(entity));
    }

    public void softDelete(long id) {
        AppDatabase.databaseWriteExecutor.execute(() -> accountDao.softDelete(id, new Date()));
    }

    public void updateBalance(long id, double balance) {
        AppDatabase.databaseWriteExecutor.execute(() ->
                accountDao.updateBalance(id, balance, new Date()));
    }

    // ── Sync-related queries (called from background threads) ──────────────

    public List<AccountEntity> getPendingSync() {
        return accountDao.getPendingSync();
    }

    public void updateSyncStatus(long id, SyncStatus status, String firebaseId) {
        accountDao.updateSyncStatus(id, status, firebaseId);
    }
}
