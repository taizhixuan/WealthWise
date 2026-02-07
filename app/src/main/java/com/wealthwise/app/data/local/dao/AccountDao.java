package com.wealthwise.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.wealthwise.app.data.local.entity.AccountEntity;
import com.wealthwise.app.data.remote.SyncStatus;

import java.util.Date;
import java.util.List;

@Dao
public interface AccountDao {

    @Query("SELECT * FROM accounts WHERE is_deleted = 0 ORDER BY name")
    LiveData<List<AccountEntity>> getAll();

    @Query("SELECT * FROM accounts WHERE id = :id")
    LiveData<AccountEntity> getById(long id);

    @Query("SELECT * FROM accounts WHERE id = :id")
    AccountEntity getByIdSync(long id);

    @Query("SELECT * FROM accounts WHERE sync_status = 'PENDING' AND is_deleted = 0")
    List<AccountEntity> getPendingSync();

    @Query("SELECT SUM(balance) FROM accounts WHERE is_deleted = 0")
    LiveData<Double> getTotalBalance();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(AccountEntity account);

    @Update
    void update(AccountEntity account);

    @Query("UPDATE accounts SET is_deleted = 1, sync_status = 'PENDING', updated_at = :now WHERE id = :id")
    void softDelete(long id, Date now);

    @Query("UPDATE accounts SET balance = :balance, updated_at = :updatedAt WHERE id = :id")
    void updateBalance(long id, double balance, Date updatedAt);

    @Query("UPDATE accounts SET sync_status = :status, firebase_id = :firebaseId WHERE id = :id")
    void updateSyncStatus(long id, SyncStatus status, String firebaseId);
}
