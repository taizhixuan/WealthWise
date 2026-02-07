package com.wealthwise.app.ui.common;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Pattern;

public class CurrencyInputFilter implements InputFilter {

    private static final Pattern PATTERN = Pattern.compile("^\\d*\\.?\\d{0,2}$");

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        StringBuilder builder = new StringBuilder(dest);
        builder.replace(dstart, dend, source.subSequence(start, end).toString());
        String result = builder.toString();

        if (!PATTERN.matcher(result).matches()) {
            return "";
        }
        return null;
    }
}
