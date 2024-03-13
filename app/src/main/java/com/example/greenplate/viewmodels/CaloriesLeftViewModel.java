package com.example.greenplate.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.github.mikephil.charting.data.PieEntry;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class CaloriesLeftViewModel extends ViewModel {
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private MutableLiveData<List<PieEntry>> pieChartEntries = new MutableLiveData<>();
    private InputMealViewModel inputMealVM = new InputMealViewModel();

    // Constructor and other methods...

    // This method is used by the UI to observe changes to the pie chart data.
    public LiveData<List<PieEntry>> getPieChartEntries() {
        return pieChartEntries;
    }

    public CaloriesLeftViewModel() {
        try {
            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("InputMealViewModel: There's no user signed in.");
            }
            myRef = database.getReference("user").child(currentUser.getUid());
        } catch (Exception e) {
            Log.d("Issue", "InputMealViewModel: " + e.getLocalizedMessage());
        }
    }

    public void fetchCaloriesForToday() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final long caloriesGoal;
                    String gender = dataSnapshot.child("information").child("gender")
                            .getValue() != null ? dataSnapshot.child("information").child("gender")
                            .getValue(String.class) : "Unknown";
                    double height = (dataSnapshot.child("information").child("height")
                            .getValue() != null ? dataSnapshot.child("information").child("height")
                            .getValue(Double.class) : 0.0);
                    double weight = (dataSnapshot.child("information").child("weight")
                            .getValue() != null ? dataSnapshot.child("information").child("weight")
                            .getValue(Double.class) : 0.0);
                    int age = (dataSnapshot.child("information").child("age").getValue() != null
                            ? dataSnapshot.child("information").child("age")
                            .getValue(Integer.class) : 0);

                    // Calculate the sum of height and weight
                    double bmr;
                    double amr;
                    if (gender.equals("Male")) {
                        bmr = 66.47 + 5.003 * height
                                + 13.75 * weight
                                - 6.755 * age;
                    } else if (gender.equals("Female")) {
                        bmr = 655.1 + 1.850 * height
                                + 9.563 * weight
                                - 4.676 * age;
                    } else {
                        bmr = 360.785 + 3.4265 * height
                                + 11.6565 * weight
                                - 5.7155 * age;
                    }
                    amr = bmr * 1.3;
                    caloriesGoal = (int) Math.round(amr);

                    long totalCalories = 0;
                    // Get today's date in the format used in the database
                    String formattedDate = new SimpleDateFormat("MM-dd-yyyy",
                            Locale.getDefault()).format(new Date());
                    for (DataSnapshot mealSnapshot : dataSnapshot.child("meals")
                            .child(formattedDate).getChildren()) {
                        String mealName = mealSnapshot.child("name").getValue(String.class);
                        Long calories = mealSnapshot.child("calories").getValue(Long.class);
                        if (mealName != null && calories != null) {
                            totalCalories += calories;
                        }
                    }

                    List<PieEntry> pieEntries = new ArrayList<>();
                    pieEntries.add(new PieEntry((caloriesGoal - totalCalories), "Calories Left"));
                    pieEntries.add(new PieEntry(totalCalories, "Consumed"));

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