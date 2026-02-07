package com.wealthwise.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.wealthwise.app.data.local.entity.CategoryEntity;
import com.wealthwise.app.data.model.TransactionType;
import com.wealthwise.app.data.remote.SyncStatus;

import java.util.Date;
import java.util.List;

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM categories WHERE is_deleted = 0 ORDER BY name")
    LiveData<List<CategoryEntity>> getAll();

    @Query("SELECT * FROM categories WHERE is_deleted = 0 AND type = :type ORDER BY name")
    LiveData<List<CategoryEntity>> getByType(TransactionType type);

    @Query("SELECT * FROM categories WHERE id = :id")
    LiveData<CategoryEntity> getById(long id);

    @Query("SELECT * FROM categories WHERE id = :id")
    CategoryEntity getByIdSync(long id);

    @Query("SELECT * FROM categories WHERE firebase_id = :firebaseId LIMIT 1")
    CategoryEntity getByFirebaseIdSync(String firebaseId);

    @Query("SELECT * FROM categories WHERE sync_status = 'PENDING' AND is_deleted = 0")
    List<CategoryEntity> getPendingSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CategoryEntity category);

    @Update
    void update(CategoryEntity category);

    @Query("UPDATE categories SET is_deleted = 1, sync_status = 'PENDING', updated_at = :now WHERE id = :id")
    void softDelete(long id, Date now);

    @Query("UPDATE categories SET sync_status = :status, firebase_id = :firebaseId WHERE id = :id")
    void updateSyncStatus(long id, SyncStatus status, String firebaseId);

    @Query("SELECT COUNT(*) FROM categories WHERE is_deleted = 0")
    int getCount();
}
