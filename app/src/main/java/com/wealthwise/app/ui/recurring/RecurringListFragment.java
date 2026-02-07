package com.wealthwise.app.ui.recurring;

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
import com.wealthwise.app.databinding.FragmentRecurringListBinding;

public class RecurringListFragment extends Fragment {
    private FragmentRecurringListBinding binding;
    private RecurringViewModel viewModel;
    private RecurringAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecurringListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RecurringViewModel.class);

        setupRecyclerView();
        setupFab();
        observeData();
    }

    private void setupRecyclerView() {
        adapter = new RecurringAdapter(recurring -> {
            Bundle args = new Bundle();
            args.putLong("recurringId", recurring.getId());
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_recurringList_to_addEditRecurring, args);
        });

        binding.rvRecurring.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRecurring.setAdapter(adapter);
        binding.rvRecurring.setHasFixedSize(true);
    }

    private void setupFab() {
        binding.fabAddRecurring.setOnClickListener(v ->
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_recurringList_to_addEditRecurring));
    }

    private void observeData() {
        viewModel.getAllRecurring().observe(getViewLifecycleOwner(), recurringList -> {
            binding.progressLoading.setVisibility(View.GONE);
            if (recurringList != null && !recurringList.isEmpty()) {
                adapter.submitList(recurringList);
                binding.rvRecurring.setVisibility(View.VISIBLE);
                binding.layoutEmptyState.setVisibility(View.GONE);
            } else {
                adapter.submitList(null);
                binding.rvRecurring.setVisibility(View.GONE);
                binding.layoutEmptyState.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
