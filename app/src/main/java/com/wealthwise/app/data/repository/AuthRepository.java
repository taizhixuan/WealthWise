package com.wealthwise.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wealthwise.app.util.Resource;

public class AuthRepository {

    private final FirebaseAuth firebaseAuth;

    public AuthRepository(Application application) {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public boolean isLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public String getUserId() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public LiveData<Resource<FirebaseUser>> login(String email, String password) {
        MutableLiveData<Resource<FirebaseUser>> result = new MutableLiveData<>();
        result.postValue(Resource.loading(null));

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult ->
                        result.postValue(Resource.success(authResult.getUser())))
                .addOnFailureListener(e ->
                        result.postValue(Resource.error(e.getMessage(), null)));

        return result;
    }

    public LiveData<Resource<FirebaseUser>> register(String email, String password) {
        MutableLiveData<Resource<FirebaseUser>> result = new MutableLiveData<>();
        result.postValue(Resource.loading(null));

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult ->
                        result.postValue(Resource.success(authResult.getUser())))
                .addOnFailureListener(e ->
                        result.postValue(Resource.error(e.getMessage(), null)));

        return result;
    }

    public LiveData<Resource<FirebaseUser>> signInWithCredential(AuthCredential credential) {
        MutableLiveData<Resource<FirebaseUser>> result = new MutableLiveData<>();
        result.postValue(Resource.loading(null));

        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult ->
                        result.postValue(Resource.success(authResult.getUser())))
                .addOnFailureListener(e ->
                        result.postValue(Resource.error(e.getMessage(), null)));

        return result;
    }

    public void signOut() {
        firebaseAuth.signOut();
    }
}
