package com.wealthwise.app.data.remote;

import android.app.Application;
import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.wealthwise.app.data.local.AppDatabase;
import com.wealthwise.app.data.local.entity.TransactionEntity;
import com.wealthwise.app.data.local.entity.CategoryEntity;
import com.wealthwise.app.data.local.entity.BudgetEntity;
import com.wealthwise.app.data.remote.dto.TransactionDto;
import com.wealthwise.app.data.remote.dto.CategoryDto;
import com.wealthwise.app.data.remote.dto.BudgetDto;
import com.wealthwise.app.util.PreferenceManager;

import java.util.Date;
import java.util.List;

public class SyncManager {

    private static final String TAG = "SyncManager";

    private final AppDatabase database;
    private final FirestoreDataSource firestoreDataSource;
    private final PreferenceManager preferenceManager;

    public SyncManager(Application application) {
        this.database = AppDatabase.getInstance(application);
        this.firestoreDataSource = new FirestoreDataSource();
        this.preferenceManager = new PreferenceManager(application);
    }

    public void syncAll() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Log.d(TAG, "No user logged in, skipping sync");
            return;
        }

        pushPendingTransactions();
        pushPendingCategories();
        pushPendingBudgets();
        pushDeletedTransactions();
        pullRemoteChanges();

        preferenceManager.setLastSyncTime(System.currentTimeMillis());
    }

    private void pushPendingTransactions() {
        List<TransactionEntity> pending = database.transactionDao().getPendingSync();
        for (TransactionEntity entity : pending) {
            TransactionDto dto = firestoreDataSource.entityToDto(entity);
            if (entity.getFirebaseId() == null) {
                firestoreDataSource.addTransaction(dto)
                        .addOnSuccessListener(docRef -> {
                            database.transactionDao().updateSyncStatus(
                                    entity.getId(), SyncStatus.SYNCED, docRef.getId());
                        })
                        .addOnFailureListener(e ->
                                Log.e(TAG, "Failed to push transaction: " + entity.getId(), e));
            } else {
                firestoreDataSource.updateTransaction(entity.getFirebaseId(), dto)
                        .addOnSuccessListener(aVoid -> {
                            database.transactionDao().updateSyncStatus(
                                    entity.getId(), SyncStatus.SYNCED, entity.getFirebaseId());
                        })
                        .addOnFailureListener(e ->
                                Log.e(TAG, "Failed to update transaction: " + entity.getId(), e));
            }
        }
    }

    private void pushDeletedTransactions() {
        List<TransactionEntity> deleted = database.transactionDao().getDeletedPendingSync();
        for (TransactionEntity entity : deleted) {
            if (entity.getFirebaseId() != null) {
                firestoreDataSource.deleteTransaction(entity.getFirebaseId())
                        .addOnSuccessListener(aVoid ->
                                Log.d(TAG, "Deleted transaction from Firestore: " + entity.getFirebaseId()))
                        .addOnFailureListener(e ->
                                Log.e(TAG, "Failed to delete transaction: " + entity.getFirebaseId(), e));
            }
        }
    }

    private void pushPendingCategories() {
        List<CategoryEntity> pending = database.categoryDao().getPendingSync();
        for (CategoryEntity entity : pending) {
            CategoryDto dto = firestoreDataSource.entityToDto(entity);
            if (entity.getFirebaseId() == null) {
                firestoreDataSource.addCategory(dto)
                        .addOnSuccessListener(docRef -> {
                            database.categoryDao().updateSyncStatus(
                                    entity.getId(), SyncStatus.SYNCED, docRef.getId());
                        })
                        .addOnFailureListener(e ->
                                Log.e(TAG, "Failed to push category: " + entity.getId(), e));
            } else {
                firestoreDataSource.updateCategory(entity.getFirebaseId(), dto)
                        .addOnSuccessListener(aVoid -> {
                            database.categoryDao().updateSyncStatus(
                                    entity.getId(), SyncStatus.SYNCED, entity.getFirebaseId());
                        })
                        .addOnFailureListener(e ->
                                Log.e(TAG, "Failed to update category: " + entity.getId(), e));
            }
        }
    }

    private void pushPendingBudgets() {
        List<BudgetEntity> pending = database.budgetDao().getPendingSync();
        for (BudgetEntity entity : pending) {
            BudgetDto dto = firestoreDataSource.entityToDto(entity);
            if (entity.getFirebaseId() == null) {
                firestoreDataSource.addBudget(dto)
                        .addOnSuccessListener(docRef -> {
                            database.budgetDao().updateSyncStatus(
                                    entity.getId(), SyncStatus.SYNCED, docRef.getId());
                        })
                        .addOnFailureListener(e ->
                                Log.e(TAG, "Failed to push budget: " + entity.getId(), e));
            } else {
                firestoreDataSource.updateBudget(entity.getFirebaseId(), dto)
                        .addOnSuccessListener(aVoid -> {
                            database.budgetDao().updateSyncStatus(
                                    entity.getId(), SyncStatus.SYNCED, entity.getFirebaseId());
                        })
                        .addOnFailureListener(e ->
                                Log.e(TAG, "Failed to update budget: " + entity.getId(), e));
            }
        }
    }

    private void pullRemoteChanges() {
        long lastSync = preferenceManager.getLastSyncTime();
        Timestamp since = new Timestamp(new Date(lastSync));

        // Pull transactions - last-write-wins conflict resolution
        firestoreDataSource.getTransactionsSince(since)
                .addOnSuccessListener(querySnapshot -> {
                    Log.d(TAG, "Pulled " + querySnapshot.size() + " remote transactions");
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            TransactionDto dto = doc.toObject(TransactionDto.class);
                            if (dto == null) continue;
                            String firebaseId = doc.getId();
                            TransactionEntity existing = database.transactionDao()
                                    .getByFirebaseIdSync(firebaseId);
                            TransactionEntity entity = firestoreDataSource
                                    .dtoToEntity(dto, firebaseId);
                            if (existing != null) {
                                entity.setId(existing.getId());
                                database.transactionDao().update(entity);
                            } else {
                                database.transactionDao().insert(entity);
                            }
                        }
                    });
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to pull transactions", e));

        firestoreDataSource.getCategoriesSince(since)
                .addOnSuccessListener(querySnapshot -> {
                    Log.d(TAG, "Pulled " + querySnapshot.size() + " remote categories");
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            CategoryDto dto = doc.toObject(CategoryDto.class);
                            if (dto == null) continue;
                            String firebaseId = doc.getId();
                            CategoryEntity existing = database.categoryDao()
                                    .getByFirebaseIdSync(firebaseId);
                            CategoryEntity entity = firestoreDataSource
                                    .dtoToEntity(dto, firebaseId);
                            if (existing != null) {
                                entity.setId(existing.getId());
                                database.categoryDao().update(entity);
                            } else {
                                database.categoryDao().insert(entity);
                            }
                        }
                    });
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to pull categories", e));

        firestoreDataSource.getBudgetsSince(since)
                .addOnSuccessListener(querySnapshot -> {
                    Log.d(TAG, "Pulled " + querySnapshot.size() + " remote budgets");
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            BudgetDto dto = doc.toObject(BudgetDto.class);
                            if (dto == null) continue;
                            String firebaseId = doc.getId();
                            BudgetEntity existing = database.budgetDao()
                                    .getByFirebaseIdSync(firebaseId);
                            BudgetEntity entity = firestoreDataSource
                                    .dtoToEntity(dto, firebaseId);
                            if (existing != null) {
                                entity.setId(existing.getId());
                                database.budgetDao().update(entity);
                            } else {
                                database.budgetDao().insert(entity);
                            }
                        }
                    });
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to pull budgets", e));
    }
}
