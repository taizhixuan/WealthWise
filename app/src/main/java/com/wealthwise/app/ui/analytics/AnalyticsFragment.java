package com.wealthwise.app.ui.analytics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.wealthwise.app.databinding.FragmentAnalyticsBinding;

public class AnalyticsFragment extends Fragment {

    private FragmentAnalyticsBinding binding;

    private static final String[] TAB_TITLES = {"Pie Chart", "Bar Chart", "Line Chart"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAnalyticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewPager();
    }

    private void setupViewPager() {
        AnalyticsPagerAdapter pagerAdapter = new AnalyticsPagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> tab.setText(TAB_TITLES[position])
        ).attach();
    }

    private static class AnalyticsPagerAdapter extends FragmentStateAdapter {

        public AnalyticsPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new PieChartFragment();
                case 1:
                    return new BarChartFragment();
                case 2:
                    return new LineChartFragment();
                default:
                    return new PieChartFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
