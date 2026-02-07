package com.wealthwise.app.util;

import android.graphics.Color;

import com.wealthwise.app.data.model.TransactionType;

public final class ChartColorPalette {

    private ChartColorPalette() { }

    /** 10 Material Design colors for category charts. */
    public static final int[] CATEGORY_COLORS = {
            Color.parseColor("#E53935"), // Red 600
            Color.parseColor("#1E88E5"), // Blue 600
            Color.parseColor("#43A047"), // Green 600
            Color.parseColor("#FB8C00"), // Orange 600
            Color.parseColor("#8E24AA"), // Purple 600
            Color.parseColor("#00ACC1"), // Cyan 600
            Color.parseColor("#FDD835"), // Yellow 600
            Color.parseColor("#6D4C41"), // Brown 600
            Color.parseColor("#3949AB"), // Indigo 600
            Color.parseColor("#D81B60"), // Pink 600
    };

    /**
     * Returns a color from the palette, cycling if index exceeds the array length.
     */
    public static int getColor(int index) {
        return CATEGORY_COLORS[Math.abs(index) % CATEGORY_COLORS.length];
    }

    public static final int INCOME_COLOR   = Color.parseColor("#2E7D32");
    public static final int EXPENSE_COLOR  = Color.parseColor("#C62828");
    public static final int TRANSFER_COLOR = Color.parseColor("#1565C0");
    public static final int FORECAST_COLOR = Color.parseColor("#0A7B61");

    /** FORECAST_COLOR with 40 (0x28) alpha for the confidence band overlay. */
    public static final int CONFIDENCE_BAND_COLOR = Color.argb(
            40,
            Color.red(FORECAST_COLOR),
            Color.green(FORECAST_COLOR),
            Color.blue(FORECAST_COLOR)
    );

    /**
     * Returns the appropriate color for the given transaction type.
     */
    public static int getColorForType(TransactionType type) {
        switch (type) {
            case INCOME:
                return INCOME_COLOR;
            case EXPENSE:
                return EXPENSE_COLOR;
            case TRANSFER:
                return TRANSFER_COLOR;
            default:
                return EXPENSE_COLOR;
        }
    }
}
