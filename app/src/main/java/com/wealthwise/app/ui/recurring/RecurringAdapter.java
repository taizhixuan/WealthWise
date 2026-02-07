package com.wealthwise.app.ui.recurring;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.wealthwise.app.R;
import com.wealthwise.app.data.local.entity.RecurringTransactionEntity;
import com.wealthwise.app.databinding.ItemRecurringBinding;
import com.wealthwise.app.util.ChartColorPalette;
import com.wealthwise.app.util.CurrencyFormatter;
import com.wealthwise.app.util.DateUtils;

public class RecurringAdapter extends ListAdapter<RecurringTransactionEntity, RecurringAdapter.ViewHolder> {

    public interface OnRecurringClickListener {
        void onClick(RecurringTransactionEntity item);
    }

    private final OnRecurringClickListener listener;

    public RecurringAdapter(OnRecurringClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<RecurringTransactionEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<RecurringTransactionEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull RecurringTransactionEntity oldItem,
                                               @NonNull RecurringTransactionEntity newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull RecurringTransactionEntity oldItem,
                                                  @NonNull RecurringTransactionEntity newItem) {
                    return oldItem.getAmount() == newItem.getAmount()
                            && oldItem.isActive() == newItem.isActive()
                            && oldItem.getUpdatedAt().equals(newItem.getUpdatedAt());
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecurringBinding binding = ItemRecurringBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemRecurringBinding binding;

        ViewHolder(ItemRecurringBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(RecurringTransactionEntity item) {
            // Title (payee or note)
            String title = item.getPayee();
            if (title == null || title.isEmpty()) {
                title = item.getNote();
            }
            if (title == null || title.isEmpty()) {
                title = item.getType() != null ? item.getType().name() : "Recurring";
            }
            binding.tvTitle.setText(title);

            // Interval
            binding.tvInterval.setText(item.getInterval() != null ?
                    capitalizeFirst(item.getInterval().name()) : "");

            // Next occurrence
            if (item.getNextOccurrence() != null) {
                binding.tvNextOccurrence.setText("Next: " + DateUtils.formatDate(item.getNextOccurrence()));
            } else {
                binding.tvNextOccurrence.setText("");
            }

            // Amount
            int color = ChartColorPalette.getColorForType(item.getType());
            binding.tvAmount.setText(CurrencyFormatter.formatSigned(item.getAmount(), item.getType()));
            binding.tvAmount.setTextColor(color);

            // Active/Inactive chip
            if (item.isActive()) {
                binding.chipStatus.setText(R.string.active);
                binding.chipStatus.setChipBackgroundColorResource(R.color.income_green);
                binding.chipStatus.setTextColor(Color.WHITE);
            } else {
                binding.chipStatus.setText(R.string.inactive);
                binding.chipStatus.setChipBackgroundColor(
                        android.content.res.ColorStateList.valueOf(Color.GRAY));
                binding.chipStatus.setTextColor(Color.WHITE);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onClick(item);
            });
        }

        private String capitalizeFirst(String s) {
            if (s == null || s.isEmpty()) return s;
            return s.charAt(0) + s.substring(1).toLowerCase();
        }
    }
}
