package com.wealthwise.app.worker;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.wealthwise.app.data.repository.RecurringTransactionRepository;
import com.wealthwise.app.data.repository.TransactionRepository;
import com.wealthwise.app.engine.recurring.RecurringTransactionProcessor;

public class RecurringTransactionWorker extends Worker {

    public static final String WORK_NAME = "recurring_transaction_worker";

    public RecurringTransactionWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Application app = (Application) getApplicationContext();
            RecurringTransactionRepository recurringRepo =
                    new RecurringTransactionRepository(app);
            TransactionRepository transactionRepo =
                    new TransactionRepository(app);

            RecurringTransactionProcessor processor =
                    new RecurringTransactionProcessor(recurringRepo, transactionRepo);

            int processed = processor.processDueRecurring();
            return Result.success();
        } catch (Exception e) {
            return Result.retry();
        }
    }
}
