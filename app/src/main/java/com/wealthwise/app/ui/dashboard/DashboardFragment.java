package com.wealthwise.app.ui.dashboard;

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
import com.wealthwise.app.databinding.FragmentDashboardBinding;
import com.wealthwise.app.util.CurrencyFormatter;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;
    private RecentTransactionAdapter recentAdapter;
    private BudgetProgressAdapter budgetAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        setupRecyclerViews();
        setupClickListeners();
        observeData();
    }

    private void setupRecyclerViews() {
        recentAdapter = new RecentTransactionAdapter(transaction -> {
            Bundle args = new Bundle();
            args.putLong("transactionId", transaction.transaction.getId());
            Navigation.findNavController(requireView())
                    .navigate(R.id.addEditTransactionFragment, args);
        });
        binding.rvRecentTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRecentTransactions.setAdapter(recentAdapter);
        binding.rvRecentTransactions.setNestedScrollingEnabled(false);

        budgetAdapter = new BudgetProgressAdapter();
        binding.rvBudgetProgress.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvBudgetProgress.setAdapter(budgetAdapter);
    }

    private void setupClickListeners() {
        binding.tvViewAllTransactions.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.transactionListFragment));
        binding.tvViewAllBudgets.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.budgetListFragment));
    }

    private void observeData() {
        viewModel.getTotalBalance().observe(getViewLifecycleOwner(), balance -> {
            binding.progressLoading.setVisibility(View.GONE);
            if (balance != null) {
                binding.tvTotalBalance.setText(CurrencyFormatter.format(balance));
            }
        });

        viewModel.getMonthlyIncome().observe(getViewLifecycleOwner(), income ->
                binding.tvMonthlyIncome.setText(CurrencyFormatter.format(income != null ? income : 0)));

        viewModel.getMonthlyExpense().observe(getViewLifecycleOwner(), expense ->
                binding.tvMonthlyExpense.setText(CurrencyFormatter.format(expense != null ? expense : 0)));

        viewModel.getRecentTransactions().observe(getViewLifecycleOwner(), transactions -> {
            if (transactions != null && !transactions.isEmpty()) {
                recentAdapter.submitList(transactions);
                binding.rvRecentTransactions.setVisibility(View.VISIBLE);
                binding.tvNoTransactions.setVisibility(View.GONE);
            } else {
                binding.rvRecentTransactions.setVisibility(View.GONE);
                binding.tvNoTransactions.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getCurrentBudgets().observe(getViewLifecycleOwner(), budgets -> {
            if (budgets != null) {
                budgetAdapter.submitList(budgets);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.refresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
