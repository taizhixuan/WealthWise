package com.wealthwise.app.ui.transaction;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.wealthwise.app.R;
import com.wealthwise.app.data.model.TransactionType;
import com.wealthwise.app.databinding.BottomSheetTransactionFilterBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TransactionFilterBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "TransactionFilterBottomSheet";

    private BottomSheetTransactionFilterBinding binding;
    private FilterListener filterListener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private Date fromDate;
    private Date toDate;

    public interface FilterListener {
        void onFilterApplied(FilterCriteria criteria);
    }

    public static class FilterCriteria {
        private Date fromDate;
        private Date toDate;
        private Long categoryId;
        private TransactionType type;
        private Double minAmount;
        private Double maxAmount;

        public Date getFromDate() { return fromDate; }
        public void setFromDate(Date fromDate) { this.fromDate = fromDate; }
        public Date getToDate() { return toDate; }
        public void setToDate(Date toDate) { this.toDate = toDate; }
        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
        public TransactionType getType() { return type; }
        public void setType(TransactionType type) { this.type = type; }
        public Double getMinAmount() { return minAmount; }
        public void setMinAmount(Double minAmount) { this.minAmount = minAmount; }
        public Double getMaxAmount() { return maxAmount; }
        public void setMaxAmount(Double maxAmount) { this.maxAmount = maxAmount; }
    }

    public void setFilterListener(FilterListener listener) {
        this.filterListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetTransactionFilterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupDateFields();
        setupTypeChips();
        setupButtons();
    }

    private void setupDateFields() {
        binding.etDateFrom.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (fromDate != null) {
                calendar.setTime(fromDate);
            }
            new DatePickerDialog(requireContext(),
                    (datePicker, year, month, dayOfMonth) -> {
                        Calendar selected = Calendar.getInstance();
                        selected.set(year, month, dayOfMonth, 0, 0, 0);
                        selected.set(Calendar.MILLISECOND, 0);
                        fromDate = selected.getTime();
                        binding.etDateFrom.setText(dateFormat.format(fromDate));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        binding.etDateTo.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (toDate != null) {
                calendar.setTime(toDate);
            }
            new DatePickerDialog(requireContext(),
                    (datePicker, year, month, dayOfMonth) -> {
                        Calendar selected = Calendar.getInstance();
                        selected.set(year, month, dayOfMonth, 23, 59, 59);
                        selected.set(Calendar.MILLISECOND, 999);
                        toDate = selected.getTime();
                        binding.etDateTo.setText(dateFormat.format(toDate));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });
    }

    private void setupTypeChips() {
        binding.chipGroupType.setSingleSelection(true);
    }

    private void setupButtons() {
        binding.btnApply.setOnClickListener(v -> {
            if (filterListener != null) {
                FilterCriteria criteria = buildCriteria();
                filterListener.onFilterApplied(criteria);
            }
            dismiss();
        });

        binding.btnReset.setOnClickListener(v -> resetAllFields());
    }

    private FilterCriteria buildCriteria() {
        FilterCriteria criteria = new FilterCriteria();

        criteria.setFromDate(fromDate);
        criteria.setToDate(toDate);

        int checkedTypeChipId = binding.chipGroupType.getCheckedChipId();
        if (checkedTypeChipId == R.id.chip_income) {
            criteria.setType(TransactionType.INCOME);
        } else if (checkedTypeChipId == R.id.chip_expense) {
            criteria.setType(TransactionType.EXPENSE);
        } else {
            criteria.setType(null);
        }

        int checkedCategoryChipId = binding.chipGroupCategories.getCheckedChipId();
        if (checkedCategoryChipId != View.NO_ID) {
            Chip checkedChip = binding.chipGroupCategories.findViewById(checkedCategoryChipId);
            if (checkedChip != null && checkedChip.getTag() instanceof Long) {
                criteria.setCategoryId((Long) checkedChip.getTag());
            }
        }

        String minAmountStr = binding.etAmountMin.getText() != null
                ? binding.etAmountMin.getText().toString().trim() : "";
        if (!TextUtils.isEmpty(minAmountStr)) {
            try {
                criteria.setMinAmount(Double.parseDouble(minAmountStr));
            } catch (NumberFormatException ignored) {
            }
        }

        String maxAmountStr = binding.etAmountMax.getText() != null
                ? binding.etAmountMax.getText().toString().trim() : "";
        if (!TextUtils.isEmpty(maxAmountStr)) {
            try {
                criteria.setMaxAmount(Double.parseDouble(maxAmountStr));
            } catch (NumberFormatException ignored) {
            }
        }

        return criteria;
    }

    private void resetAllFields() {
        fromDate = null;
        toDate = null;

        binding.etDateFrom.setText("");
        binding.etDateTo.setText("");
        binding.etAmountMin.setText("");
        binding.etAmountMax.setText("");

        binding.chipGroupType.clearCheck();
        binding.chipGroupCategories.clearCheck();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
