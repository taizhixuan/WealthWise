package com.wealthwise.app;

import android.app.Application;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.wealthwise.app.data.local.AppDatabase;
import com.wealthwise.app.util.CurrencyFormatter;
import com.wealthwise.app.util.NotificationHelper;
import com.wealthwise.app.worker.BudgetAlertWorker;
import com.wealthwise.app.worker.RecurringTransactionWorker;
import com.wealthwise.app.worker.SyncWorker;

import java.util.concurrent.TimeUnit;

public class WealthWiseApplication extends Application {

    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        database = AppDatabase.getInstance(this);
        CurrencyFormatter.init(this);
        NotificationHelper.createNotificationChannels(this);
        schedulePeriodicWork();
    }

    private void schedulePeriodicWork() {
        WorkManager workManager = WorkManager.getInstance(this);

        // Recurring transaction processing - runs daily
        PeriodicWorkRequest recurringWork = new PeriodicWorkRequest.Builder(
                RecurringTransactionWorker.class, 24, TimeUnit.HOURS)
                .setInitialDelay(1, TimeUnit.HOURS)
                .build();
        workManager.enqueueUniquePeriodicWork(
                "recurring_transactions",
                ExistingPeriodicWorkPolicy.KEEP,
                recurringWork);

        // Sync with Firebase - runs every 6 hours when connected
        Constraints syncConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest syncWork = new PeriodicWorkRequest.Builder(
                SyncWorker.class, 6, TimeUnit.HOURS)
                .setConstraints(syncConstraints)
                .build();
        workManager.enqueueUniquePeriodicWork(
                "firebase_sync",
                ExistingPeriodicWorkPolicy.KEEP,
                syncWork);

        // Budget alert checking - runs every 12 hours
        PeriodicWorkRequest budgetAlertWork = new PeriodicWorkRequest.Builder(
                BudgetAlertWorker.class, 12, TimeUnit.HOURS)
                .build();
        workManager.enqueueUniquePeriodicWork(
                "budget_alerts",
                ExistingPeriodicWorkPolicy.KEEP,
                budgetAlertWork);
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
