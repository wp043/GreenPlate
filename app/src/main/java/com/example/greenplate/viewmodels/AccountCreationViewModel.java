package com.example.greenplate.viewmodels;

import android.view.View;
import android.widget.ProgressBar;

import androidx.lifecycle.ViewModel;
import com.example.greenplate.models.User;

import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.atomic.AtomicBoolean;

public class AccountCreationViewModel extends ViewModel {

    private User user;
    private FirebaseAuth mAuth;

    public AccountCreationViewModel() {
//        this.player = new User(username, email, password);
    }

    public void setUser(String email, String password) {
        this.user = new User(email, password);
    }

    public boolean createAccount(ProgressBar bar) {
        AtomicBoolean success = new AtomicBoolean(false);
        mAuth
            .createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
            .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bar.setVisibility(View.GONE);

                        // if the user created intent to login activity
//                        Intent intent
//                                = new Intent(RegistrationActivity.this,
//                                MainActivity.class);
//                        startActivity(intent);
                        success.set(true);
                    }
                    else {
                        bar.setVisibility(View.GONE);
                    }
                }
            );
        return success.get();
    }



}