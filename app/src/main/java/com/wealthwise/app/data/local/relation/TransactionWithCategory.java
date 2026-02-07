package com.wealthwise.app.data.local.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.wealthwise.app.data.local.entity.CategoryEntity;
import com.wealthwise.app.data.local.entity.TransactionEntity;

public class TransactionWithCategory {

    @Embedded
    public TransactionEntity transaction;

    @Relation(parentColumn = "category_id", entityColumn = "id")
    public CategoryEntity category;
}
