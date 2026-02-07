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
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.wealthwise.app.data.local.dao.TransactionDao;
import com.wealthwise.app.databinding.FragmentPieChartBinding;
import com.wealthwise.app.util.ChartColorPalette;
import com.wealthwise.app.util.CurrencyFormatter;
import com.wealthwise.app.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class PieChartFragment extends Fragment {
    private FragmentPieChartBinding binding;
    private AnalyticsViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPieChartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireParentFragment()).get(AnalyticsViewModel.class);
        setupChart();
        observeData();
        viewModel.loadCategorySummary(DateUtils.getCurrentMonth(), DateUtils.getCurrentYear());
    }

    private void setupChart() {
        binding.pieChart.setUsePercentValues(true);
        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.setExtraOffsets(5, 10, 5, 5);
        binding.pieChart.setDragDecelerationFrictionCoef(0.95f);
        binding.pieChart.setCenterText("Spending\nBreakdown");
        binding.pieChart.setCenterTextSize(14f);
        binding.pieChart.setDrawHoleEnabled(true);
        binding.pieChart.setHoleColor(Color.TRANSPARENT);
        binding.pieChart.setTransparentCircleColor(Color.WHITE);
        binding.pieChart.setTransparentCircleAlpha(110);
        binding.pieChart.setHoleRadius(55f);
        binding.pieChart.setTransparentCircleRadius(58f);
        binding.pieChart.setDrawCenterText(true);
        binding.pieChart.setRotationAngle(0);
        binding.pieChart.setRotationEnabled(true);
        binding.pieChart.setHighlightPerTapEnabled(true);
        binding.pieChart.animateY(1400, Easing.EaseInOutQuad);

        Legend legend = binding.pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setWordWrapEnabled(true);
    }

    private void observeData() {
        viewModel.getCategorySummaries().observe(getViewLifecycleOwner(), summaries -> {
            if (summaries != null && !summaries.isEmpty()) {
                binding.pieChart.setVisibility(View.VISIBLE);
                binding.rvLegend.setVisibility(View.VISIBLE);
                binding.layoutEmptyState.setVisibility(View.GONE);

                List<PieEntry> entries = new ArrayList<>();
                List<Integer> colors = new ArrayList<>();

                for (int i = 0; i < summaries.size(); i++) {
                    TransactionDao.CategorySummaryResult summary = summaries.get(i);
                    entries.add(new PieEntry((float) summary.totalAmount, summary.categoryName));
                    if (summary.categoryColorHex != null) {
                        try {
                            colors.add(Color.parseColor(summary.categoryColorHex));
                        } catch (Exception e) {
                            colors.add(ChartColorPalette.getColor(i));
                        }
                    } else {
                        colors.add(ChartColorPalette.getColor(i));
                    }
                }

                PieDataSet dataSet = new PieDataSet(entries, "");
                dataSet.setSliceSpace(3f);
                dataSet.setSelectionShift(5f);
                dataSet.setColors(colors);
                dataSet.setValueLinePart1OffsetPercentage(80f);
                dataSet.setValueLinePart1Length(0.3f);
                dataSet.setValueLinePart2Length(0.4f);
                dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

                PieData data = new PieData(dataSet);
                data.setValueFormatter(new PercentFormatter(binding.pieChart));
                data.setValueTextSize(11f);
                data.setValueTextColor(Color.DKGRAY);
                binding.pieChart.setData(data);
                binding.pieChart.invalidate();
            } else {
                binding.pieChart.setVisibility(View.GONE);
                binding.rvLegend.setVisibility(View.GONE);
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
