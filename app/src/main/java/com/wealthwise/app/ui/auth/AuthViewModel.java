package com.wealthwise.app.ui.auth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.wealthwise.app.data.repository.AuthRepository;
import com.wealthwise.app.util.Resource;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;

    private final MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();
    private final MutableLiveData<Resource<FirebaseUser>> authResult = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);

        checkAuthState();
    }

    // ── Authentication actions ─────────────────────────────────────────────

    public void login(String email, String password) {
        LiveData<Resource<FirebaseUser>> result = authRepository.login(email, password);
        authResult.postValue(Resource.loading(null));
        observeAuthResult(result);
    }

    public void register(String email, String password) {
        LiveData<Resource<FirebaseUser>> result = authRepository.register(email, password);
        authResult.postValue(Resource.loading(null));
        observeAuthResult(result);
    }

    public void signInWithGoogle(AuthCredential credential) {
        LiveData<Resource<FirebaseUser>> result = authRepository.signInWithCredential(credential);
        authResult.postValue(Resource.loading(null));
        observeAuthResult(result);
    }

    private void observeAuthResult(LiveData<Resource<FirebaseUser>> result) {
        Observer<Resource<FirebaseUser>> observer = new Observer<Resource<FirebaseUser>>() {
            @Override
            public void onChanged(Resource<FirebaseUser> resource) {
                authResult.postValue(resource);
                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    currentUser.postValue(resource.data);
                }
                if (resource.status != Resource.Status.LOADING) {
                    result.removeObserver(this);
                }
            }
        };
        result.observeForever(observer);
    }

    public void signOut() {
        authRepository.signOut();
        currentUser.postValue(null);
        authResult.postValue(null);
    }

    // ── Auth state ─────────────────────────────────────────────────────────

    public boolean isLoggedIn() {
        return authRepository.isLoggedIn();
    }

    public void checkAuthState() {
        FirebaseUser user = authRepository.getCurrentUser();
        currentUser.postValue(user);
    }

    // ── Getters ────────────────────────────────────────────────────────────

    public LiveData<FirebaseUser> getCurrentUser() {
        return currentUser;
    }

    public LiveData<Resource<FirebaseUser>> getAuthResult() {
        return authResult;
    }
}
