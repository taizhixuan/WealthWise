package com.wealthwise.app.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private static final String PREF_NAME = "wealthwise_prefs";

    private static final String KEY_CURRENCY = "currency";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String KEY_LAST_SYNC_TIME = "last_sync_time";
    private static final String KEY_BUDGET_ALERT_THRESHOLD = "budget_alert_threshold";
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String KEY_USER_ID = "user_id";

    private final SharedPreferences prefs;

    public PreferenceManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // --- Currency ---

    public String getCurrency() {
        return prefs.getString(KEY_CURRENCY, "USD");
    }

    public void setCurrency(String code) {
        prefs.edit().putString(KEY_CURRENCY, code).apply();
    }

    // --- Dark Mode ---

    public boolean isDarkMode() {
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }

    public void setDarkMode(boolean enabled) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
    }

    // --- Notifications ---

    public boolean isNotificationsEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }

    public void setNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply();
    }

    // --- Last Sync Time ---

    public long getLastSyncTime() {
        return prefs.getLong(KEY_LAST_SYNC_TIME, 0L);
    }

    public void setLastSyncTime(long timeMillis) {
        prefs.edit().putLong(KEY_LAST_SYNC_TIME, timeMillis).apply();
    }

    // --- Budget Alert Threshold ---

    public int getBudgetAlertThreshold() {
        return prefs.getInt(KEY_BUDGET_ALERT_THRESHOLD, 80);
    }

    public void setBudgetAlertThreshold(int percent) {
        prefs.edit().putInt(KEY_BUDGET_ALERT_THRESHOLD, percent).apply();
    }

    // --- First Launch ---

    public boolean isFirstLaunch() {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true);
    }

    public void setFirstLaunch(boolean isFirst) {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, isFirst).apply();
    }

    // --- User ID ---

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public void setUserId(String userId) {
        prefs.edit().putString(KEY_USER_ID, userId).apply();
    }

    // --- Clear All ---

    public void clear() {
        prefs.edit().clear().apply();
    }
}
