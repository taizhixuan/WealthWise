package com.wealthwise.app.util;

public final class Constants {

    private Constants() { }

    // --- Firestore Collection Names ---
    public static final String FIRESTORE_USERS = "users";
    public static final String FIRESTORE_TRANSACTIONS = "transactions";
    public static final String FIRESTORE_CATEGORIES = "categories";
    public static final String FIRESTORE_BUDGETS = "budgets";
    public static final String FIRESTORE_RECURRING = "recurring_transactions";
    public static final String FIRESTORE_ACCOUNTS = "accounts";

    // --- Sync ---
    public static final int SYNC_INTERVAL_HOURS = 6;

    // --- Defaults ---
    public static final String DEFAULT_CURRENCY = "USD";

    // --- Budget Thresholds ---
    public static final int BUDGET_WARNING_THRESHOLD = 80;
    public static final int BUDGET_DANGER_THRESHOLD = 100;

    // --- Forecast Weights ---
    public static final double FORECAST_MA_WEIGHT = 0.30;
    public static final double FORECAST_LR_WEIGHT = 0.40;
    public static final double FORECAST_WA_WEIGHT = 0.30;

    // --- Confidence Interval ---
    public static final double CONFIDENCE_Z_SCORE = 1.96;

    // --- UI ---
    public static final int MAX_RECENT_TRANSACTIONS = 5;

    // --- CSV ---
    public static final String CSV_DATE_FORMAT = "yyyy-MM-dd";
}
