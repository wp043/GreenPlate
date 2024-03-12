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

            //Do some testing w/o UI
            //addMealToDatabase(null);
            //addMealToDatabase(new Meal(null, 0.));
            //addMealToDatabase(new Meal("Neg meal", -1.0));
            //addMealToDatabase(new Meal("    ", 12.0));
            //GreenPlateStatus s1 = addMealToDatabase(new Meal("Test meal 1", 120.0));
            //GreenPlateStatus s2 = addMealToDatabase(new Meal("Test meal 1", 150.4));
            //GreenPlateStatus s3 = addMealToDatabase(new Meal("Test meal 3", 741.23));
            //Log.d("Info", s1.toString());
            //Log.d("Info", s2.toString());
            //Log.d("Info", s3.toString());
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
                    String height = (dataSnapshot.child("height").getValue() != null
                            ? dataSnapshot.child("height").getValue(String.class) : "0");
                    String weight = (dataSnapshot.child("weight").getValue() != null
                            ? dataSnapshot.child("weight").getValue(String.class) : "0");
                    String age = (dataSnapshot.child("age").getValue() != null
                            ? dataSnapshot.child("age").getValue(String.class) : "0");

                    // Calculate the sum of height and weight
                    double bmr;
                    double amr;
                    if (gender.equals("Male")) {
                        bmr = 66.47 + 5.003 * Double.parseDouble(height)
                                + 13.75 * Double.parseDouble(weight)
                                - 6.755 * Double.parseDouble(age);
                    } else if (gender.equals("Female")) {
                        bmr = 655.1 + 1.850 * Double.parseDouble(height)
                                + 9.563 * Double.parseDouble(weight)
                                - 4.676 * Double.parseDouble(age);
                    } else {
                        bmr = 360.785 + 3.4265 * Double.parseDouble(height)
                                + 11.6565 * Double.parseDouble(weight)
                                - 5.7155 * Double.parseDouble(age);
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

    public interface DataCallback {
        void onDataReceived(long data);
    }

    public void getIntakeToday(DataCallback callback) {
        try {
            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("InputMealViewModel: There's no user signed in.");
            }
            myRef = database.getReference("user").child(currentUser.getUid())
                    .child("meals");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long sum = 0;
                    for (DataSnapshot mealSnapshot : dataSnapshot.getChildren()) {
                        Long calories = mealSnapshot.child("calories").getValue(Long.class);
                        sum += calories != null ? calories : 0;
                    }
                    callback.onDataReceived(sum);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Error handling
                    callback.onDataReceived(0); // Or handle error differently
                }
            });
        } catch (Exception e) {
            Log.d("Issue", "DailyGoalMealInputChartViewModel: " + e.getLocalizedMessage());
        }

    }

    public void getCalorieGoal(DataCallback callback) {
        try {
            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("InputMealViewModel: There's no user signed in.");
            }
            myRef = database.getReference("user").child(currentUser.getUid())
                    .child("meals");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String gender = dataSnapshot.child("gender").getValue() != null
                            ? dataSnapshot.child("gender").getValue(String.class) : "Unknown";
                    String height = (dataSnapshot.child("height").getValue() != null
                            ? dataSnapshot.child("height").getValue(String.class) : "0");
                    String weight = (dataSnapshot.child("weight").getValue() != null
                            ? dataSnapshot.child("weight").getValue(String.class) : "0");
                    String age = (dataSnapshot.child("age").getValue() != null
                            ? dataSnapshot.child("age").getValue(String.class) : "0");

                    // Calculate the sum of height and weight
                    double bmr;
                    double amr;
                    if (gender.equals("Male")) {
                        bmr = 66.47 + 5.003 * Double.parseDouble(height)
                                + 13.75 * Double.parseDouble(weight)
                                - 6.755 * Double.parseDouble(age);
                    } else if (gender.equals("Female")) {
                        bmr = 655.1 + 1.850 * Double.parseDouble(height)
                                + 9.563 * Double.parseDouble(weight)
                                - 4.676 * Double.parseDouble(age);
                    } else {
                        bmr = 360.785 + 3.4265 * Double.parseDouble(height)
                                + 11.6565 * Double.parseDouble(weight)
                                - 5.7155 * Double.parseDouble(age);
                    }
                    amr = bmr * 1.3;
                    int goal = (int) Math.round(amr);
                    callback.onDataReceived(goal);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Error handling
                    callback.onDataReceived(0); // Or handle error differently
                }
            });
        } catch (Exception e) {
            Log.d("Issue", "DailyGoalMealInputChartViewModel: " + e.getLocalizedMessage());
        }

    }

}