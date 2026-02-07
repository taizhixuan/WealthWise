package com.wealthwise.app.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.wealthwise.app.R;
import com.wealthwise.app.databinding.FragmentRegisterBinding;
import com.wealthwise.app.util.Resource;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private AuthViewModel authViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        setupClickListeners();
        observeAuthState();
    }

    private void setupClickListeners() {
        binding.btnCreateAccount.setOnClickListener(v -> attemptRegister());

        binding.btnSignIn.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_register_to_login));
    }

    private void attemptRegister() {
        String email = binding.etEmail.getText() != null
                ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null
                ? binding.etPassword.getText().toString().trim() : "";
        String confirmPassword = binding.etConfirmPassword.getText() != null
                ? binding.etConfirmPassword.getText().toString().trim() : "";

        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            binding.tilEmail.setError("Email is required");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError("Enter a valid email");
            valid = false;
        } else {
            binding.tilEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError("Password is required");
            valid = false;
        } else if (password.length() < 6) {
            binding.tilPassword.setError("Password must be at least 6 characters");
            valid = false;
        } else {
            binding.tilPassword.setError(null);
        }

        if (!password.equals(confirmPassword)) {
            binding.tilConfirmPassword.setError("Passwords do not match");
            valid = false;
        } else {
            binding.tilConfirmPassword.setError(null);
        }

        if (valid) {
            binding.btnCreateAccount.setEnabled(false);
            authViewModel.register(email, password);
        }
    }

    private void observeAuthState() {
        authViewModel.getAuthResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            switch (result.status) {
                case LOADING:
                    binding.btnCreateAccount.setEnabled(false);
                    break;
                case SUCCESS:
                    binding.btnCreateAccount.setEnabled(true);
                    navigateToDashboard();
                    break;
                case ERROR:
                    binding.btnCreateAccount.setEnabled(true);
                    if (result.message != null) {
                        Snackbar.make(binding.getRoot(), result.message, Snackbar.LENGTH_LONG).show();
                    }
                    break;
            }
        });
    }

    private void navigateToDashboard() {
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.registerFragment, true)
                .build();
        Navigation.findNavController(requireView())
                .navigate(R.id.dashboardFragment, null, navOptions);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
