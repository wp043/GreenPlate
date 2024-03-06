package com.example.greenplate.viewmodels;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.greenplate.models.Meal;
import com.example.greenplate.models.GreenPlateStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class InputMealViewModel extends ViewModel {
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    public InputMealViewModel() {
        try {
            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("InputMealViewModel: There's no user signed in.");
            }
            myRef = database.getReference("user").child(currentUser.getUid())
                    .child("meals");

//            Do some testing w/o UI
//            addMealToDatabase(null);
//            addMealToDatabase(new Meal(null, 0.));
//            addMealToDatabase(new Meal("Neg meal", -1.0));
//            addMealToDatabase(new Meal("    ", 12.0));
//            GreenPlateStatus s1 = addMealToDatabase(new Meal("Test meal 1", 120.0));
//            GreenPlateStatus s2 = addMealToDatabase(new Meal("Test meal 1", 150.4));
//            GreenPlateStatus s3 = addMealToDatabase(new Meal("Test meal 3", 741.23));
//            Log.d("Info", s1.toString());
//            Log.d("Info", s2.toString());
//            Log.d("Info", s3.toString());
        } catch (Exception e) {
            Log.d("Issue", "InputMealViewModel: " + e.getLocalizedMessage());
        }
    }

    public GreenPlateStatus addMealToDatabase(Meal meal) {
        if (meal == null) {
            return new GreenPlateStatus(false,
                    "Add meal: can't add a null meal");
        }
        if (meal.getCalorie() < 0.) {
            return new GreenPlateStatus(false,
                    "Add meal: can't a meal with negative calorie");
        }
        if (meal.getName() == null || TextUtils.isEmpty(meal.getName().trim())) {
            return new GreenPlateStatus(false,
                    "Add meal: can't a meal with null or blank name");
        }
        try {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("Signed-in user can't be found");
            }

            String mealKey = myRef.push().getKey();
            if (mealKey == null) {
                throw new RuntimeException("Failed to generate meal key");
            }
            myRef.child(mealKey).child("name").setValue(meal.getName());
            myRef.child(mealKey).child("calories").setValue(meal.getCalorie());
            Log.d("Success", String.format("Added %s to the db", meal));

        } catch (Exception e) {
            Log.d("Failure", "InputMeal failure due to: " + e.getLocalizedMessage());
            return new GreenPlateStatus(false, "Add meal: " + e.getLocalizedMessage());
        }
        return new GreenPlateStatus(true,
                String.format("%s added to database successfully", meal));
    }
}