package com.wealthwise.app.ui.category;

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

import com.google.android.material.tabs.TabLayout;
import com.wealthwise.app.R;
import com.wealthwise.app.databinding.FragmentCategoryListBinding;

public class CategoryListFragment extends Fragment {

    private FragmentCategoryListBinding binding;
    private CategoryViewModel viewModel;
    private CategoryAdapter adapter;
    private boolean showingExpense = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoryListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);

        setupRecyclerView();
        setupTabLayout();
        setupFab();
        observeData();
    }

    private void setupRecyclerView() {
        adapter = new CategoryAdapter(category -> {
            Bundle args = new Bundle();
            args.putLong("categoryId", category.getId());
            Navigation.findNavController(requireView())
                    .navigate(R.id.addEditCategoryFragment, args);
        });
        binding.rvCategories.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCategories.setAdapter(adapter);
    }

    private void setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showingExpense = tab.getPosition() == 0;
                observeData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupFab() {
        binding.fabAddCategory.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.addEditCategoryFragment));
    }

    private void observeData() {
        if (showingExpense) {
            viewModel.getExpenseCategories().observe(getViewLifecycleOwner(), categories -> {
                binding.progressLoading.setVisibility(View.GONE);
                if (categories != null) {
                    adapter.submitList(categories);
                }
            });
        } else {
            viewModel.getIncomeCategories().observe(getViewLifecycleOwner(), categories -> {
                binding.progressLoading.setVisibility(View.GONE);
                if (categories != null) {
                    adapter.submitList(categories);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
