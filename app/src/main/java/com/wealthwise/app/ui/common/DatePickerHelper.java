package com.wealthwise.app.ui.common;

import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.Date;

public final class DatePickerHelper {

    public interface OnDateSelectedListener {
        void onDateSelected(Date date);
    }

    private DatePickerHelper() {}

    public static void showDatePicker(FragmentManager fragmentManager, Date initialDate,
                                       OnDateSelectedListener listener) {
        long selection = initialDate != null ? initialDate.getTime() : MaterialDatePicker.todayInUtcMilliseconds();

        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(selection)
                .build();

        picker.addOnPositiveButtonClickListener(selectedDate -> {
            if (listener != null) {
                listener.onDateSelected(new Date(selectedDate));
            }
        });

        picker.show(fragmentManager, "date_picker");
    }
}
