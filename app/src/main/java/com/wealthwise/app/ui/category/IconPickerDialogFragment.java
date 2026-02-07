package com.wealthwise.app.ui.category;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.wealthwise.app.R;
import com.wealthwise.app.ui.transaction.TransactionAdapter;

public class IconPickerDialogFragment extends DialogFragment {

    public interface OnIconSelectedListener {
        void onIconSelected(String iconName);
    }

    private OnIconSelectedListener listener;

    private static final String[] ICON_NAMES = {
            "ic_restaurant", "ic_directions_car", "ic_shopping_cart", "ic_receipt",
            "ic_movie", "ic_fitness_center", "ic_school", "ic_spa",
            "ic_home", "ic_work", "ic_laptop", "ic_trending_up",
            "ic_card_giftcard", "ic_attach_money"
    };

    public void setOnIconSelectedListener(OnIconSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = new RecyclerView(requireContext());
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 5));
        recyclerView.setPadding(32, 32, 32, 32);
        recyclerView.setAdapter(new IconAdapter());

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select Icon")
                .setView(recyclerView)
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    private class IconAdapter extends RecyclerView.Adapter<IconAdapter.VH> {
        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageView iv = new ImageView(parent.getContext());
            int size = (int) (48 * parent.getContext().getResources().getDisplayMetrics().density);
            iv.setLayoutParams(new ViewGroup.LayoutParams(size, size));
            iv.setPadding(16, 16, 16, 16);
            return new VH(iv);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            String iconName = ICON_NAMES[position];
            int resId = TransactionAdapter.getIconResource(iconName);
            holder.imageView.setImageResource(resId);
            holder.imageView.setColorFilter(Color.GRAY);

            holder.imageView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onIconSelected(iconName);
                }
                dismiss();
            });
        }

        @Override
        public int getItemCount() {
            return ICON_NAMES.length;
        }

        class VH extends RecyclerView.ViewHolder {
            ImageView imageView;
            VH(ImageView iv) {
                super(iv);
                this.imageView = iv;
            }
        }
    }
}
