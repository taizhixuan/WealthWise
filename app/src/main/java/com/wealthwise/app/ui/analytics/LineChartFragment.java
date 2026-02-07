package com.wealthwise.app.ui.analytics;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.Utils;
import com.wealthwise.app.R;
import com.wealthwise.app.data.local.dao.TransactionDao;
import com.wealthwise.app.databinding.FragmentLineChartBinding;

import java.util.ArrayList;
import java.util.List;

public class LineChartFragment extends Fragment {
    private FragmentLineChartBinding binding;
    private AnalyticsViewModel viewModel;

    private static final int PERIOD_1M = 1;
    private static final int PERIOD_3M = 3;
    private static final int PERIOD_6M = 6;
    private static final int PERIOD_1Y = 12;

    private static final int COLOR_POSITIVE = Color.parseColor("#4CAF50");
    private static final int COLOR_NEGATIVE = Color.parseColor("#F44336");

    private static final String[] MONTH_NAMES = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLineChartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireParentFragment()).get(AnalyticsViewModel.class);
        setupChart();
        setupPeriodChips();
        observeData();
        viewModel.loadBalanceTrend(PERIOD_3M);
    }

    private void setupChart() {
        binding.lineChart.getDescription().setEnabled(false);
        binding.lineChart.setDrawGridBackground(false);
        binding.lineChart.setTouchEnabled(true);
        binding.lineChart.setDragEnabled(true);
        binding.lineChart.setScaleEnabled(false);
        binding.lineChart.setPinchZoom(false);
        binding.lineChart.setExtraBottomOffset(10f);
        binding.lineChart.setExtraLeftOffset(10f);

        XAxis xAxis = binding.lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(10f);
        xAxis.setLabelRotationAngle(-45f);

        YAxis leftAxis = binding.lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#E0E0E0"));
        leftAxis.setTextSize(10f);

        binding.lineChart.getAxisRight().setEnabled(false);

        Legend legend = binding.lineChart.getLegend();
        legend.setEnabled(false);
    }

    private void setupPeriodChips() {
        binding.chip1m.setOnClickListener(v -> {
            setActiveChip(PERIOD_1M);
            viewModel.loadBalanceTrend(PERIOD_1M);
        });

        binding.chip3m.setOnClickListener(v -> {
            setActiveChip(PERIOD_3M);
            viewModel.loadBalanceTrend(PERIOD_3M);
        });

        binding.chip6m.setOnClickListener(v -> {
            setActiveChip(PERIOD_6M);
            viewModel.loadBalanceTrend(PERIOD_6M);
        });

        binding.chip1y.setOnClickListener(v -> {
            setActiveChip(PERIOD_1Y);
            viewModel.loadBalanceTrend(PERIOD_1Y);
        });

        binding.chip3m.setChecked(true);
    }

    private void setActiveChip(int period) {
        binding.chip1m.setChecked(period == PERIOD_1M);
        binding.chip3m.setChecked(period == PERIOD_3M);
        binding.chip6m.setChecked(period == PERIOD_6M);
        binding.chip1y.setChecked(period == PERIOD_1Y);
    }

    private void observeData() {
        viewModel.getMonthlySnapshots().observe(getViewLifecycleOwner(), snapshots -> {
            if (snapshots != null && !snapshots.isEmpty()) {
                binding.lineChart.setVisibility(View.VISIBLE);
                binding.layoutEmptyState.setVisibility(View.GONE);

                List<Entry> entries = new ArrayList<>();
                List<String> dateLabels = new ArrayList<>();

                // Compute running net balance (income - expense) per month
                double runningBalance = 0;
                for (int i = 0; i < snapshots.size(); i++) {
                    TransactionDao.MonthlySnapshotResult snapshot = snapshots.get(i);
                    runningBalance += (snapshot.totalIncome - snapshot.totalExpense);
                    entries.add(new Entry(i, (float) runningBalance));

                    String label = (snapshot.month >= 1 && snapshot.month <= 12)
                            ? MONTH_NAMES[snapshot.month - 1] : "?";
                    dateLabels.add(label);
                }

                boolean isPositiveTrend = entries.get(entries.size() - 1).getY()
                        >= entries.get(0).getY();
                int lineColor = isPositiveTrend ? COLOR_POSITIVE : COLOR_NEGATIVE;

                LineDataSet dataSet = new LineDataSet(entries, "Balance");
                dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                dataSet.setCubicIntensity(0.2f);
                dataSet.setDrawCircles(false);
                dataSet.setLineWidth(2.5f);
                dataSet.setColor(lineColor);
                dataSet.setHighLightColor(Color.parseColor("#BDBDBD"));
                dataSet.setDrawHorizontalHighlightIndicator(false);
                dataSet.setDrawValues(false);
                dataSet.setDrawFilled(true);

                if (Utils.getSDKInt() >= 18) {
                    Drawable fillDrawable = ContextCompat.getDrawable(requireContext(),
                            isPositiveTrend ? R.drawable.gradient_fill_green : R.drawable.gradient_fill_red);
                    if (fillDrawable != null) {
                        dataSet.setFillDrawable(fillDrawable);
                    } else {
                        dataSet.setFillColor(lineColor);
                        dataSet.setFillAlpha(50);
                    }
                } else {
                    dataSet.setFillColor(lineColor);
                    dataSet.setFillAlpha(50);
                }

                XAxis xAxis = binding.lineChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(dateLabels));
                xAxis.setLabelCount(Math.min(dateLabels.size(), 6), true);

                LineData lineData = new LineData(dataSet);
                binding.lineChart.setData(lineData);
                binding.lineChart.animateX(1200, Easing.EaseInOutQuad);
                binding.lineChart.invalidate();
            } else {
                binding.lineChart.setVisibility(View.GONE);
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
