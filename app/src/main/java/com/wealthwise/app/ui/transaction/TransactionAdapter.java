package com.wealthwise.app.ui.transaction;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.wealthwise.app.R;
import com.wealthwise.app.data.local.entity.CategoryEntity;
import com.wealthwise.app.data.local.relation.TransactionWithCategory;
import com.wealthwise.app.databinding.ItemTransactionBinding;
import com.wealthwise.app.util.ChartColorPalette;
import com.wealthwise.app.util.CurrencyFormatter;
import com.wealthwise.app.util.DateUtils;

public class TransactionAdapter extends ListAdapter<TransactionWithCategory, TransactionAdapter.ViewHolder> {

    public interface OnTransactionClickListener {
        void onClick(TransactionWithCategory transaction);
    }

    private final OnTransactionClickListener listener;

    public TransactionAdapter(OnTransactionClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<TransactionWithCategory> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<TransactionWithCategory>() {
                @Override
                public boolean areItemsTheSame(@NonNull TransactionWithCategory oldItem,
                                               @NonNull TransactionWithCategory newItem) {
                    return oldItem.transaction.getId() == newItem.transaction.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull TransactionWithCategory oldItem,
                                                  @NonNull TransactionWithCategory newItem) {
                    return oldItem.transaction.getAmount() == newItem.transaction.getAmount()
                            && oldItem.transaction.getType() == newItem.transaction.getType()
                            && oldItem.transaction.getUpdatedAt().equals(newItem.transaction.getUpdatedAt());
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransactionBinding binding = ItemTransactionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public TransactionWithCategory getTransactionAt(int position) {
        if (position >= 0 && position < getCurrentList().size()) {
            return getItem(position);
        }
        return null;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemTransactionBinding binding;

        ViewHolder(ItemTransactionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(TransactionWithCategory item) {
            CategoryEntity category = item.category;

            // Category name and icon
            if (category != null) {
                binding.tvCategoryName.setText(category.getName());
                setCategoryIcon(category);
            } else {
                binding.tvCategoryName.setText("Uncategorized");
            }

            // Payee / Note
            String subtitle = item.transaction.getPayee();
            if (subtitle == null || subtitle.isEmpty()) {
                subtitle = item.transaction.getNote();
            }
            binding.tvPayeeNote.setText(subtitle != null ? subtitle : "");

            // Amount with color
            int color = ChartColorPalette.getColorForType(item.transaction.getType());
            binding.tvAmount.setText(CurrencyFormatter.formatSigned(
                    item.transaction.getAmount(), item.transaction.getType()));
            binding.tvAmount.setTextColor(color);

            // Date
            if (item.transaction.getDate() != null) {
                binding.tvDate.setText(DateUtils.formatDate(item.transaction.getDate()));
            }

            // Click listener
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClick(item);
                }
            });
        }

        private void setCategoryIcon(CategoryEntity category) {
            int iconResId = getIconResource(category.getIconName());
            binding.ivCategoryIcon.setImageResource(iconResId);

            // Set circular background with category color
            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            try {
                circle.setColor(Color.parseColor(category.getColorHex()));
            } catch (Exception e) {
                circle.setColor(Color.GRAY);
            }
            binding.ivCategoryIcon.setBackground(circle);
        }
    }

    public static int getIconResource(String iconName) {
        if (iconName == null) return R.drawable.ic_category;
        switch (iconName) {
            case "ic_restaurant": return R.drawable.ic_restaurant;
            case "ic_directions_car": return R.drawable.ic_directions_car;
            case "ic_shopping_cart": return R.drawable.ic_shopping_cart;
            case "ic_receipt": return R.drawable.ic_receipt;
            case "ic_movie": return R.drawable.ic_movie;
            case "ic_fitness_center": return R.drawable.ic_fitness_center;
            case "ic_school": return R.drawable.ic_school;
            case "ic_spa": return R.drawable.ic_spa;
            case "ic_home": return R.drawable.ic_home;
            case "ic_work": return R.drawable.ic_work;
            case "ic_laptop": return R.drawable.ic_laptop;
            case "ic_trending_up": return R.drawable.ic_trending_up;
            case "ic_card_giftcard": return R.drawable.ic_card_giftcard;
            case "ic_attach_money": return R.drawable.ic_attach_money;
            default: return R.drawable.ic_category;
        }
    }
}
