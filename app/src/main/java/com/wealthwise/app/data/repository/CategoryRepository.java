package com.wealthwise.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.wealthwise.app.data.local.AppDatabase;
import com.wealthwise.app.data.local.dao.CategoryDao;
import com.wealthwise.app.data.local.entity.CategoryEntity;
import com.wealthwise.app.data.model.TransactionType;
import com.wealthwise.app.data.remote.SyncStatus;

import java.util.Date;
import java.util.List;

public class CategoryRepository {

    private final CategoryDao categoryDao;

    public CategoryRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        categoryDao = db.categoryDao();
    }

    // ── LiveData queries (observed on main thread) ─────────────────────────

    public LiveData<List<CategoryEntity>> getAll() {
        return categoryDao.getAll();
    }

    public LiveData<List<CategoryEntity>> getByType(TransactionType type) {
        return categoryDao.getByType(type);
    }

    public LiveData<CategoryEntity> getById(long id) {
        return categoryDao.getById(id);
    }

    // ── Synchronous queries (called from background threads) ───────────────

    public CategoryEntity getByIdSync(long id) {
        return categoryDao.getByIdSync(id);
    }

    // ── Write operations (executed on background thread) ───────────────────

    public void insert(CategoryEntity entity) {
        AppDatabase.databaseWriteExecutor.execute(() -> categoryDao.insert(entity));
    }

    public void update(CategoryEntity entity) {
        entity.setUpdatedAt(new Date());
        entity.setSyncStatus(SyncStatus.PENDING);
        AppDatabase.databaseWriteExecutor.execute(() -> categoryDao.update(entity));
    }

    public void softDelete(long id) {
        AppDatabase.databaseWriteExecutor.execute(() -> categoryDao.softDelete(id, new Date()));
    }

    // ── Sync-related queries (called from background threads) ──────────────

    public List<CategoryEntity> getPendingSync() {
        return categoryDao.getPendingSync();
    }

    public void updateSyncStatus(long id, SyncStatus status, String firebaseId) {
        categoryDao.updateSyncStatus(id, status, firebaseId);
    }

    public int getCount() {
        return categoryDao.getCount();
    }
}
