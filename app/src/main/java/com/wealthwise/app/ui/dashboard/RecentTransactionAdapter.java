package com.wealthwise.app.ui.dashboard;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.wealthwise.app.data.local.entity.CategoryEntity;
import com.wealthwise.app.data.local.relation.TransactionWithCategory;
import com.wealthwise.app.databinding.ItemTransactionBinding;
import com.wealthwise.app.ui.transaction.TransactionAdapter;
import com.wealthwise.app.util.ChartColorPalette;
import com.wealthwise.app.util.CurrencyFormatter;
import com.wealthwise.app.util.DateUtils;

public class RecentTransactionAdapter extends ListAdapter<TransactionWithCategory, RecentTransactionAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onClick(TransactionWithCategory transaction);
    }

    private final OnItemClickListener listener;

    public RecentTransactionAdapter(OnItemClickListener listener) {
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

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemTransactionBinding binding;

        ViewHolder(ItemTransactionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(TransactionWithCategory item) {
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
            } else {
                binding.tvCategoryName.setText("Uncategorized");
            }

            String subtitle = item.transaction.getPayee();
            if (subtitle == null || subtitle.isEmpty()) {
                subtitle = item.transaction.getNote();
            }
            binding.tvPayeeNote.setText(subtitle != null ? subtitle : "");

            int color = ChartColorPalette.getColorForType(item.transaction.getType());
            binding.tvAmount.setText(CurrencyFormatter.formatSigned(
                    item.transaction.getAmount(), item.transaction.getType()));
            binding.tvAmount.setTextColor(color);

            if (item.transaction.getDate() != null) {
                binding.tvDate.setText(DateUtils.formatDate(item.transaction.getDate()));
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onClick(item);
            });
        }
    }
}
