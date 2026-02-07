package com.wealthwise.app.ui.recommendation;

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
import com.wealthwise.app.databinding.FragmentRecommendationBinding;
import com.wealthwise.app.engine.recommendation.Recommendation;

public class RecommendationFragment extends Fragment {
    private FragmentRecommendationBinding binding;
    private RecommendationViewModel viewModel;
    private RecommendationAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecommendationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RecommendationViewModel.class);

        setupRecyclerView();
        observeData();
    }

    private void setupRecyclerView() {
        adapter = new RecommendationAdapter(new RecommendationAdapter.OnRecommendationActionListener() {
            @Override
            public void onDismiss(Recommendation recommendation) {
                viewModel.dismiss(recommendation.getId());
            }

            @Override
            public void onAction(Recommendation recommendation) {
                navigateForRecommendation(recommendation);
            }
        });
        binding.rvRecommendations.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRecommendations.setAdapter(adapter);
        binding.rvRecommendations.setHasFixedSize(true);
    }

    private void navigateForRecommendation(Recommendation recommendation) {
        if (recommendation.getType() == null) return;

        switch (recommendation.getType()) {
            case BUDGET_OVERRUN:
            case NO_BUDGET_HIGH_SPEND:
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_recommendation_to_budgetList);
                break;
            case SPENDING_SPIKE:
            case UPWARD_TREND:
            case LOW_SAVINGS_RATE:
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_recommendation_to_analytics);
                break;
            case SUBSCRIPTION_CREEP:
            case LATTE_FACTOR:
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_recommendation_to_transactionList);
                break;
        }
    }

    private void observeData() {
        viewModel.getRecommendations().observe(getViewLifecycleOwner(), recommendations -> {
            if (recommendations != null && !recommendations.isEmpty()) {
                adapter.submitList(recommendations);
                binding.rvRecommendations.setVisibility(View.VISIBLE);
                binding.layoutEmptyState.setVisibility(View.GONE);
            } else {
                adapter.submitList(null);
                binding.rvRecommendations.setVisibility(View.GONE);
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
