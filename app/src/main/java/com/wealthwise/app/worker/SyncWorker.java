package com.wealthwise.app.worker;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.wealthwise.app.data.remote.SyncManager;

public class SyncWorker extends Worker {

    public static final String WORK_NAME = "sync_worker";

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Application app = (Application) getApplicationContext();
            SyncManager syncManager = new SyncManager(app);
            syncManager.syncAll();
            return Result.success();
        } catch (Exception e) {
            return Result.retry();
        }
    }
}
