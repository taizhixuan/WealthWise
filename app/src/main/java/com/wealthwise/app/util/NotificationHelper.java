package com.wealthwise.app.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.wealthwise.app.R;

public final class NotificationHelper {

    public static final String CHANNEL_BUDGET_ALERTS = "budget_alerts";
    public static final String CHANNEL_RECURRING = "recurring";

    private static final int NOTIFICATION_ID_BUDGET_BASE = 1000;
    private static final int NOTIFICATION_ID_RECURRING = 2000;

    private NotificationHelper() { }

    /**
     * Creates notification channels for budget alerts and recurring transaction processing.
     * Must be called at app startup (safe to call multiple times).
     */
    public static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager == null) return;

            NotificationChannel budgetChannel = new NotificationChannel(
                    CHANNEL_BUDGET_ALERTS,
                    "Budget Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            budgetChannel.setDescription("Alerts when spending approaches or exceeds budget limits");
            manager.createNotificationChannel(budgetChannel);

            NotificationChannel recurringChannel = new NotificationChannel(
                    CHANNEL_RECURRING,
                    "Recurring Transactions",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            recurringChannel.setDescription("Notifications about processed recurring transactions");
            manager.createNotificationChannel(recurringChannel);
        }
    }

    /**
     * Shows a notification warning that a budget category is approaching or exceeding its limit.
     *
     * @param context      application context
     * @param categoryName the budget category name
     * @param percentUsed  percentage of budget used (e.g. 85.0 for 85%)
     */
    public static void showBudgetAlert(Context context, String categoryName, double percentUsed) {
        String title;
        String message;

        if (percentUsed >= 100) {
            title = "Budget Exceeded!";
            message = String.format("You've exceeded your %s budget (%.0f%% used).", categoryName, percentUsed);
        } else {
            title = "Budget Warning";
            message = String.format("You've used %.0f%% of your %s budget.", percentUsed, categoryName);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_BUDGET_ALERTS)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        int notificationId = NOTIFICATION_ID_BUDGET_BASE + Math.abs(categoryName.hashCode() % 999);
        managerCompat.notify(notificationId, builder.build());
    }

    /**
     * Shows a notification that recurring transactions have been processed.
     *
     * @param context application context
     * @param count   number of recurring transactions processed
     */
    public static void showRecurringProcessed(Context context, int count) {
        String title = "Recurring Transactions";
        String message = count == 1
                ? "1 recurring transaction has been processed."
                : count + " recurring transactions have been processed.";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_RECURRING)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(NOTIFICATION_ID_RECURRING, builder.build());
    }
}
