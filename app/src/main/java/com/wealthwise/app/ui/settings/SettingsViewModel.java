package com.wealthwise.app.ui.settings;

import android.app.Application;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wealthwise.app.data.local.AppDatabase;
import com.wealthwise.app.data.local.dao.TransactionDao;
import com.wealthwise.app.data.local.entity.TransactionEntity;
import com.wealthwise.app.data.local.relation.TransactionWithCategory;
import com.wealthwise.app.data.remote.SyncManager;
import com.wealthwise.app.data.repository.AuthRepository;
import com.wealthwise.app.data.repository.TransactionRepository;
import com.wealthwise.app.util.Constants;
import com.wealthwise.app.util.CurrencyFormatter;
import com.wealthwise.app.util.DateUtils;
import com.wealthwise.app.util.PreferenceManager;
import com.wealthwise.app.util.Resource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SettingsViewModel extends AndroidViewModel {

    private final PreferenceManager preferenceManager;
    private final SyncManager syncManager;
    private final AuthRepository authRepository;
    private final TransactionRepository transactionRepository;

    private final MutableLiveData<Boolean> isDarkMode = new MutableLiveData<>();
    private final MutableLiveData<String> currency = new MutableLiveData<>();
    private final MutableLiveData<Boolean> notificationsEnabled = new MutableLiveData<>();
    private final MutableLiveData<String> lastSyncTime = new MutableLiveData<>();

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        preferenceManager = new PreferenceManager(application);
        syncManager = new SyncManager(application);
        authRepository = new AuthRepository(application);
        transactionRepository = new TransactionRepository(application);

        // Load current preference values
        isDarkMode.setValue(preferenceManager.isDarkMode());
        currency.setValue(preferenceManager.getCurrency());
        notificationsEnabled.setValue(preferenceManager.isNotificationsEnabled());
        updateLastSyncTimeDisplay();
    }

    // ── Dark mode ──────────────────────────────────────────────────────────

    public void toggleDarkMode() {
        boolean current = Boolean.TRUE.equals(isDarkMode.getValue());
        boolean newValue = !current;
        preferenceManager.setDarkMode(newValue);
        isDarkMode.setValue(newValue);
    }

    // ── Currency ───────────────────────────────────────────────────────────

    public void setCurrency(String currencyCode) {
        preferenceManager.setCurrency(currencyCode);
        currency.setValue(currencyCode);
        CurrencyFormatter.invalidate();
    }

    // ── Notifications ──────────────────────────────────────────────────────

    public void toggleNotifications() {
        boolean current = Boolean.TRUE.equals(notificationsEnabled.getValue());
        boolean newValue = !current;
        preferenceManager.setNotificationsEnabled(newValue);
        notificationsEnabled.setValue(newValue);
    }

    // ── Sync ───────────────────────────────────────────────────────────────

    public void syncNow() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            syncManager.syncAll();
            updateLastSyncTimeDisplay();
        });
    }

    private void updateLastSyncTimeDisplay() {
        long lastSync = preferenceManager.getLastSyncTime();
        if (lastSync == 0L) {
            lastSyncTime.postValue("Never");
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US);
            lastSyncTime.postValue(sdf.format(new Date(lastSync)));
        }
    }

    // ── CSV Export ─────────────────────────────────────────────────────────

    public LiveData<Resource<String>> exportCsv() {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.postValue(Resource.loading(null));

        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // Retrieve all transactions synchronously via monthly snapshots approach
                AppDatabase db = AppDatabase.getInstance(getApplication());
                TransactionDao transactionDao = db.transactionDao();

                // Get all transactions as LiveData, but we need sync access
                // Use a direct query approach through the DAO
                SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.CSV_DATE_FORMAT, Locale.US);

                File exportDir = new File(
                        getApplication().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                        "WealthWise");
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }

                String fileName = "wealthwise_export_" + dateFormat.format(new Date()) + ".csv";
                File csvFile = new File(exportDir, fileName);

                try (FileWriter writer = new FileWriter(csvFile)) {
                    writer.append("Date,Type,Amount,Category,Note,Payee\n");

                    List<TransactionWithCategory> transactions =
                            transactionDao.getAllWithCategorySync();

                    for (TransactionWithCategory twc : transactions) {
                        TransactionEntity t = twc.transaction;
                        String date = t.getDate() != null
                                ? dateFormat.format(t.getDate()) : "";
                        String type = t.getType() != null
                                ? t.getType().name() : "";
                        String amount = String.valueOf(t.getAmount());
                        String category = twc.category != null
                                ? escapeCsv(twc.category.getName()) : "";
                        String note = escapeCsv(t.getNote());
                        String payee = escapeCsv(t.getPayee());

                        writer.append(date).append(',')
                                .append(type).append(',')
                                .append(amount).append(',')
                                .append(category).append(',')
                                .append(note).append(',')
                                .append(payee).append('\n');
                    }
                }

                result.postValue(Resource.success(csvFile.getAbsolutePath()));
            } catch (IOException e) {
                result.postValue(Resource.error("Failed to export CSV: " + e.getMessage(), null));
            }
        });

        return result;
    }

    // ── Sign out ───────────────────────────────────────────────────────────

    public void signOut() {
        authRepository.signOut();
        preferenceManager.clear();
    }

    // ── Getters ────────────────────────────────────────────────────────────

    public LiveData<Boolean> getIsDarkMode() {
        return isDarkMode;
    }

    public LiveData<String> getCurrency() {
        return currency;
    }

    public LiveData<Boolean> getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public LiveData<String> getLastSyncTime() {
        return lastSyncTime;
    }

    private static String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
