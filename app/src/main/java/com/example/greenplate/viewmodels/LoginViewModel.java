package com.example.greenplate.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.greenplate.models.User;
import com.example.greenplate.views.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

public class LoginViewModel {
    final private FirebaseAuth mAuth;
    private boolean status;


    public LoginViewModel() {
        mAuth = FirebaseAuth.getInstance();
    }


    public boolean checkUser(Context context, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                //.addOnCompleteListener(task -> {
                //    status = task.isSuccessful();
                //});
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(context,
                                    "Login successful.",
                                    Toast.LENGTH_LONG)
                            .show();
                    status = true;
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context,
                                    "Email or Password is invalid.",
                                    Toast.LENGTH_LONG)
                            .show();
                });
        return status;
    }

}
