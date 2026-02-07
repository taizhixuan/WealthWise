package com.wealthwise.app.ui.forecast;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.wealthwise.app.R;
import com.wealthwise.app.databinding.FragmentForecastBinding;
import com.wealthwise.app.engine.forecast.ForecastResult;
import com.wealthwise.app.util.CurrencyFormatter;

import java.util.ArrayList;
import java.util.List;

public class ForecastFragment extends Fragment {
    private FragmentForecastBinding binding;
    private ForecastViewModel viewModel;
    private ForecastResultAdapter categoryForecastAdapter;

    private static final int FORECAST_30_DAYS = 30;
    private static final int FORECAST_60_DAYS = 60;
    private static final int FORECAST_90_DAYS = 90;

    private static final int COLOR_PROJECTED = Color.parseColor("#FF9800");

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentForecastBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ForecastViewModel.class);

        setupChart();
        setupToggleGroup();
        setupCategoryForecastList();
        observeData();
    }

    private void setupChart() {
        binding.forecastChart.getDescription().setEnabled(false);
        binding.forecastChart.setDrawGridBackground(false);
        binding.forecastChart.setTouchEnabled(true);
        binding.forecastChart.setDragEnabled(true);
        binding.forecastChart.setScaleEnabled(false);
        binding.forecastChart.setPinchZoom(false);
        binding.forecastChart.setExtraBottomOffset(10f);

        XAxis xAxis = binding.forecastChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(10f);

        YAxis leftAxis = binding.forecastChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#E0E0E0"));
        leftAxis.setTextSize(10f);

        binding.forecastChart.getAxisRight().setEnabled(false);

        Legend legend = binding.forecastChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(11f);
    }

    private void setupToggleGroup() {
        binding.togglePeriod.check(R.id.btn_30_days);
        binding.togglePeriod.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_30_days) {
                    viewModel.loadForecast(FORECAST_30_DAYS);
                } else if (checkedId == R.id.btn_60_days) {
                    viewModel.loadForecast(FORECAST_60_DAYS);
                } else if (checkedId == R.id.btn_90_days) {
                    viewModel.loadForecast(FORECAST_90_DAYS);
                }
            }
        });
    }

    private void setupCategoryForecastList() {
        categoryForecastAdapter = new ForecastResultAdapter();
        binding.rvCategoryForecasts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCategoryForecasts.setAdapter(categoryForecastAdapter);
        binding.rvCategoryForecasts.setNestedScrollingEnabled(false);
    }

    private void observeData() {
        viewModel.getForecastResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                updateProjectedBalance(result.getProjectedAmount());
                updateChart(result);
            }
        });

        viewModel.getCategoryForecasts().observe(getViewLifecycleOwner(), forecasts -> {
            if (forecasts != null && !forecasts.isEmpty()) {
                categoryForecastAdapter.submitList(forecasts);
                binding.rvCategoryForecasts.setVisibility(View.VISIBLE);
            } else {
                binding.rvCategoryForecasts.setVisibility(View.GONE);
            }
        });
    }

    private void updateProjectedBalance(double projectedAmount) {
        binding.tvProjectedAmount.setText(CurrencyFormatter.format(projectedAmount));
    }

    private void updateChart(ForecastResult result) {
        List<Double> projections = result.getDailyProjections();
        if (projections == null || projections.isEmpty()) {
            binding.forecastChart.clear();
            binding.forecastChart.invalidate();
            return;
        }

        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < projections.size(); i++) {
            entries.add(new Entry(i, projections.get(i).floatValue()));
            labels.add("Day " + (i + 1));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Projected Spending");
        dataSet.setColor(COLOR_PROJECTED);
        dataSet.setLineWidth(2.5f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.15f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(COLOR_PROJECTED);
        dataSet.setFillAlpha(40);

        XAxis xAxis = binding.forecastChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelCount(Math.min(labels.size(), 6), true);

        LineData lineData = new LineData(dataSet);
        binding.forecastChart.setData(lineData);
        binding.forecastChart.animateX(800);
        binding.forecastChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
