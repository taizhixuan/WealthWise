package com.wealthwise.app.ui.recommendation;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wealthwise.app.data.local.AppDatabase;
import com.wealthwise.app.data.repository.BudgetRepository;
import com.wealthwise.app.data.repository.TransactionRepository;
import com.wealthwise.app.engine.recommendation.Recommendation;
import com.wealthwise.app.engine.recommendation.RecommendationEngine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecommendationViewModel extends AndroidViewModel {

    private static final String PREFS_NAME = "recommendation_prefs";
    private static final String KEY_DISMISSED_IDS = "dismissed_ids";

    private final RecommendationEngine recommendationEngine;
    private final SharedPreferences sharedPreferences;

    private final MutableLiveData<List<Recommendation>> recommendations = new MutableLiveData<>();
    private final Set<String> dismissedIds;

    public RecommendationViewModel(@NonNull Application application) {
        super(application);

        TransactionRepository transactionRepository = new TransactionRepository(application);
        BudgetRepository budgetRepository = new BudgetRepository(application);
        recommendationEngine = new RecommendationEngine(transactionRepository, budgetRepository);

        sharedPreferences = application.getSharedPreferences(PREFS_NAME, Application.MODE_PRIVATE);
        dismissedIds = new HashSet<>(
                sharedPreferences.getStringSet(KEY_DISMISSED_IDS, new HashSet<>()));

        loadRecommendations();
    }

    // ── Load recommendations and filter out dismissed ones ─────────────────

    public void loadRecommendations() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                List<Recommendation> allRecommendations = recommendationEngine.generate();
                List<Recommendation> filtered = new ArrayList<>();
                for (Recommendation recommendation : allRecommendations) {
                    if (!dismissedIds.contains(recommendation.getId())) {
                        filtered.add(recommendation);
                    }
                }
                recommendations.postValue(filtered);
            } catch (Exception e) {
                recommendations.postValue(new ArrayList<>());
            }
        });
    }

    // ── Dismiss a recommendation ───────────────────────────────────────────

    public void dismiss(String recommendationId) {
        dismissedIds.add(recommendationId);
        sharedPreferences.edit()
                .putStringSet(KEY_DISMISSED_IDS, dismissedIds)
                .apply();

        // Update the live data to remove the dismissed recommendation
        List<Recommendation> current = recommendations.getValue();
        if (current != null) {
            List<Recommendation> updated = new ArrayList<>();
            for (Recommendation recommendation : current) {
                if (!recommendation.getId().equals(recommendationId)) {
                    updated.add(recommendation);
                }
            }
            recommendations.setValue(updated);
        }
    }

    // ── Getters ────────────────────────────────────────────────────────────

    public LiveData<List<Recommendation>> getRecommendations() {
        return recommendations;
    }
}
