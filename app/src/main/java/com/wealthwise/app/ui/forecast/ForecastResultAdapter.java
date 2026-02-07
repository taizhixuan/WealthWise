package com.wealthwise.app.ui.forecast;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.wealthwise.app.R;
import com.wealthwise.app.databinding.ItemForecastCategoryBinding;
import com.wealthwise.app.engine.forecast.ForecastResult;
import com.wealthwise.app.util.CurrencyFormatter;

import java.util.Locale;

public class ForecastResultAdapter extends ListAdapter<ForecastResult, ForecastResultAdapter.ViewHolder> {

    public ForecastResultAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<ForecastResult> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ForecastResult>() {
                @Override
                public boolean areItemsTheSame(@NonNull ForecastResult oldItem,
                                               @NonNull ForecastResult newItem) {
                    return oldItem.getCategoryId() == newItem.getCategoryId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull ForecastResult oldItem,
                                                  @NonNull ForecastResult newItem) {
                    return oldItem.getProjectedAmount() == newItem.getProjectedAmount();
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemForecastCategoryBinding binding = ItemForecastCategoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemForecastCategoryBinding binding;

        ViewHolder(ItemForecastCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ForecastResult item) {
            binding.tvCategoryName.setText(item.getCategoryName() != null ?
                    item.getCategoryName() : "Overall");

            binding.tvMonthlyAverage.setText(String.format("Avg: %s/mo",
                    CurrencyFormatter.format(item.getCurrentAverage())));

            binding.tvProjectedAmount.setText(CurrencyFormatter.format(item.getProjectedAmount()));

            double change = item.getChangePercentage();
            if (change > 0) {
                binding.tvChangePercentage.setText(String.format(Locale.US, "+%.1f%%", change));
                binding.tvChangePercentage.setTextColor(Color.parseColor("#D32F2F"));
                binding.ivChangeArrow.setRotation(0);
            } else if (change < 0) {
                binding.tvChangePercentage.setText(String.format(Locale.US, "%.1f%%", change));
                binding.tvChangePercentage.setTextColor(Color.parseColor("#388E3C"));
                binding.ivChangeArrow.setRotation(180);
            } else {
                binding.tvChangePercentage.setText("0.0%");
                binding.tvChangePercentage.setTextColor(Color.GRAY);
                binding.layoutChangeIndicator.setVisibility(View.GONE);
            }
        }
    }
}
