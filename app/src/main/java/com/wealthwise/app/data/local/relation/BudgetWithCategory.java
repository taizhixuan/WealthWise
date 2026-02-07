package com.wealthwise.app.data.local.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.wealthwise.app.data.local.entity.BudgetEntity;
import com.wealthwise.app.data.local.entity.CategoryEntity;

public class BudgetWithCategory {

    @Embedded
    public BudgetEntity budget;

    @Relation(parentColumn = "category_id", entityColumn = "id")
    public CategoryEntity category;
}
