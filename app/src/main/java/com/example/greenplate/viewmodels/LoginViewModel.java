package com.example.greenplate.viewmodels;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginViewModel {
    private final FirebaseAuth mAuth;
    private int remainingAttempts;


    public LoginViewModel() {
        mAuth = FirebaseAuth.getInstance();
        remainingAttempts = 5;
    }


    public void checkUser(Context context, String email, String password,
                          OnSuccessListener<AuthResult> callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(context,
                                    "Login successful.",
                                    Toast.LENGTH_LONG)
                            .show();
                    callback.onSuccess(authResult);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context,
                                    "Email or Password is invalid.",
                                    Toast.LENGTH_SHORT)
                            .show();
                    if (remainingAttempts != 0) {
                        Toast.makeText(context,
                                        "Remaining attempts before app exit: " + remainingAttempts,
                                        Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        System.exit(0);
                    }
                });

    }

    public boolean isInputDataValid(String email, String password,
                                    EditText emailField, EditText passwordField) {
        boolean error = false;
        if (email.trim().isEmpty()) {
            emailField.setError("Email cannot be empty.");
            error = true;
        }
        if (password.trim().isEmpty()) {
            passwordField.setError("Password cannot be empty.");
            error = true;
        }
        return !error;
    }

    // Return false if no more sign in attempts are remaining
    public void updateRemainingAttempts() {
        remainingAttempts--;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public boolean isInputDataValidForTest(String email, String password) {
        boolean error = false;
        if (email.trim().isEmpty()) {
            error = true;
        }
        if (password.trim().isEmpty()) {
            error = true;
        }
        if (password.length() < 6) {
            error = true;
        }
        return !error;
    }
}
