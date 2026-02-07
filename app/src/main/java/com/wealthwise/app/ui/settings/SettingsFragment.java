package com.wealthwise.app.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.wealthwise.app.R;
import com.wealthwise.app.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        setupClickListeners();
        observeData();
    }

    private void setupClickListeners() {
        // Dark mode toggle
        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.toggleDarkMode();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });

        // Budget alerts toggle
        binding.switchBudgetAlerts.setOnCheckedChangeListener((buttonView, isChecked) ->
                viewModel.toggleNotifications());

        // Currency selector
        binding.rowCurrency.setOnClickListener(v -> showCurrencyPicker());

        // Export CSV
        binding.rowExportCsv.setOnClickListener(v -> {
            viewModel.exportCsv().observe(getViewLifecycleOwner(), resource -> {
                if (resource == null) return;
                switch (resource.status) {
                    case LOADING:
                        Snackbar.make(binding.getRoot(), R.string.loading, Snackbar.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        Snackbar.make(binding.getRoot(), "Exported to: " + resource.data,
                                Snackbar.LENGTH_LONG).show();
                        break;
                    case ERROR:
                        Snackbar.make(binding.getRoot(), resource.message != null ?
                                resource.message : "Export failed", Snackbar.LENGTH_LONG).show();
                        break;
                }
            });
        });

        // Sync now
        binding.rowSyncNow.setOnClickListener(v -> {
            Snackbar.make(binding.getRoot(), "Syncing...", Snackbar.LENGTH_SHORT).show();
            viewModel.syncNow();
        });

        // Sign out
        binding.rowSignOut.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.sign_out)
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton(R.string.sign_out, (dialog, which) -> {
                        viewModel.signOut();
                        Navigation.findNavController(requireView())
                                .navigate(R.id.loginFragment);
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        });
    }

    private void observeData() {
        viewModel.getIsDarkMode().observe(getViewLifecycleOwner(), isDark ->
                binding.switchDarkMode.setChecked(Boolean.TRUE.equals(isDark)));

        viewModel.getCurrency().observe(getViewLifecycleOwner(), currency ->
                binding.tvCurrencyValue.setText(currency != null ? currency : "USD"));

        viewModel.getNotificationsEnabled().observe(getViewLifecycleOwner(), enabled ->
                binding.switchBudgetAlerts.setChecked(Boolean.TRUE.equals(enabled)));

        viewModel.getLastSyncTime().observe(getViewLifecycleOwner(), time ->
                binding.tvLastSynced.setText(getString(R.string.last_synced, time)));
    }

    private void showCurrencyPicker() {
        String[] currencies = {"USD", "EUR", "GBP", "CAD", "AUD", "JPY", "INR"};
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.currency)
                .setItems(currencies, (dialog, which) -> viewModel.setCurrency(currencies[which]))
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
