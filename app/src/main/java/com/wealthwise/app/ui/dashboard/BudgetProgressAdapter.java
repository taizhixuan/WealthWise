package com.wealthwise.app.ui.dashboard;

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
import com.wealthwise.app.databinding.ItemBudgetProgressBinding;
import com.wealthwise.app.ui.transaction.TransactionAdapter;
import com.wealthwise.app.util.CurrencyFormatter;

public class BudgetProgressAdapter extends ListAdapter<BudgetWithCategory, BudgetProgressAdapter.ViewHolder> {

    public BudgetProgressAdapter() {
        super(DIFF_CALLBACK);
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
        ItemBudgetProgressBinding binding = ItemBudgetProgressBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemBudgetProgressBinding binding;

        ViewHolder(ItemBudgetProgressBinding binding) {
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
            // Spent amount is unknown here (dashboard shows budget-only data);
            // default to 0 - the DashboardFragment can provide actual spent via a map
            double spent = 0;
            int percentage = limit > 0 ? (int) ((spent / limit) * 100) : 0;

            binding.progressBudget.setProgress(Math.min(percentage, 100));
            binding.tvSpentOfBudget.setText(String.format("Spent %s of %s",
                    CurrencyFormatter.format(spent), CurrencyFormatter.format(limit)));
            binding.tvPercentage.setText(percentage + "%");

            // Color coding
            int progressColor;
            if (percentage >= 100) {
                progressColor = ContextCompat.getColor(binding.getRoot().getContext(), R.color.expense_red);
            } else if (percentage >= 60) {
                progressColor = Color.parseColor("#F57F17");
            } else {
                progressColor = ContextCompat.getColor(binding.getRoot().getContext(), R.color.income_green);
            }
            binding.progressBudget.setIndicatorColor(progressColor);
            binding.tvPercentage.setTextColor(progressColor);
        }
    }
}
