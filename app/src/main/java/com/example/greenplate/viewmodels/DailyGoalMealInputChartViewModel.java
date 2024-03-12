package com.example.greenplate.viewmodels;

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DailyGoalMealInputChartViewModel extends ViewModel {
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    public DailyGoalMealInputChartViewModel() {
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



    public void getCalorieGoal(TextView view) {
        try {
            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("InputMealViewModel: There's no user signed in.");
            }
            myRef = database.getReference("user").child(currentUser.getUid())
                    .child("information");
            Query userInfoQuery = myRef;
            userInfoQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String gender = dataSnapshot.child("gender").getValue() != null
                            ? dataSnapshot.child("gender").getValue(String.class) : "Unknown";
                    double height = (dataSnapshot.child("height").getValue() != null
                            ? (double) dataSnapshot.child("height").getValue(Long.class) : 0);
                    double weight = (dataSnapshot.child("weight").getValue() != null
                            ? (double) dataSnapshot.child("weight").getValue(Long.class) : 0);
                    double age = (dataSnapshot.child("age").getValue() != null
                            ? (double) dataSnapshot.child("age").getValue(Long.class) : 0);

                    // Calculate the sum of height and weight
                    double bmr;
                    double amr;
                    if (gender.equals("Male")) {
                        bmr = 66.47 + 5.003 * height + 13.75 * weight - 6.755 * age;
                    } else if (gender.equals("Female")) {
                        bmr = 655.1 + 1.850 * height + 9.563 * weight - 4.676 * age;
                    } else {
                        bmr = 360.785 + 3.4265 * height + 11.6565 * weight - 5.7155 * age;
                    }
                    amr = bmr * 1.3;
                    int goal = (int) Math.round(amr);
                    view.setText("Calorie Goal: " + goal + " calories");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w("firebase", "loadPost:onCancelled", databaseError.toException());
                }
            });

        } catch (Exception e) {
            Log.d("Issue", "InputMealViewModel: " + e.getLocalizedMessage());
        }
    }

    // todo: Only add the calories of meals on current date
    public void getIntakeToday(TextView view) {
        try {
            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("InputMealViewModel: There's no user signed in.");
            }
            myRef = database.getReference("user").child(currentUser.getUid())
                    .child("meals");
            Query userInfoQuery = myRef;
            userInfoQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long sum = 0;
                    for (DataSnapshot mealSnapshot: dataSnapshot.getChildren()) {
                        if (mealSnapshot.child("calories").getValue() != null) {
                            Long calories = mealSnapshot.child("calories").getValue(Long.class);
                            sum += calories;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w("firebase", "loadPost:onCancelled", databaseError.toException());
                }
            });

        } catch (Exception e) {
            Log.d("Issue", "InputMealViewModel: " + e.getLocalizedMessage());
        }
    }
}