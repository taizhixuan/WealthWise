package com.wealthwise.app.data.remote;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.wealthwise.app.data.local.entity.BudgetEntity;
import com.wealthwise.app.data.local.entity.CategoryEntity;
import com.wealthwise.app.data.local.entity.TransactionEntity;
import com.wealthwise.app.data.model.TransactionType;
import com.wealthwise.app.data.remote.dto.BudgetDto;
import com.wealthwise.app.data.remote.dto.CategoryDto;
import com.wealthwise.app.data.remote.dto.TransactionDto;
import com.wealthwise.app.util.Constants;

import java.util.Date;
import java.util.Map;

public class FirestoreDataSource {

    private final FirebaseFirestore firestore;

    public FirestoreDataSource() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    private String getUserId() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return null;
    }

    private CollectionReference getUserCollection(String collection) {
        String uid = getUserId();
        if (uid == null) return null;
        return firestore.collection(Constants.FIRESTORE_USERS)
                .document(uid)
                .collection(collection);
    }

    // Transaction operations
    public Task<DocumentReference> addTransaction(TransactionDto dto) {
        CollectionReference ref = getUserCollection(Constants.FIRESTORE_TRANSACTIONS);
        if (ref == null) return null;
        return ref.add(dto.toMap());
    }

    public Task<Void> updateTransaction(String firebaseId, TransactionDto dto) {
        CollectionReference ref = getUserCollection(Constants.FIRESTORE_TRANSACTIONS);
        if (ref == null) return null;
        return ref.document(firebaseId).update(dto.toMap());
    }

    public Task<Void> deleteTransaction(String firebaseId) {
        CollectionReference ref = getUserCollection(Constants.FIRESTORE_TRANSACTIONS);
        if (ref == null) return null;
        return ref.document(firebaseId).delete();
    }

    public Task<QuerySnapshot> getTransactionsSince(Timestamp since) {
        CollectionReference ref = getUserCollection(Constants.FIRESTORE_TRANSACTIONS);
        if (ref == null) return null;
        return ref.whereGreaterThan("updatedAt", since).get();
    }

    // Category operations
    public Task<DocumentReference> addCategory(CategoryDto dto) {
        CollectionReference ref = getUserCollection(Constants.FIRESTORE_CATEGORIES);
        if (ref == null) return null;
        return ref.add(dto.toMap());
    }

    public Task<Void> updateCategory(String firebaseId, CategoryDto dto) {
        CollectionReference ref = getUserCollection(Constants.FIRESTORE_CATEGORIES);
        if (ref == null) return null;
        return ref.document(firebaseId).update(dto.toMap());
    }

    public Task<QuerySnapshot> getCategoriesSince(Timestamp since) {
        CollectionReference ref = getUserCollection(Constants.FIRESTORE_CATEGORIES);
        if (ref == null) return null;
        return ref.whereGreaterThan("updatedAt", since).get();
    }

    // Budget operations
    public Task<DocumentReference> addBudget(BudgetDto dto) {
        CollectionReference ref = getUserCollection(Constants.FIRESTORE_BUDGETS);
        if (ref == null) return null;
        return ref.add(dto.toMap());
    }

    public Task<Void> updateBudget(String firebaseId, BudgetDto dto) {
        CollectionReference ref = getUserCollection(Constants.FIRESTORE_BUDGETS);
        if (ref == null) return null;
        return ref.document(firebaseId).update(dto.toMap());
    }

    public Task<QuerySnapshot> getBudgetsSince(Timestamp since) {
        CollectionReference ref = getUserCollection(Constants.FIRESTORE_BUDGETS);
        if (ref == null) return null;
        return ref.whereGreaterThan("updatedAt", since).get();
    }

    // Utility
    public TransactionDto entityToDto(TransactionEntity entity) {
        TransactionDto dto = new TransactionDto();
        dto.setType(entity.getType().name());
        dto.setAmount(entity.getAmount());
        dto.setCategoryId(entity.getCategoryId());
        dto.setAccountId(entity.getAccountId());
        dto.setDate(new Timestamp(entity.getDate()));
        dto.setNote(entity.getNote());
        dto.setPayee(entity.getPayee());
        dto.setLocalId(entity.getId());
        return dto;
    }

    public CategoryDto entityToDto(CategoryEntity entity) {
        CategoryDto dto = new CategoryDto();
        dto.setName(entity.getName());
        dto.setType(entity.getType().name());
        dto.setIconName(entity.getIconName());
        dto.setColorHex(entity.getColorHex());
        dto.setDefault(entity.isDefault());
        dto.setLocalId(entity.getId());
        return dto;
    }

    public BudgetDto entityToDto(BudgetEntity entity) {
        BudgetDto dto = new BudgetDto();
        dto.setCategoryId(entity.getCategoryId());
        dto.setLimitAmount(entity.getLimitAmount());
        dto.setMonth(entity.getMonth());
        dto.setYear(entity.getYear());
        dto.setRollover(entity.isRollover());
        dto.setLocalId(entity.getId());
        return dto;
    }

    // ── DTO → Entity reverse conversions ───────────────────────────────────

    public TransactionEntity dtoToEntity(TransactionDto dto, String firebaseId) {
        TransactionEntity entity = new TransactionEntity();
        entity.setFirebaseId(firebaseId);
        try {
            entity.setType(TransactionType.valueOf(dto.getType()));
        } catch (IllegalArgumentException | NullPointerException e) {
            entity.setType(TransactionType.EXPENSE);
        }
        entity.setAmount(dto.getAmount());
        entity.setCategoryId(dto.getCategoryId());
        entity.setAccountId(dto.getAccountId());
        entity.setDate(dto.getDate() != null ? dto.getDate().toDate() : new Date());
        entity.setNote(dto.getNote());
        entity.setPayee(dto.getPayee());
        entity.setSyncStatus(SyncStatus.SYNCED);
        entity.setUpdatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt().toDate() : new Date());
        return entity;
    }

    public CategoryEntity dtoToEntity(CategoryDto dto, String firebaseId) {
        CategoryEntity entity = new CategoryEntity();
        entity.setFirebaseId(firebaseId);
        entity.setName(dto.getName());
        try {
            entity.setType(TransactionType.valueOf(dto.getType()));
        } catch (IllegalArgumentException | NullPointerException e) {
            entity.setType(TransactionType.EXPENSE);
        }
        entity.setIconName(dto.getIconName());
        entity.setColorHex(dto.getColorHex());
        entity.setDefault(dto.isDefault());
        entity.setSyncStatus(SyncStatus.SYNCED);
        entity.setUpdatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt().toDate() : new Date());
        return entity;
    }

    public BudgetEntity dtoToEntity(BudgetDto dto, String firebaseId) {
        BudgetEntity entity = new BudgetEntity();
        entity.setFirebaseId(firebaseId);
        entity.setCategoryId(dto.getCategoryId());
        entity.setLimitAmount(dto.getLimitAmount());
        entity.setMonth(dto.getMonth());
        entity.setYear(dto.getYear());
        entity.setRollover(dto.isRollover());
        entity.setSyncStatus(SyncStatus.SYNCED);
        entity.setUpdatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt().toDate() : new Date());
        return entity;
    }
}
