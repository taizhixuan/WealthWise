package com.wealthwise.app.data.local.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.wealthwise.app.data.local.entity.CategoryEntity;
import com.wealthwise.app.data.local.entity.TransactionEntity;

import java.util.List;

public class CategoryWithTransactions {

    @Embedded
    public CategoryEntity category;

    @Relation(parentColumn = "id", entityColumn = "category_id")
    public List<TransactionEntity> transactions;
}
