package com.example.greenplate.viewmodels;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.greenplate.models.Meal;
import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Personal;
import com.example.greenplate.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class UserInfoViewModel extends ViewModel {
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private Personal personal;

    public UserInfoViewModel() {
        try {
            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("InputMealViewModel: There's no user signed in.");
            }
            myRef = database.getReference("user").child(currentUser.getUid())
                    .child("meals");

        } catch (Exception e) {
            Log.d("Issue", "UserInfoViewModel: " + e.getLocalizedMessage());
        }
    }

    public GreenPlateStatus editUserInfo(Personal personal) {

    }
    public void updateInfo(OnCompleteListener<AuthResult> callback) {
        mAuth.(personal.getHeight(), personal.getWeight(), personal.getGender())
                .addOnCompleteListener(task -> {
                    callback.onComplete(task); // pass result to caller's callback
                });
    }

}