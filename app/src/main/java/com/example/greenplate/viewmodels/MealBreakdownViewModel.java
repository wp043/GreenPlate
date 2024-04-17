package com.example.greenplate.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MealBreakdownViewModel extends ViewModel {
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private MutableLiveData<List<PieEntry>> pieChartEntries = new MutableLiveData<>();

    // Constructor and other methods...

    // This method is used by the UI to observe changes to the pie chart data.
    public LiveData<List<PieEntry>> getPieChartEntries() {
        return pieChartEntries;
    }

    public MealBreakdownViewModel() {
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
            Log.d("Issue", "InputMealViewModel: " + e.getLocalizedMessage());
        }
    }
    public void fetchMealsForToday() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Get today's date in the format used in the database
            String formattedDate = new SimpleDateFormat("MM-dd-yyyy",
                    Locale.getDefault()).format(new Date());

            DatabaseReference mealsRefToday = myRef.child(formattedDate);
            mealsRefToday.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<PieEntry> pieEntries = new ArrayList<>();
                    long totalCalories = 0;

                    for (DataSnapshot mealSnapshot : dataSnapshot.getChildren()) {
                        String mealName = mealSnapshot.child("name").getValue(String.class);
                        Long calories = mealSnapshot.child("calories").getValue(Long.class);
                        if (mealName != null && calories != null) {
                            pieEntries.add(new PieEntry(calories, mealName));
                            totalCalories += calories;
                        }
                    }

                    // Update the LiveData with the new list
                    pieChartEntries.setValue(pieEntries);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("MealBreakdownViewModel", "loadMeals:onCancelled",
                            databaseError.toException());
                }
            });
        }
    }


}