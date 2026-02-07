package com.wealthwise.app.ui.common;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.wealthwise.app.R;
import com.wealthwise.app.util.DateUtils;

import java.util.Calendar;

public class MonthYearPickerDialog extends DialogFragment {

    public interface OnMonthYearSelectedListener {
        void onMonthYearSelected(int month, int year);
    }

    private static final String ARG_MONTH = "month";
    private static final String ARG_YEAR = "year";

    private OnMonthYearSelectedListener listener;

    public static MonthYearPickerDialog newInstance(int month, int year) {
        MonthYearPickerDialog dialog = new MonthYearPickerDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_MONTH, month);
        args.putInt(ARG_YEAR, year);
        dialog.setArguments(args);
        return dialog;
    }

    public void setOnMonthYearSelectedListener(OnMonthYearSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        int initialMonth = getArguments() != null ? getArguments().getInt(ARG_MONTH, DateUtils.getCurrentMonth()) : DateUtils.getCurrentMonth();
        int initialYear = getArguments() != null ? getArguments().getInt(ARG_YEAR, DateUtils.getCurrentYear()) : DateUtils.getCurrentYear();

        View view = LayoutInflater.from(requireContext()).inflate(android.R.layout.simple_list_item_2, null);

        // Create pickers programmatically
        android.widget.LinearLayout layout = new android.widget.LinearLayout(requireContext());
        layout.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        layout.setGravity(android.view.Gravity.CENTER);
        layout.setPadding(48, 32, 48, 32);

        NumberPicker monthPicker = new NumberPicker(requireContext());
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(initialMonth);
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        monthPicker.setDisplayedValues(monthNames);

        NumberPicker yearPicker = new NumberPicker(requireContext());
        yearPicker.setMinValue(2020);
        yearPicker.setMaxValue(2035);
        yearPicker.setValue(initialYear);

        layout.addView(monthPicker);
        layout.addView(yearPicker);

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.month_picker)
                .setView(layout)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    if (listener != null) {
                        listener.onMonthYearSelected(monthPicker.getValue(), yearPicker.getValue());
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }
}
