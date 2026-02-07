package com.wealthwise.app.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.wealthwise.app.R;
import com.wealthwise.app.databinding.ActivityMainBinding;
import com.wealthwise.app.util.PreferenceManager;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private PreferenceManager preferenceManager;

    private static final Set<Integer> TOP_LEVEL_DESTINATIONS = Set.of(
            R.id.dashboardFragment,
            R.id.transactionListFragment,
            R.id.analyticsFragment,
            R.id.settingsFragment
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        preferenceManager = new PreferenceManager(this);
        applyTheme();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupNavigation();
        setupFab();
    }

    private void applyTheme() {
        if (preferenceManager.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

            // Hide bottom nav on non-top-level destinations
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int destId = destination.getId();
                if (TOP_LEVEL_DESTINATIONS.contains(destId)) {
                    binding.bottomNavigation.setVisibility(View.VISIBLE);
                    binding.fabAdd.setVisibility(View.VISIBLE);
                } else if (destId == R.id.loginFragment || destId == R.id.registerFragment) {
                    binding.bottomNavigation.setVisibility(View.GONE);
                    binding.fabAdd.setVisibility(View.GONE);
                } else {
                    binding.bottomNavigation.setVisibility(View.VISIBLE);
                    binding.fabAdd.setVisibility(View.GONE);
                }
            });

            // Intercept the middle (add) tab
            binding.bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_add) {
                    return false;
                }
                return NavigationUI.onNavDestinationSelected(item, navController);
            });
        }
    }

    private void setupFab() {
        binding.fabAdd.setOnClickListener(v -> {
            navController.navigate(R.id.addEditTransactionFragment);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
