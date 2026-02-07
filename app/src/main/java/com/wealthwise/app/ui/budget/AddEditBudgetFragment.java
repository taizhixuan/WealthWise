package com.wealthwise.app.ui.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.wealthwise.app.data.local.entity.BudgetEntity;
import com.wealthwise.app.data.local.entity.CategoryEntity;
import com.wealthwise.app.databinding.FragmentAddEditBudgetBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddEditBudgetFragment extends Fragment {
    private FragmentAddEditBudgetBinding binding;
    private BudgetViewModel viewModel;
    private long budgetId = -1;
    private BudgetEntity existingBudget;

    private int selectedMonth;
    private int selectedYear;
    private long selectedCategoryId = -1;
    private List<CategoryEntity> categoryList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddEditBudgetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(BudgetViewModel.class);

        if (getArguments() != null) {
            budgetId = getArguments().getLong("budgetId", -1);
        }

        Calendar calendar = Calendar.getInstance();
        selectedMonth = calendar.get(Calendar.MONTH) + 1;
        selectedYear = calendar.get(Calendar.YEAR);

        setupCategoryDropdown();
        setupMonthYearPickers();
        setupSaveButton();

        if (budgetId != -1) {
            loadExistingBudget();
        }
    }

    private void setupCategoryDropdown() {
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                categoryList = categories;
                List<String> categoryNames = new ArrayList<>();
                for (CategoryEntity category : categories) {
                    categoryNames.add(category.getName());
                }

                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        categoryNames
                );
                binding.actvCategory.setAdapter(categoryAdapter);

                binding.actvCategory.setOnItemClickListener((parent, v, position, id) ->
                        selectedCategoryId = categoryList.get(position).getId());

                if (existingBudget != null) {
                    for (int i = 0; i < categories.size(); i++) {
                        if (categories.get(i).getId() == existingBudget.getCategoryId()) {
                            binding.actvCategory.setText(categories.get(i).getName(), false);
                            selectedCategoryId = existingBudget.getCategoryId();
                            break;
                        }
                    }
                }
            }
        });
    }

    private void setupMonthYearPickers() {
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                months
        );
        binding.actvMonth.setAdapter(monthAdapter);
        binding.actvMonth.setText(months[selectedMonth - 1], false);
        binding.actvMonth.setOnItemClickListener((parent, v, position, id) ->
                selectedMonth = position + 1);

        List<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int y = currentYear - 1; y <= currentYear + 2; y++) {
            years.add(String.valueOf(y));
        }

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                years
        );
        binding.actvYear.setAdapter(yearAdapter);
        binding.actvYear.setText(String.valueOf(selectedYear), false);
        binding.actvYear.setOnItemClickListener((parent, v, position, id) ->
                selectedYear = Integer.parseInt(years.get(position)));
    }

    private void setupSaveButton() {
        binding.btnSave.setOnClickListener(v -> saveBudget());
    }

    private void loadExistingBudget() {
        viewModel.getBudgetById(budgetId).observe(getViewLifecycleOwner(), budget -> {
            if (budget != null) {
                existingBudget = budget;
                populateFields(budget);
            }
        });
    }

    private void populateFields(BudgetEntity budget) {
        binding.etLimitAmount.setText(String.valueOf(budget.getLimitAmount()));
        selectedMonth = budget.getMonth();
        selectedYear = budget.getYear();
        binding.switchRollover.setChecked(budget.isRollover());

        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        binding.actvMonth.setText(months[budget.getMonth() - 1], false);
        binding.actvYear.setText(String.valueOf(budget.getYear()), false);
    }

    private void saveBudget() {
        String amountStr = binding.etLimitAmount.getText().toString().trim();

        if (selectedCategoryId == -1) {
            binding.tilCategory.setError("Please select a category");
            return;
        }

        if (amountStr.isEmpty()) {
            binding.tilLimitAmount.setError("Please enter an amount");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            binding.tilLimitAmount.setError("Invalid amount");
            return;
        }

        if (amount <= 0) {
            binding.tilLimitAmount.setError("Amount must be greater than zero");
            return;
        }

        binding.tilCategory.setError(null);
        binding.tilLimitAmount.setError(null);

        BudgetEntity budget;
        if (existingBudget != null) {
            budget = existingBudget;
        } else {
            budget = new BudgetEntity();
        }

        budget.setCategoryId(selectedCategoryId);
        budget.setLimitAmount(amount);
        budget.setMonth(selectedMonth);
        budget.setYear(selectedYear);
        budget.setRollover(binding.switchRollover.isChecked());

        if (existingBudget != null) {
            viewModel.update(budget);
        } else {
            viewModel.insert(budget);
        }

        Toast.makeText(requireContext(), "Budget saved", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(requireView()).popBackStack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
