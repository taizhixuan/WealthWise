package com.wealthwise.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.wealthwise.app.data.local.entity.RecurringTransactionEntity;
import com.wealthwise.app.data.remote.SyncStatus;

import java.util.Date;
import java.util.List;

@Dao
public interface RecurringTransactionDao {

    @Query("SELECT * FROM recurring_transactions WHERE is_deleted = 0 ORDER BY next_occurrence")
    LiveData<List<RecurringTransactionEntity>> getAll();

    @Query("SELECT * FROM recurring_transactions WHERE is_deleted = 0 AND is_active = 1 ORDER BY next_occurrence")
    LiveData<List<RecurringTransactionEntity>> getActive();

    @Query("SELECT * FROM recurring_transactions WHERE is_active = 1 AND is_deleted = 0 AND next_occurrence <= :now")
    List<RecurringTransactionEntity> getDueRecurring(Date now);

    @Query("SELECT * FROM recurring_transactions WHERE id = :id")
    LiveData<RecurringTransactionEntity> getById(long id);

    @Query("SELECT * FROM recurring_transactions WHERE sync_status = 'PENDING' AND is_deleted = 0")
    List<RecurringTransactionEntity> getPendingSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(RecurringTransactionEntity recurringTransaction);

    @Update
    void update(RecurringTransactionEntity recurringTransaction);

    @Query("UPDATE recurring_transactions SET is_deleted = 1, sync_status = 'PENDING', updated_at = :now WHERE id = :id")
    void softDelete(long id, Date now);

    @Query("UPDATE recurring_transactions SET next_occurrence = :nextOccurrence, updated_at = :updatedAt WHERE id = :id")
    void updateNextOccurrence(long id, Date nextOccurrence, Date updatedAt);

    @Query("UPDATE recurring_transactions SET sync_status = :status, firebase_id = :firebaseId WHERE id = :id")
    void updateSyncStatus(long id, SyncStatus status, String firebaseId);
}
