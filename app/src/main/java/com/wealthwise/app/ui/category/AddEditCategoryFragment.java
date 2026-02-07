package com.wealthwise.app.ui.category;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wealthwise.app.R;
import com.wealthwise.app.data.local.entity.CategoryEntity;
import com.wealthwise.app.data.model.TransactionType;
import com.wealthwise.app.databinding.FragmentAddEditCategoryBinding;
import com.wealthwise.app.ui.transaction.TransactionAdapter;

public class AddEditCategoryFragment extends Fragment {

    private FragmentAddEditCategoryBinding binding;
    private CategoryViewModel viewModel;
    private long categoryId = -1;
    private String selectedIconName = "ic_restaurant";
    private String selectedColorHex = "#E53935";

    private static final String[] ICON_NAMES = {
            "ic_restaurant", "ic_directions_car", "ic_shopping_cart", "ic_receipt",
            "ic_movie", "ic_fitness_center", "ic_school", "ic_spa",
            "ic_home", "ic_work", "ic_laptop", "ic_trending_up",
            "ic_card_giftcard", "ic_attach_money"
    };

    private static final String[] COLORS = {
            "#E53935", "#1E88E5", "#43A047", "#FB8C00",
            "#8E24AA", "#00ACC1", "#FDD835", "#6D4C41",
            "#3949AB", "#D81B60", "#546E7A", "#78909C"
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddEditCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);

        if (getArguments() != null) {
            categoryId = getArguments().getLong("categoryId", -1);
        }

        setupIconPicker();
        setupColorPicker();
        setupSaveButton();

        if (categoryId > 0) {
            loadCategory();
        }
    }

    private void loadCategory() {
        viewModel.getCategoryById(categoryId).observe(getViewLifecycleOwner(), category -> {
            if (category != null) {
                binding.etCategoryName.setText(category.getName());

                if (category.getType() == TransactionType.INCOME) {
                    binding.toggleType.check(R.id.btn_income);
                } else {
                    binding.toggleType.check(R.id.btn_expense);
                }

                if (category.getIconName() != null) {
                    selectedIconName = category.getIconName();
                }
                if (category.getColorHex() != null) {
                    selectedColorHex = category.getColorHex();
                }
            }
        });
    }

    private void setupIconPicker() {
        binding.rvIcons.setAdapter(new IconAdapter());
    }

    private void setupColorPicker() {
        binding.rvColors.setAdapter(new ColorAdapter());
    }

    private void setupSaveButton() {
        binding.btnSave.setOnClickListener(v -> {
            String name = binding.etCategoryName.getText() != null ?
                    binding.etCategoryName.getText().toString().trim() : "";

            if (TextUtils.isEmpty(name)) {
                binding.tilCategoryName.setError("Name is required");
                return;
            }
            binding.tilCategoryName.setError(null);

            TransactionType type = binding.toggleType.getCheckedButtonId() == R.id.btn_income
                    ? TransactionType.INCOME : TransactionType.EXPENSE;

            if (categoryId > 0) {
                viewModel.getCategoryById(categoryId).observe(getViewLifecycleOwner(), existing -> {
                    if (existing != null) {
                        existing.setName(name);
                        existing.setType(type);
                        existing.setIconName(selectedIconName);
                        existing.setColorHex(selectedColorHex);
                        viewModel.update(existing);
                        Navigation.findNavController(requireView()).popBackStack();
                    }
                });
            } else {
                CategoryEntity newCategory = new CategoryEntity();
                newCategory.setName(name);
                newCategory.setType(type);
                newCategory.setIconName(selectedIconName);
                newCategory.setColorHex(selectedColorHex);
                viewModel.insert(newCategory);
                Navigation.findNavController(requireView()).popBackStack();
            }
        });
    }

    // Inner adapter for icon grid
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

            if (iconName.equals(selectedIconName)) {
                GradientDrawable bg = new GradientDrawable();
                bg.setShape(GradientDrawable.OVAL);
                try {
                    bg.setColor(Color.parseColor(selectedColorHex));
                } catch (Exception e) {
                    bg.setColor(Color.GRAY);
                }
                holder.imageView.setBackground(bg);
                holder.imageView.setColorFilter(Color.WHITE);
            } else {
                holder.imageView.setBackground(null);
                holder.imageView.setColorFilter(Color.GRAY);
            }

            holder.imageView.setOnClickListener(v -> {
                selectedIconName = iconName;
                notifyDataSetChanged();
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

    // Inner adapter for color grid
    private class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.VH> {
        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = new View(parent.getContext());
            int size = (int) (40 * parent.getContext().getResources().getDisplayMetrics().density);
            int margin = (int) (6 * parent.getContext().getResources().getDisplayMetrics().density);
            GridLayoutManager.LayoutParams params = new GridLayoutManager.LayoutParams(size, size);
            params.setMargins(margin, margin, margin, margin);
            view.setLayoutParams(params);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            String colorHex = COLORS[position];
            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.OVAL);
            bg.setColor(Color.parseColor(colorHex));

            if (colorHex.equals(selectedColorHex)) {
                bg.setStroke(6, Color.WHITE);
            }

            holder.view.setBackground(bg);
            holder.view.setOnClickListener(v -> {
                selectedColorHex = colorHex;
                notifyDataSetChanged();
                // Also update icon picker to reflect new color
                if (binding.rvIcons.getAdapter() != null) {
                    binding.rvIcons.getAdapter().notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return COLORS.length;
        }

        class VH extends RecyclerView.ViewHolder {
            View view;
            VH(View v) {
                super(v);
                this.view = v;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
