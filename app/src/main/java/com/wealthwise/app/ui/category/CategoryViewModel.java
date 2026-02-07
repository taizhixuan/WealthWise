package com.wealthwise.app.ui.category;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wealthwise.app.data.local.entity.CategoryEntity;
import com.wealthwise.app.data.model.TransactionType;
import com.wealthwise.app.data.repository.CategoryRepository;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {

    private final CategoryRepository categoryRepository;

    private final LiveData<List<CategoryEntity>> expenseCategories;
    private final LiveData<List<CategoryEntity>> incomeCategories;
    private final MutableLiveData<CategoryEntity> selectedCategory = new MutableLiveData<>();

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        categoryRepository = new CategoryRepository(application);

        expenseCategories = categoryRepository.getByType(TransactionType.EXPENSE);
        incomeCategories = categoryRepository.getByType(TransactionType.INCOME);
    }

    // ── Load single category for edit mode ─────────────────────────────────

    public void loadCategory(long id) {
        LiveData<CategoryEntity> source = categoryRepository.getById(id);
        // The fragment should observe this LiveData directly for reactive updates
    }

    public LiveData<CategoryEntity> getCategoryById(long id) {
        return categoryRepository.getById(id);
    }

    // ── CRUD operations ────────────────────────────────────────────────────

    public void insert(CategoryEntity category) {
        categoryRepository.insert(category);
    }

    public void update(CategoryEntity category) {
        categoryRepository.update(category);
    }

    public void softDelete(long id) {
        categoryRepository.softDelete(id);
    }

    // ── Getters ────────────────────────────────────────────────────────────

    public LiveData<List<CategoryEntity>> getExpenseCategories() {
        return expenseCategories;
    }

    public LiveData<List<CategoryEntity>> getIncomeCategories() {
        return incomeCategories;
    }

    public LiveData<CategoryEntity> getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(CategoryEntity category) {
        selectedCategory.setValue(category);
    }
}
