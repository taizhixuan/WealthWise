package com.wealthwise.app.ui.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.wealthwise.app.R;
import com.wealthwise.app.databinding.FragmentBudgetListBinding;
import com.wealthwise.app.util.DateUtils;

import java.util.Calendar;

public class BudgetListFragment extends Fragment {
    private FragmentBudgetListBinding binding;
    private BudgetViewModel viewModel;
    private BudgetAdapter adapter;

    private int selectedMonth;
    private int selectedYear;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBudgetListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(BudgetViewModel.class);

        Calendar calendar = Calendar.getInstance();
        selectedMonth = calendar.get(Calendar.MONTH) + 1;
        selectedYear = calendar.get(Calendar.YEAR);

        setupRecyclerView();
        setupMonthNavigation();
        setupFab();
        observeData();
        loadBudgets();
    }

    private void setupRecyclerView() {
        adapter = new BudgetAdapter(budgetWithCategory -> {
            Bundle args = new Bundle();
            args.putLong("budgetId", budgetWithCategory.budget.getId());
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_budgetList_to_addEditBudget, args);
        });

        binding.rvBudgets.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvBudgets.setAdapter(adapter);
        binding.rvBudgets.setHasFixedSize(true);
    }

    private void setupMonthNavigation() {
        updateMonthDisplay();

        binding.btnPreviousMonth.setOnClickListener(v -> {
            selectedMonth--;
            if (selectedMonth < 1) {
                selectedMonth = 12;
                selectedYear--;
            }
            updateMonthDisplay();
            loadBudgets();
        });

        binding.btnNextMonth.setOnClickListener(v -> {
            selectedMonth++;
            if (selectedMonth > 12) {
                selectedMonth = 1;
                selectedYear++;
            }
            updateMonthDisplay();
            loadBudgets();
        });
    }

    private void updateMonthDisplay() {
        String monthYearText = DateUtils.formatMonthYear(selectedMonth, selectedYear);
        binding.tvMonthYear.setText(monthYearText);
    }

    private void setupFab() {
        binding.fabAddBudget.setOnClickListener(v ->
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_budgetList_to_addEditBudget));
    }

    private void observeData() {
        viewModel.getBudgets().observe(getViewLifecycleOwner(), budgets -> {
            binding.progressLoading.setVisibility(View.GONE);
            if (budgets != null && !budgets.isEmpty()) {
                adapter.submitList(budgets);
                binding.rvBudgets.setVisibility(View.VISIBLE);
                binding.layoutEmptyState.setVisibility(View.GONE);
            } else {
                adapter.submitList(null);
                binding.rvBudgets.setVisibility(View.GONE);
                binding.layoutEmptyState.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getSpentAmounts().observe(getViewLifecycleOwner(), spentAmounts -> {
            if (spentAmounts != null) {
                adapter.setSpentAmounts(spentAmounts);
            }
        });
    }

    private void loadBudgets() {
        viewModel.loadBudgets(selectedMonth, selectedYear);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
