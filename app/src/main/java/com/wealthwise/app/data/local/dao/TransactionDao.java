package com.wealthwise.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.wealthwise.app.data.local.entity.TransactionEntity;
import com.wealthwise.app.data.local.relation.TransactionWithCategory;
import com.wealthwise.app.data.model.TransactionType;
import com.wealthwise.app.data.remote.SyncStatus;

import java.util.Date;
import java.util.List;

@Dao
public interface TransactionDao {

    // ── Result classes for aggregate queries ──────────────────────────────

    class CategorySummaryResult {
        public long categoryId;
        public String categoryName;
        public String categoryColorHex;
        public String categoryIconName;
        public double totalAmount;
        public int transactionCount;
    }

    class MonthlySnapshotResult {
        public int month;
        public int year;
        public double totalIncome;
        public double totalExpense;
    }

    // ── Relational queries (require @Transaction) ────────────────────────

    @Transaction
    @Query("SELECT * FROM transactions WHERE is_deleted = 0 ORDER BY date DESC")
    LiveData<List<TransactionWithCategory>> getAllWithCategory();

    @Transaction
    @Query("SELECT * FROM transactions WHERE is_deleted = 0 ORDER BY date DESC")
    List<TransactionWithCategory> getAllWithCategorySync();

    @Transaction
    @Query("SELECT * FROM transactions WHERE id = :id")
    LiveData<TransactionWithCategory> getByIdWithCategory(long id);

    @Transaction
    @Query("SELECT * FROM transactions WHERE is_deleted = 0 ORDER BY date DESC LIMIT :limit")
    LiveData<List<TransactionWithCategory>> getRecentTransactions(int limit);

    @Transaction
    @Query("SELECT * FROM transactions WHERE is_deleted = 0 " +
            "AND (note LIKE '%' || :query || '%' OR payee LIKE '%' || :query || '%') " +
            "ORDER BY date DESC")
    LiveData<List<TransactionWithCategory>> searchTransactions(String query);

    // ── Filtered queries ─────────────────────────────────────────────────

    @Query("SELECT * FROM transactions WHERE is_deleted = 0 " +
            "AND date BETWEEN :start AND :end ORDER BY date DESC")
    LiveData<List<TransactionEntity>> getByDateRange(Date start, Date end);

    @Query("SELECT * FROM transactions WHERE is_deleted = 0 " +
            "AND type = :type ORDER BY date DESC")
    LiveData<List<TransactionEntity>> getByType(TransactionType type);

    @Query("SELECT * FROM transactions WHERE is_deleted = 0 " +
            "AND category_id = :categoryId ORDER BY date DESC")
    LiveData<List<TransactionEntity>> getByCategory(long categoryId);

    // ── Aggregate queries ────────────────────────────────────────────────

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions " +
            "WHERE is_deleted = 0 AND type = :type " +
            "AND date BETWEEN :startOfMonth AND :endOfMonth")
    double getMonthlyTotalByType(TransactionType type, Date startOfMonth, Date endOfMonth);

    @Query("SELECT t.category_id AS categoryId, " +
            "c.name AS categoryName, " +
            "c.color_hex AS categoryColorHex, " +
            "c.icon_name AS categoryIconName, " +
            "SUM(t.amount) AS totalAmount, " +
            "COUNT(t.id) AS transactionCount " +
            "FROM transactions t " +
            "INNER JOIN categories c ON t.category_id = c.id " +
            "WHERE t.is_deleted = 0 AND t.date BETWEEN :start AND :end AND t.type = :type " +
            "GROUP BY t.category_id " +
            "ORDER BY totalAmount DESC")
    List<CategorySummaryResult> getMonthlyCategorySummary(Date start, Date end, TransactionType type);

    @Query("SELECT CAST(strftime('%m', date / 1000, 'unixepoch') AS INTEGER) AS month, " +
            "CAST(strftime('%Y', date / 1000, 'unixepoch') AS INTEGER) AS year, " +
            "SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END) AS totalIncome, " +
            "SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END) AS totalExpense " +
            "FROM transactions WHERE is_deleted = 0 AND date >= :startDate " +
            "GROUP BY year, month ORDER BY year, month")
    List<MonthlySnapshotResult> getMonthlySnapshots(Date startDate);

    // ── Sync-related queries ─────────────────────────────────────────────

    @Query("SELECT * FROM transactions WHERE firebase_id = :firebaseId LIMIT 1")
    TransactionEntity getByFirebaseIdSync(String firebaseId);

    @Query("SELECT * FROM transactions WHERE sync_status = 'PENDING' AND is_deleted = 0")
    List<TransactionEntity> getPendingSync();

    @Query("SELECT * FROM transactions WHERE is_deleted = 1 AND sync_status = 'PENDING'")
    List<TransactionEntity> getDeletedPendingSync();

    // ── Insert / Update / Delete ─────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TransactionEntity transaction);

    @Update
    void update(TransactionEntity transaction);

    @Query("UPDATE transactions SET is_deleted = 1, sync_status = 'PENDING', updated_at = :now WHERE id = :id")
    void softDelete(long id, Date now);

    @Query("UPDATE transactions SET sync_status = :status, firebase_id = :firebaseId WHERE id = :id")
    void updateSyncStatus(long id, SyncStatus status, String firebaseId);

    // ── Counting queries ─────────────────────────────────────────────────

    @Query("SELECT COUNT(*) FROM transactions WHERE is_deleted = 0 " +
            "AND category_id = :categoryId AND date BETWEEN :start AND :end")
    int getCountByCategory(long categoryId, Date start, Date end);

    @Query("SELECT COUNT(*) FROM transactions WHERE is_deleted = 0 " +
            "AND category_id = :categoryId AND amount < :maxAmount " +
            "AND date BETWEEN :start AND :end")
    int getSmallTransactionCount(long categoryId, double maxAmount, Date start, Date end);
}
