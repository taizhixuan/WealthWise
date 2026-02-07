package com.wealthwise.app.ui.forecast;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wealthwise.app.data.local.AppDatabase;
import com.wealthwise.app.data.repository.TransactionRepository;
import com.wealthwise.app.engine.forecast.ForecastEngine;
import com.wealthwise.app.engine.forecast.ForecastResult;

import java.util.List;

public class ForecastViewModel extends AndroidViewModel {

    private final TransactionRepository transactionRepository;
    private final ForecastEngine forecastEngine;

    private final MutableLiveData<ForecastResult> forecastResult = new MutableLiveData<>();
    private final MutableLiveData<Integer> selectedDays = new MutableLiveData<>(30);
    private final MutableLiveData<List<ForecastResult>> categoryForecasts = new MutableLiveData<>();

    public ForecastViewModel(@NonNull Application application) {
        super(application);
        transactionRepository = new TransactionRepository(application);
        forecastEngine = new ForecastEngine(transactionRepository);

        loadForecast(30);
    }

    // ── Load overall forecast for given number of days ─────────────────────

    public void loadForecast(int days) {
        selectedDays.setValue(days);

        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                ForecastResult result = forecastEngine.forecast(days);
                forecastResult.postValue(result);

                List<ForecastResult> catForecasts = forecastEngine.forecastByCategory(days);
                categoryForecasts.postValue(catForecasts);
            } catch (Exception e) {
                // Post null to indicate forecast could not be computed
                forecastResult.postValue(null);
                categoryForecasts.postValue(null);
            }
        });
    }

    // ── Period selection ───────────────────────────────────────────────────

    public void setDays(int days) {
        loadForecast(days);
    }

    // ── Getters ────────────────────────────────────────────────────────────

    public LiveData<ForecastResult> getForecastResult() {
        return forecastResult;
    }

    public LiveData<Integer> getSelectedDays() {
        return selectedDays;
    }

    public LiveData<List<ForecastResult>> getCategoryForecasts() {
        return categoryForecasts;
    }
}
