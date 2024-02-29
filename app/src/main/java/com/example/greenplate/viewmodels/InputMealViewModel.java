package com.example.greenplate.viewmodels;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.greenplate.models.Meal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InputMealViewModel extends ViewModel {
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    public InputMealViewModel() {
        try {
            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            myRef = database.getReference("meals");
            Log.d("Success", String.format("Successfully connect to the database for %s", currentUser.getEmail()));
//            addMealToDatabase(new Meal("Test meal", 320.5));
        } catch (Exception e) {
            Log.d("Issue", e.getLocalizedMessage());
        }
    }

    public void addMealToDatabase(Meal meal) {
        try {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                Log.d("Issue", "Current user can't be found. Fail to add meal.");
                return;
            }
            String userEmail = currentUser.getEmail();
            String mealKey = myRef.push().getKey();
            if (mealKey != null) {
                myRef.child(mealKey).child("name").setValue(meal.getName());
                myRef.child(mealKey).child("calories").setValue(meal.getCalorie());
                myRef.child(mealKey).child("userEmail").setValue(userEmail);
                Log.d("Success", "Meal added to database successfully");
            } else {
                Log.d("Issue", "Failed to generate meal key");
            }
        } catch (Exception e) {
            Log.d("Issue", e.getLocalizedMessage());
        }
    }

}
