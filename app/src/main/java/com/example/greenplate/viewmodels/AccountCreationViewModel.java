package com.example.greenplate.viewmodels;

import androidx.lifecycle.ViewModel;
import com.example.greenplate.models.User;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AccountCreationViewModel extends ViewModel {

    private User user;
    private FirebaseAuth mAuth;

    /**
     * No-arg constructor for VM.
     */
    public AccountCreationViewModel() {
        // this.player = new User(username, email, password);
    }

    /**
     * Setter for user.
     * @param email email of the user
     * @param password password of the user
     */
    public void setUser(String email, String password) {
        this.user = new User(email, password);
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Create account using firebase authorization.
     * @param callback callback function
     */
    public void createAccount(OnCompleteListener<AuthResult> callback) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    callback.onComplete(task); // pass result to caller's callback
                });
    }
}