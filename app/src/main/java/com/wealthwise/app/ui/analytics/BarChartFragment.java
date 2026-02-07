package com.wealthwise.app.ui.analytics;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.wealthwise.app.data.local.dao.TransactionDao;
import com.wealthwise.app.databinding.FragmentBarChartBinding;
import com.wealthwise.app.util.ChartColorPalette;
import com.wealthwise.app.util.CurrencyFormatter;

import java.util.ArrayList;
import java.util.List;

public class BarChartFragment extends Fragment {
    private FragmentBarChartBinding binding;
    private AnalyticsViewModel viewModel;

    private static final int COLOR_INCOME = Color.parseColor("#4CAF50");
    private static final int COLOR_EXPENSE = Color.parseColor("#F44336");

    private static final float GROUP_SPACE = 0.3f;
    private static final float BAR_SPACE = 0.05f;
    private static final float BAR_WIDTH = 0.3f;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBarChartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireParentFragment()).get(AnalyticsViewModel.class);
        setupChart();
        observeData();
        viewModel.loadMonthlySnapshots(6);
    }

    private void setupChart() {
        binding.barChart.getDescription().setEnabled(false);
        binding.barChart.setDrawGridBackground(false);
        binding.barChart.setDrawBarShadow(false);
        binding.barChart.setDrawValueAboveBar(true);
        binding.barChart.setPinchZoom(false);
        binding.barChart.setScaleEnabled(false);
        binding.barChart.setDoubleTapToZoomEnabled(false);
        binding.barChart.setExtraBottomOffset(10f);

        XAxis xAxis = binding.barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setTextSize(11f);

        YAxis leftAxis = binding.barChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setTextSize(10f);

        binding.barChart.getAxisRight().setEnabled(false);

        Legend legend = binding.barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(12f);
        legend.setFormSize(10f);
        legend.setXEntrySpace(15f);
    }

    private static final String[] MONTH_NAMES = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private void observeData() {
        viewModel.getMonthlySnapshots().observe(getViewLifecycleOwner(), snapshots -> {
            if (snapshots != null && !snapshots.isEmpty()) {
                binding.barChart.setVisibility(View.VISIBLE);
                binding.layoutEmptyState.setVisibility(View.GONE);

                List<BarEntry> incomeEntries = new ArrayList<>();
                List<BarEntry> expenseEntries = new ArrayList<>();
                List<String> monthLabels = new ArrayList<>();

                for (int i = 0; i < snapshots.size(); i++) {
                    TransactionDao.MonthlySnapshotResult snapshot = snapshots.get(i);
                    incomeEntries.add(new BarEntry(i, (float) snapshot.totalIncome));
                    expenseEntries.add(new BarEntry(i, (float) snapshot.totalExpense));
                    String label = (snapshot.month >= 1 && snapshot.month <= 12)
                            ? MONTH_NAMES[snapshot.month - 1] : "?";
                    monthLabels.add(label);
                }

                BarDataSet incomeDataSet = new BarDataSet(incomeEntries, "Income");
                incomeDataSet.setColor(COLOR_INCOME);
                incomeDataSet.setValueTextSize(9f);
                incomeDataSet.setValueFormatter(new LargeValueFormatter());

                BarDataSet expenseDataSet = new BarDataSet(expenseEntries, "Expense");
                expenseDataSet.setColor(COLOR_EXPENSE);
                expenseDataSet.setValueTextSize(9f);
                expenseDataSet.setValueFormatter(new LargeValueFormatter());

                BarData barData = new BarData(incomeDataSet, expenseDataSet);
                barData.setBarWidth(BAR_WIDTH);

                binding.barChart.setData(barData);

                XAxis xAxis = binding.barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(monthLabels));
                xAxis.setAxisMinimum(0f);
                xAxis.setAxisMaximum(barData.getGroupWidth(GROUP_SPACE, BAR_SPACE) * snapshots.size());
                xAxis.setLabelCount(snapshots.size());

                binding.barChart.groupBars(0f, GROUP_SPACE, BAR_SPACE);
                binding.barChart.animateY(1000, Easing.EaseInOutQuad);
                binding.barChart.invalidate();
            } else {
                binding.barChart.setVisibility(View.GONE);
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
