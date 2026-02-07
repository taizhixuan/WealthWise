package com.wealthwise.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.wealthwise.app.data.local.entity.BudgetEntity;
import com.wealthwise.app.data.local.relation.BudgetWithCategory;
import com.wealthwise.app.data.remote.SyncStatus;

import java.util.Date;
import java.util.List;

@Dao
public interface BudgetDao {

    @Transaction
    @Query("SELECT * FROM budgets WHERE is_deleted = 0 ORDER BY year DESC, month DESC")
    LiveData<List<BudgetWithCategory>> getAllWithCategory();

    @Transaction
    @Query("SELECT * FROM budgets WHERE is_deleted = 0 AND month = :month AND year = :year")
    LiveData<List<BudgetWithCategory>> getByMonthYear(int month, int year);

    @Query("SELECT * FROM budgets WHERE id = :id")
    LiveData<BudgetEntity> getById(long id);

    @Query("SELECT * FROM budgets WHERE category_id = :categoryId AND month = :month AND year = :year AND is_deleted = 0")
    BudgetEntity getByCategoryAndMonth(long categoryId, int month, int year);

    @Query("SELECT * FROM budgets WHERE firebase_id = :firebaseId LIMIT 1")
    BudgetEntity getByFirebaseIdSync(String firebaseId);

    @Query("SELECT * FROM budgets WHERE sync_status = 'PENDING' AND is_deleted = 0")
    List<BudgetEntity> getPendingSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(BudgetEntity budget);

    @Update
    void update(BudgetEntity budget);

    @Query("UPDATE budgets SET is_deleted = 1, sync_status = 'PENDING', updated_at = :now WHERE id = :id")
    void softDelete(long id, Date now);

    @Query("UPDATE budgets SET sync_status = :status, firebase_id = :firebaseId WHERE id = :id")
    void updateSyncStatus(long id, SyncStatus status, String firebaseId);
}
