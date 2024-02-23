package com.example.greenplate.viewmodels;

//import android.util.Log;
//import android.view.View;
import android.widget.ProgressBar;

import androidx.lifecycle.ViewModel;
import com.example.greenplate.models.User;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

//import java.util.concurrent.atomic.AtomicBoolean;

public class AccountCreationViewModel extends ViewModel {

    private User user;
    private FirebaseAuth mAuth;

    public AccountCreationViewModel() {
        //this.player = new User(username, email, password);
    }

    public void setUser(String email, String password) {
        this.user = new User(email, password);
        mAuth = FirebaseAuth.getInstance();
    }

    public void createAccount(ProgressBar bar, OnCompleteListener<AuthResult> callback) {

        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    callback.onComplete(task); // pass result to caller's callback
                });

    }



}