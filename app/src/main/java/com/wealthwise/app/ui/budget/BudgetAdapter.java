package com.wealthwise.app.ui.budget;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.wealthwise.app.R;
import com.wealthwise.app.data.local.entity.CategoryEntity;
import com.wealthwise.app.data.local.relation.BudgetWithCategory;
import com.wealthwise.app.databinding.ItemBudgetBinding;
import com.wealthwise.app.ui.transaction.TransactionAdapter;
import com.wealthwise.app.util.CurrencyFormatter;

import java.util.Map;

public class BudgetAdapter extends ListAdapter<BudgetWithCategory, BudgetAdapter.ViewHolder> {

    public interface OnBudgetClickListener {
        void onClick(BudgetWithCategory budget);
    }

    private final OnBudgetClickListener listener;
    private Map<Long, Double> spentAmounts;

    public BudgetAdapter(OnBudgetClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    public void setSpentAmounts(Map<Long, Double> spentAmounts) {
        this.spentAmounts = spentAmounts;
        notifyDataSetChanged();
    }

    private static final DiffUtil.ItemCallback<BudgetWithCategory> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<BudgetWithCategory>() {
                @Override
                public boolean areItemsTheSame(@NonNull BudgetWithCategory oldItem,
                                               @NonNull BudgetWithCategory newItem) {
                    return oldItem.budget.getId() == newItem.budget.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull BudgetWithCategory oldItem,
                                                  @NonNull BudgetWithCategory newItem) {
                    return oldItem.budget.getLimitAmount() == newItem.budget.getLimitAmount()
                            && oldItem.budget.getUpdatedAt().equals(newItem.budget.getUpdatedAt());
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBudgetBinding binding = ItemBudgetBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemBudgetBinding binding;

        ViewHolder(ItemBudgetBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(BudgetWithCategory item) {
            CategoryEntity category = item.category;

            if (category != null) {
                binding.tvCategoryName.setText(category.getName());
                int iconRes = TransactionAdapter.getIconResource(category.getIconName());
                binding.ivCategoryIcon.setImageResource(iconRes);

                GradientDrawable circle = new GradientDrawable();
                circle.setShape(GradientDrawable.OVAL);
                try {
                    circle.setColor(Color.parseColor(category.getColorHex()));
                } catch (Exception e) {
                    circle.setColor(Color.GRAY);
                }
                binding.ivCategoryIcon.setBackground(circle);
            }

            double limit = item.budget.getLimitAmount();
            double spent = 0;
            if (spentAmounts != null && spentAmounts.containsKey(item.budget.getCategoryId())) {
                spent = spentAmounts.get(item.budget.getCategoryId());
            }

            int percentage = limit > 0 ? (int) ((spent / limit) * 100) : 0;
            double remaining = limit - spent;

            binding.progressBudget.setProgress(Math.min(percentage, 100));
            binding.tvPercentageBadge.setText(percentage + "%");
            binding.tvSpentLimit.setText(String.format("%s / %s",
                    CurrencyFormatter.format(spent), CurrencyFormatter.format(limit)));

            // Color coding
            int progressColor;
            if (percentage >= 100) {
                progressColor = ContextCompat.getColor(binding.getRoot().getContext(), R.color.expense_red);
                binding.tvRemaining.setText(String.format("%s over", CurrencyFormatter.format(Math.abs(remaining))));
            } else if (percentage >= 60) {
                progressColor = Color.parseColor("#F57F17");
                binding.tvRemaining.setText(String.format("%s left", CurrencyFormatter.format(remaining)));
            } else {
                progressColor = ContextCompat.getColor(binding.getRoot().getContext(), R.color.income_green);
                binding.tvRemaining.setText(String.format("%s left", CurrencyFormatter.format(remaining)));
            }
            binding.progressBudget.setIndicatorColor(progressColor);
            binding.tvPercentageBadge.setTextColor(progressColor);
            binding.tvRemaining.setTextColor(progressColor);

            // Badge background
            GradientDrawable badge = new GradientDrawable();
            badge.setCornerRadius(28);
            badge.setColor(progressColor);
            binding.tvPercentageBadge.setBackground(badge);
            binding.tvPercentageBadge.setTextColor(Color.WHITE);

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onClick(item);
            });
        }
    }
}
