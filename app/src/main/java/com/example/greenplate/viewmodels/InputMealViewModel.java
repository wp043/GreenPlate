package com.example.greenplate.viewmodels;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.greenplate.models.Meal;
import com.example.greenplate.models.GreenPlateStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


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
                    "Add meal: can't have a meal with negative calorie");
        }
        if (meal.getName() == null || TextUtils.isEmpty(meal.getName().trim())) {
            return new GreenPlateStatus(false,
                    "Add meal: can't have a meal with null or blank name");
        }
        try {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("Signed-in user can't be found");
            }

            DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
            Date date = new Date();
            String currDate = dateFormat.format(date);

            myRef = database.getReference("user").child(currentUser.getUid())
                    .child("meals").child(currDate);

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

    public boolean isInputDataValid(String name, String calories,
                                    EditText nameField, EditText caloriesField) {
        boolean error = false;
        if (name.trim().isEmpty()) {
            nameField.setError("Name of meal cannot be empty.");
            error = true;
        }
        if (calories.trim().isEmpty()) {
            caloriesField.setError("Estimated calorie count cannot be empty.");
            error = true;
        } else if (Integer.parseInt(calories) > 100000) {
            caloriesField.setError("Estimated calorie count must be a valid number.");
            error = true;
        }
        return !error;
    }

    public String getDateToday() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void getUserHeight(TextView view) {
        try {
            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("InputMealViewModel: There's no user signed in.");
            }
            myRef = database.getReference("user").child(currentUser.getUid())
                    .child("information").child("height");
            myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        double height = task.getResult().getValue() != null
                                ? task.getResult().getValue(Double.class) : 0;
                        String display = "<b>Height: </b>" + String.format("%.1f", height) + " cm";
                        view.setText(Html.fromHtml(display));
                    }
                }
            });
        } catch (Exception e) {
            Log.d("Issue", "InputMealViewModel: " + e.getLocalizedMessage());
        }
    }


    public void getUserWeight(TextView view) {
        try {
            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("InputMealViewModel: There's no user signed in.");
            }
            myRef = database.getReference("user").child(currentUser.getUid())
                    .child("information").child("weight");
            myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        double weight = task.getResult().getValue() != null
                                ? task.getResult().getValue(Double.class) : 0;
                        String display = "<b>Weight: </b>" + String.format("%.1f", weight) + " kg";
                        view.setText(Html.fromHtml(display));
                    }
                }
            });
        } catch (Exception e) {
            Log.d("Issue", "InputMealViewModel: " + e.getLocalizedMessage());
        }
    }

    public void getUserAge(TextView view) {
        try {
            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("InputMealViewModel: There's no user signed in.");
            }
            myRef = database.getReference("user").child(currentUser.getUid())
                    .child("information").child("age");
            myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        int age = task.getResult().getValue() != null
                                ? task.getResult().getValue(Integer.class) : 0;
                        String display = "<b>Age: </b>" + age;
                        view.setText(Html.fromHtml(display));
                    }
                }
            });
        } catch (Exception e) {
            Log.d("Issue", "InputMealViewModel: " + e.getLocalizedMessage());
        }
    }

    public void getUserGender(TextView view) {
        try {
            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("InputMealViewModel: There's no user signed in.");
            }
            myRef = database.getReference("user").child(currentUser.getUid())
                    .child("information").child("gender");
            myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        String gender = task.getResult().getValue() != null
                                ? task.getResult().getValue(String.class) : "Unknown";
                        String display = "<b>Gender: </b>" + gender;
                        view.setText(Html.fromHtml(display));
                    }
                }
            });
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
                            ? dataSnapshot.child("height").getValue(Double.class) : 0.0);
                    double weight = (dataSnapshot.child("weight").getValue() != null
                            ? dataSnapshot.child("weight").getValue(Double.class) : 0.0);
                    int age = (dataSnapshot.child("age").getValue() != null
                            ? dataSnapshot.child("age").getValue(Integer.class) : 0);

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
                    int goal = (int) Math.round(amr);

                    String display = "<b>Calorie Goal: </b>" + goal + " calories";
                    view.setText(Html.fromHtml(display));

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

    public void getIntakeToday(TextView view) {
        try {
            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("InputMealViewModel: There's no user signed in.");
            }

            DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
            Date date = new Date();
            String currDate = dateFormat.format(date);

            myRef = database.getReference("user").child(currentUser.getUid())
                    .child("meals").child(currDate);
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
                    String display = "<b>Total Intake Today: </b>" + sum + " calories";
                    view.setText(Html.fromHtml(display));
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