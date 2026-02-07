package com.wealthwise.app.ui.transaction;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.wealthwise.app.R;
import com.wealthwise.app.data.local.entity.TransactionEntity;
import com.wealthwise.app.data.model.TransactionType;
import com.wealthwise.app.data.local.relation.TransactionWithCategory;
import com.wealthwise.app.databinding.FragmentTransactionListBinding;

import java.util.ArrayList;
import java.util.List;

public class TransactionListFragment extends Fragment {

    private FragmentTransactionListBinding binding;
    private TransactionViewModel viewModel;
    private TransactionAdapter adapter;
    private List<TransactionWithCategory> allTransactions = new ArrayList<>();
    private TransactionType currentFilter = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTransactionListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        setupRecyclerView();
        setupSearchView();
        setupChipFilters();
        setupFab();
        setupSwipeToDelete();
        observeData();
    }

    private void setupRecyclerView() {
        adapter = new TransactionAdapter(transaction -> {
            Bundle args = new Bundle();
            args.putLong("transactionId", transaction.transaction.getId());
            Navigation.findNavController(requireView())
                    .navigate(R.id.addEditTransactionFragment, args);
        });
        binding.rvTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTransactions.setAdapter(adapter);
    }

    private void setupSearchView() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBySearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterBySearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            applyFilter();
            return;
        }
        String lowerQuery = query.toLowerCase().trim();
        List<TransactionWithCategory> filtered = new ArrayList<>();
        for (TransactionWithCategory t : allTransactions) {
            boolean matches = false;
            if (t.transaction.getNote() != null && t.transaction.getNote().toLowerCase().contains(lowerQuery)) {
                matches = true;
            }
            if (t.transaction.getPayee() != null && t.transaction.getPayee().toLowerCase().contains(lowerQuery)) {
                matches = true;
            }
            if (t.category != null && t.category.getName() != null && t.category.getName().toLowerCase().contains(lowerQuery)) {
                matches = true;
            }
            if (matches && (currentFilter == null || t.transaction.getType() == currentFilter)) {
                filtered.add(t);
            }
        }
        adapter.submitList(filtered);
        updateEmptyState();
    }

    private void setupChipFilters() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentFilter = null;
                applyFilter();
                return;
            }

            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chip_all) {
                currentFilter = null;
            } else if (checkedId == R.id.chip_income) {
                currentFilter = TransactionType.INCOME;
            } else if (checkedId == R.id.chip_expense) {
                currentFilter = TransactionType.EXPENSE;
            }
            applyFilter();
        });
    }

    private void applyFilter() {
        if (currentFilter == null) {
            adapter.submitList(new ArrayList<>(allTransactions));
        } else {
            List<TransactionWithCategory> filtered = new ArrayList<>();
            for (TransactionWithCategory t : allTransactions) {
                if (t.transaction.getType() == currentFilter) {
                    filtered.add(t);
                }
            }
            adapter.submitList(filtered);
        }
        updateEmptyState();
    }

    private void setupFab() {
        binding.fabAddTransaction.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.addEditTransactionFragment));
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            private final ColorDrawable background = new ColorDrawable(
                    ContextCompat.getColor(requireContext(), R.color.error));
            private final Drawable deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete);

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                TransactionWithCategory swipedTransaction = adapter.getTransactionAt(position);

                if (swipedTransaction != null) {
                    TransactionEntity deletedTransaction = swipedTransaction.transaction;
                    long deletedId = deletedTransaction.getId();
                    viewModel.softDelete(deletedId);

                    Snackbar.make(binding.getRoot(), R.string.transaction_deleted, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo, v -> viewModel.insert(deletedTransaction))
                            .show();
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                if (dX > 0) {
                    background.setBounds(itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + (int) dX, itemView.getBottom());
                } else if (dX < 0) {
                    background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(),
                            itemView.getRight(), itemView.getBottom());
                } else {
                    background.setBounds(0, 0, 0, 0);
                }
                background.draw(c);

                if (deleteIcon != null) {
                    int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                    int iconTop = itemView.getTop() + iconMargin;
                    int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

                    if (dX > 0) {
                        int iconLeft = itemView.getLeft() + iconMargin;
                        int iconRight = iconLeft + deleteIcon.getIntrinsicWidth();
                        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    } else if (dX < 0) {
                        int iconRight = itemView.getRight() - iconMargin;
                        int iconLeft = iconRight - deleteIcon.getIntrinsicWidth();
                        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    }
                    deleteIcon.draw(c);
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.rvTransactions);
    }

    private void observeData() {
        viewModel.getAllTransactions().observe(getViewLifecycleOwner(), transactions -> {
            binding.progressLoading.setVisibility(View.GONE);
            allTransactions = transactions != null ? transactions : new ArrayList<>();
            applyFilter();
        });
    }

    private void updateEmptyState() {
        List<TransactionWithCategory> currentList = adapter.getCurrentList();
        if (currentList == null || currentList.isEmpty()) {
            binding.rvTransactions.setVisibility(View.GONE);
            binding.emptyState.setVisibility(View.VISIBLE);
        } else {
            binding.rvTransactions.setVisibility(View.VISIBLE);
            binding.emptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
