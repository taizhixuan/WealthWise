package com.wealthwise.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.wealthwise.app.R;
import com.wealthwise.app.databinding.FragmentLoginBinding;
import com.wealthwise.app.util.Resource;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AuthViewModel authViewModel;
    private GoogleSignInClient googleSignInClient;

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if (account != null && account.getIdToken() != null) {
                        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                        authViewModel.signInWithGoogle(credential);
                    }
                } catch (ApiException e) {
                    showLoading(false);
                    showError("Google sign-in failed (code: " + e.getStatusCode() + ")");
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        setupGoogleSignIn();
        setupClickListeners();
        observeAuthState();
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
    }

    private void setupClickListeners() {
        binding.btnSignIn.setOnClickListener(v -> attemptLogin());

        binding.btnGoogleSignIn.setOnClickListener(v -> {
            showLoading(true);
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });

        binding.btnGuest.setOnClickListener(v -> navigateToDashboard());

        binding.btnRegister.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.registerFragment));
    }

    private void attemptLogin() {
        String email = binding.etEmail.getText() != null
                ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null
                ? binding.etPassword.getText().toString().trim() : "";

        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            binding.tilEmail.setError(getString(R.string.error_email_required));
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError(getString(R.string.error_email_invalid));
            valid = false;
        } else {
            binding.tilEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError(getString(R.string.error_password_required));
            valid = false;
        } else {
            binding.tilPassword.setError(null);
        }

        if (valid) {
            showLoading(true);
            authViewModel.login(email, password);
        }
    }

    private void observeAuthState() {
        authViewModel.getAuthResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case LOADING:
                    showLoading(true);
                    break;
                case SUCCESS:
                    showLoading(false);
                    navigateToDashboard();
                    break;
                case ERROR:
                    showLoading(false);
                    showError(resource.message);
                    break;
            }
        });
    }

    private void navigateToDashboard() {
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.loginFragment, true)
                .build();
        Navigation.findNavController(requireView())
                .navigate(R.id.dashboardFragment, null, navOptions);
    }

    private void showLoading(boolean isLoading) {
        binding.btnSignIn.setEnabled(!isLoading);
        binding.btnGoogleSignIn.setEnabled(!isLoading);
        binding.btnGuest.setEnabled(!isLoading);
    }

    private void showError(String message) {
        if (message != null && binding != null) {
            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
