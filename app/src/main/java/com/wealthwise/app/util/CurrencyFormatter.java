package com.wealthwise.app.util;

import android.content.Context;

import com.wealthwise.app.data.model.TransactionType;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class CurrencyFormatter {

    private static final Map<String, Locale> CURRENCY_LOCALE_MAP = new HashMap<>();

    static {
        CURRENCY_LOCALE_MAP.put("USD", Locale.US);
        CURRENCY_LOCALE_MAP.put("EUR", Locale.GERMANY);
        CURRENCY_LOCALE_MAP.put("GBP", Locale.UK);
        CURRENCY_LOCALE_MAP.put("JPY", Locale.JAPAN);
        CURRENCY_LOCALE_MAP.put("CAD", Locale.CANADA);
        CURRENCY_LOCALE_MAP.put("AUD", new Locale("en", "AU"));
        CURRENCY_LOCALE_MAP.put("CHF", new Locale("de", "CH"));
        CURRENCY_LOCALE_MAP.put("CNY", Locale.CHINA);
        CURRENCY_LOCALE_MAP.put("INR", new Locale("en", "IN"));
        CURRENCY_LOCALE_MAP.put("KRW", Locale.KOREA);
        CURRENCY_LOCALE_MAP.put("MYR", new Locale("ms", "MY"));
        CURRENCY_LOCALE_MAP.put("SGD", new Locale("en", "SG"));
    }

    private static PreferenceManager preferenceManager;
    private static volatile NumberFormat cachedFormat;

    private CurrencyFormatter() { }

    public static void init(Context context) {
        preferenceManager = new PreferenceManager(context.getApplicationContext());
        cachedFormat = null;
    }

    public static void invalidate() {
        cachedFormat = null;
    }

    private static NumberFormat getFormatter() {
        NumberFormat fmt = cachedFormat;
        if (fmt != null) return fmt;

        String currencyCode = preferenceManager != null
                ? preferenceManager.getCurrency() : "USD";
        Locale locale = CURRENCY_LOCALE_MAP.getOrDefault(currencyCode, Locale.US);

        fmt = NumberFormat.getCurrencyInstance(locale);
        try {
            fmt.setCurrency(Currency.getInstance(currencyCode));
        } catch (IllegalArgumentException ignored) {
        }
        cachedFormat = fmt;
        return fmt;
    }

    private static String getCurrencySymbol() {
        return getFormatter().getCurrency().getSymbol(
                CURRENCY_LOCALE_MAP.getOrDefault(
                        preferenceManager != null ? preferenceManager.getCurrency() : "USD",
                        Locale.US));
    }

    /**
     * Formats a monetary amount, e.g. "$1,234.56".
     */
    public static String format(double amount) {
        return getFormatter().format(amount);
    }

    /**
     * Formats with a sign prefix based on transaction type.
     * Income shows "+$1,234.56", expense shows "-$1,234.56", transfer shows "$1,234.56".
     */
    public static String formatSigned(double amount, TransactionType type) {
        String formatted = getFormatter().format(Math.abs(amount));
        switch (type) {
            case INCOME:
                return "+" + formatted;
            case EXPENSE:
                return "-" + formatted;
            case TRANSFER:
            default:
                return formatted;
        }
    }

    /**
     * Formats a compact representation, e.g. "$1.2K", "$3.5M".
     */
    public static String formatCompact(double amount) {
        double abs = Math.abs(amount);
        String sign = amount < 0 ? "-" : "";
        String symbol = getCurrencySymbol();

        if (abs >= 1_000_000_000) {
            return sign + symbol + String.format(Locale.US, "%.1fB", abs / 1_000_000_000);
        } else if (abs >= 1_000_000) {
            return sign + symbol + String.format(Locale.US, "%.1fM", abs / 1_000_000);
        } else if (abs >= 1_000) {
            return sign + symbol + String.format(Locale.US, "%.1fK", abs / 1_000);
        } else {
            return getFormatter().format(amount);
        }
    }

    /**
     * Parses a currency string (e.g. "$1,234.56") back to a double.
     *
     * @throws IllegalArgumentException if the string cannot be parsed
     */
    public static double parse(String currencyString) {
        try {
            Number number = getFormatter().parse(currencyString);
            return number != null ? number.doubleValue() : 0.0;
        } catch (ParseException e) {
            throw new IllegalArgumentException("Cannot parse currency string: " + currencyString, e);
        }
    }
}
