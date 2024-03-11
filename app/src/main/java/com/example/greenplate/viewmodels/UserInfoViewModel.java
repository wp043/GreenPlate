//package com.example.greenplate.viewmodels;
//
//import android.text.TextUtils;
//import android.util.Log;
//import android.widget.EditText;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.lifecycle.ViewModel;
//
//import com.example.greenplate.models.GreenPlateStatus;
//import com.example.greenplate.models.Personal;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//public class UserInfoViewModel extends ViewModel {
//    private FirebaseDatabase database;
//    private FirebaseAuth mAuth;
//    private DatabaseReference myRef;
//
//    public UserInfoViewModel() {
//        try {
//            database = FirebaseDatabase.getInstance();
//            mAuth = FirebaseAuth.getInstance();
//            FirebaseUser currentUser = mAuth.getCurrentUser();
//            if (currentUser == null) {
//                throw new RuntimeException("UserInfoViewModel: There's no user signed in.");
//            }
//            myRef = database.getReference("user").child(currentUser.getUid())
//                    .child("information");
//        } catch (Exception e) {
//            Log.d("Issue", "UserInfoViewModel: " + e.getLocalizedMessage());
//        }
//    }
//
//    public GreenPlateStatus editPersonalInformation(Personal personal) {
//        if (personal == null) {
//            return new GreenPlateStatus(false,
//                    "Edit personal information: can't update null information");
//        }
//        if (personal.getHeight() == null || TextUtils.isEmpty(personal.getHeight().trim())) {
//            return new GreenPlateStatus(false,
//                    "Edit personal information: can't have null height");
//        }
//        if (personal.getWeight() == null || TextUtils.isEmpty(personal.getWeight().trim())) {
//            return new GreenPlateStatus(false,
//                    "Edit personal information: can't have null weight");
//        }
//        if (personal.getGender() == null || TextUtils.isEmpty(personal.getGender().trim())) {
//            return new GreenPlateStatus(false,
//                    "Edit personal information: can't have null gender");
//        }
//        try {
//            FirebaseUser currentUser = mAuth.getCurrentUser();
//            if (currentUser == null) {
//                throw new RuntimeException("Signed-in user can't be found");
//            }
//
//            String personalKey = myRef.push().getKey();
//            if (personalKey == null) {
//                throw new RuntimeException("Failed to generate personal key");
//            }
//            myRef.child(personalKey).child("height").setValue(personal.getHeight());
//            myRef.child(personalKey).child("weight").setValue(personal.getWeight());
//            myRef.child(personalKey).child("gender").setValue(personal.getGender());
//            Log.d("Success", String.format("Added %s to the db", personal));
//
//        } catch (Exception e) {
//            Log.d("Failure", "Edit Personal Information failure due to: " + e.getLocalizedMessage());
//            return new GreenPlateStatus(false, "Edit Personal Info: " + e.getLocalizedMessage());
//        }
//        return new GreenPlateStatus(true,
//                String.format("%s added to database successfully", personal));
//    }
//
//    public boolean isInputDataValid(String height, String weight, String gender,
//                                    EditText heightField, EditText weightField,
//                                    RadioButton genderButton) {
//        boolean error = false;
//        if (height.trim().isEmpty()) {
//            heightField.setError("Cannot have null height");
//            error = true;
//        }
//        if (weight.trim().isEmpty()) {
//            weightField.setError("Estimated calorie count cannot be empty.");
//            error = true;
//        }
//        if (gender.trim().isEmpty()) {
//            genderButton.setError("Estimated calorie count cannot be empty.");
//            error = true;
//        }
//        return !error;
//    }
//    public void getUserHeight(TextView view) {
//        try {
//            database = FirebaseDatabase.getInstance();
//            mAuth = FirebaseAuth.getInstance();
//            FirebaseUser currentUser = mAuth.getCurrentUser();
//            if (currentUser == null) {
//                throw new RuntimeException("UserInfoViewModel: There's no user signed in.");
//            }
//            myRef = database.getReference("user").child(currentUser.getUid())
//                    .child("information").child("height");
//            myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DataSnapshot> task) {
//                    if (!task.isSuccessful()) {
//                        Log.e("firebase", "Error getting data", task.getException());
//                    } else {
//                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
//                        double height = task.getResult().getValue() != null
//                                ? task.getResult().getValue(Double.class) : 0;
//                        view.setText("Height: " + String.format("%.1f", height) + " cm");
//                    }
//                }
//            });
//        } catch (Exception e) {
//            Log.d("Issue", "UserInfoViewModel: " + e.getLocalizedMessage());
//        }
//    }
//    public void getUserWeight(TextView view) {
//        try {
//            database = FirebaseDatabase.getInstance();
//            mAuth = FirebaseAuth.getInstance();
//            FirebaseUser currentUser = mAuth.getCurrentUser();
//            if (currentUser == null) {
//                throw new RuntimeException("UserInfoViewModel: There's no user signed in.");
//            }
//            myRef = database.getReference("user").child(currentUser.getUid())
//                    .child("information").child("weight");
//            myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DataSnapshot> task) {
//                    if (!task.isSuccessful()) {
//                        Log.e("firebase", "Error getting data", task.getException());
//                    } else {
//                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
//                        double weight = task.getResult().getValue() != null
//                                ? task.getResult().getValue(Double.class) : 0;
//                        view.setText("Weight: " + String.format("%.1f", weight) + " kg");
//                    }
//                }
//            });
//        } catch (Exception e) {
//            Log.d("Issue", "UserInfoViewModel: " + e.getLocalizedMessage());
//        }
//    }
//    public void getUserGender(TextView view) {
//        try {
//            database = FirebaseDatabase.getInstance();
//            mAuth = FirebaseAuth.getInstance();
//            FirebaseUser currentUser = mAuth.getCurrentUser();
//            if (currentUser == null) {
//                throw new RuntimeException("UserInfoViewModel: There's no user signed in.");
//            }
//            myRef = database.getReference("user").child(currentUser.getUid())
//                    .child("information").child("gender");
//            myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DataSnapshot> task) {
//                    if (!task.isSuccessful()) {
//                        Log.e("firebase", "Error getting data", task.getException());
//                    } else {
//                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
//                        double gender = task.getResult().getValue() != null
//                                ? task.getResult().getValue(Double.class) : 0;
//                        view.setText("Gender: " + String.format("%.1f", gender));
//                    }
//                }
//            });
//        } catch (Exception e) {
//            Log.d("Issue", "UserInfoViewModel: " + e.getLocalizedMessage());
//        }
//    }
//}
package com.example.greenplate.viewmodels;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.greenplate.models.Personal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserInfoViewModel extends ViewModel {
    private final DatabaseReference userRef;
    private final MutableLiveData<Personal> userPersonalInfo = new MutableLiveData<>();

    public UserInfoViewModel() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference("user")
                    .child(currentUser.getUid()).child("information");
            fetchPersonalInformation();
        } else {
            throw new RuntimeException("UserInfoViewModel: There's no user signed in.");
        }
    }

    private void fetchPersonalInformation() {
        // Fetch the personal information from Firebase and post the value to userPersonalInfo LiveData
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Personal personal = dataSnapshot.getValue(Personal.class);
                userPersonalInfo.postValue(personal);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Firebase", "Failed to read value.", databaseError.toException());
            }
        });
    }

    public LiveData<Personal> getUserPersonalInfo() {
        return userPersonalInfo;
    }

    public void updatePersonalInformation(Personal personal) {
        // Directly update the fields in Firebase
        if (personal != null) {
            userRef.setValue(personal);
        }
    }

    public boolean validatePersonalInformation(String height, String weight, String gender) {
        // Return true if the information is valid, else false
        // Implement your validation logic here and return the result
        boolean isValid = !TextUtils.isEmpty(height) && !TextUtils.isEmpty(weight) && !TextUtils.isEmpty(gender);
        return isValid;
    }
}
