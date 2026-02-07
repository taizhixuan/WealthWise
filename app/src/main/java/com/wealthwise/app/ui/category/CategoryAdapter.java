package com.wealthwise.app.ui.category;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.wealthwise.app.data.local.entity.CategoryEntity;
import com.wealthwise.app.databinding.ItemCategoryBinding;
import com.wealthwise.app.ui.transaction.TransactionAdapter;

public class CategoryAdapter extends ListAdapter<CategoryEntity, CategoryAdapter.ViewHolder> {

    public interface OnCategoryClickListener {
        void onEditClick(CategoryEntity category);
    }

    private final OnCategoryClickListener listener;

    public CategoryAdapter(OnCategoryClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<CategoryEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<CategoryEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull CategoryEntity oldItem,
                                               @NonNull CategoryEntity newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull CategoryEntity oldItem,
                                                  @NonNull CategoryEntity newItem) {
                    return oldItem.getName().equals(newItem.getName())
                            && String.valueOf(oldItem.getColorHex()).equals(String.valueOf(newItem.getColorHex()));
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryBinding binding;

        ViewHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CategoryEntity category) {
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

            binding.tvTransactionCount.setText(category.getType() != null ?
                    category.getType().name() : "");

            binding.btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEditClick(category);
            });
        }
    }
}
