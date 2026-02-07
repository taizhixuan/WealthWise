package com.wealthwise.app.ui.recommendation;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.wealthwise.app.R;
import com.wealthwise.app.databinding.ItemRecommendationBinding;
import com.wealthwise.app.engine.recommendation.Recommendation;
import com.wealthwise.app.engine.recommendation.RecommendationPriority;

public class RecommendationAdapter extends ListAdapter<Recommendation, RecommendationAdapter.ViewHolder> {

    public interface OnRecommendationActionListener {
        void onDismiss(Recommendation recommendation);
        void onAction(Recommendation recommendation);
    }

    private final OnRecommendationActionListener listener;

    public RecommendationAdapter(OnRecommendationActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Recommendation> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Recommendation>() {
                @Override
                public boolean areItemsTheSame(@NonNull Recommendation oldItem,
                                               @NonNull Recommendation newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Recommendation oldItem,
                                                  @NonNull Recommendation newItem) {
                    return oldItem.getTitle().equals(newItem.getTitle());
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecommendationBinding binding = ItemRecommendationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemRecommendationBinding binding;

        ViewHolder(ItemRecommendationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Recommendation item) {
            binding.tvTitle.setText(item.getTitle());
            binding.tvDescription.setText(item.getDescription());

            // Priority chip
            RecommendationPriority priority = item.getPriority();
            binding.chipPriority.setText(priority.name());
            switch (priority) {
                case HIGH:
                    binding.chipPriority.setChipBackgroundColorResource(R.color.expense_red);
                    binding.chipPriority.setTextColor(Color.WHITE);
                    break;
                case MEDIUM:
                    binding.chipPriority.setChipBackgroundColor(
                            android.content.res.ColorStateList.valueOf(Color.parseColor("#F57F17")));
                    binding.chipPriority.setTextColor(Color.WHITE);
                    break;
                case LOW:
                    binding.chipPriority.setChipBackgroundColorResource(R.color.income_green);
                    binding.chipPriority.setTextColor(Color.WHITE);
                    break;
            }

            // Action button
            if (item.getActionText() != null && !item.getActionText().isEmpty()) {
                binding.btnAction.setText(item.getActionText());
                binding.btnAction.setVisibility(View.VISIBLE);
            } else {
                binding.btnAction.setVisibility(View.GONE);
            }

            binding.btnDismiss.setOnClickListener(v -> {
                if (listener != null) listener.onDismiss(item);
            });

            binding.btnAction.setOnClickListener(v -> {
                if (listener != null) listener.onAction(item);
            });
        }
    }
}
