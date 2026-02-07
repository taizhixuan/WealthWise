package com.wealthwise.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.wealthwise.app.data.local.AppDatabase;
import com.wealthwise.app.data.local.dao.BudgetDao;
import com.wealthwise.app.data.local.entity.BudgetEntity;
import com.wealthwise.app.data.local.relation.BudgetWithCategory;
import com.wealthwise.app.data.remote.SyncStatus;

import java.util.Date;
import java.util.List;

public class BudgetRepository {

    private final BudgetDao budgetDao;

    public BudgetRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        budgetDao = db.budgetDao();
    }

    // ── LiveData queries (observed on main thread) ─────────────────────────

    public LiveData<List<BudgetWithCategory>> getAllWithCategory() {
        return budgetDao.getAllWithCategory();
    }

    public LiveData<List<BudgetWithCategory>> getByMonthYear(int month, int year) {
        return budgetDao.getByMonthYear(month, year);
    }

    public LiveData<BudgetEntity> getById(long id) {
        return budgetDao.getById(id);
    }

    // ── Synchronous queries (called from background threads) ───────────────

    public BudgetEntity getByCategoryAndMonth(long categoryId, int month, int year) {
        return budgetDao.getByCategoryAndMonth(categoryId, month, year);
    }

    // ── Write operations (executed on background thread) ───────────────────

    public void insert(BudgetEntity entity) {
        AppDatabase.databaseWriteExecutor.execute(() -> budgetDao.insert(entity));
    }

    public void update(BudgetEntity entity) {
        entity.setUpdatedAt(new Date());
        entity.setSyncStatus(SyncStatus.PENDING);
        AppDatabase.databaseWriteExecutor.execute(() -> budgetDao.update(entity));
    }

    public void softDelete(long id) {
        AppDatabase.databaseWriteExecutor.execute(() -> budgetDao.softDelete(id, new Date()));
    }

    // ── Sync-related queries (called from background threads) ──────────────

    public List<BudgetEntity> getPendingSync() {
        return budgetDao.getPendingSync();
    }

    public void updateSyncStatus(long id, SyncStatus status, String firebaseId) {
        budgetDao.updateSyncStatus(id, status, firebaseId);
    }
}
