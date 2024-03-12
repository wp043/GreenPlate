
package com.example.greenplate.viewmodels;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Personal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class UserInfoViewModel extends ViewModel {
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private final MutableLiveData<Personal> userPersonalInfo = new MutableLiveData<>();

    /**
     * User database structure:
     * user:
     *    userID:
     *         age:
     *         height: 
     *         weight:
     *         gender:
     */
    public UserInfoViewModel() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference("user")
                    .child(currentUser.getUid()).child("information");
            // Fetch the personal information every time PersonalActivity is navigated to
            fetchPersonalInformation(); 
        } else {
            throw new RuntimeException("UserInfoViewModel: There's no user signed in.");
        }
    }

    private void fetchPersonalInformation() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Integer ageWrapper = dataSnapshot.child("age").getValue(Integer.class);
                    int age = (ageWrapper != null) ? ageWrapper : 0;

                    Double heightWrapper = dataSnapshot.child("height").getValue(Double.class);
                    double height = (heightWrapper != null) ? heightWrapper : 0.0;

                    Double weightWrapper = dataSnapshot.child("weight").getValue(Double.class);
                    double weight = (weightWrapper != null) ? weightWrapper : 0.0;

                    String gender = dataSnapshot.child("gender").getValue(String.class);
                    gender = (gender != null) ? gender : "Unknown";

                    Personal personal = new Personal(age, height, weight, gender);
                    userPersonalInfo.postValue(personal);
                }
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

    public GreenPlateStatus updatePersonalInformation(Personal personal) {
        // Directly update the fields in Firebase
        if (personal == null) {
            return new GreenPlateStatus(false,
                    "Edit personal information: can't add null information");
        }
        userRef.setValue(personal);
        if (personal.getAge() < 0.) {
            return new GreenPlateStatus(false,
                    "Edit personal information: can't have negative age");
        }
        if (personal.getHeight() <= 0.) {
            return new GreenPlateStatus(false,
                    "Edit personal information: can't have negative height");
        }
        if (personal.getWeight() <= 0.) {
            return new GreenPlateStatus(false,
                    "Edit personal information: can't have negative weight");
        }
        if (personal.getGender() == null || TextUtils.isEmpty(personal.getGender().trim())) {
            return new GreenPlateStatus(false,
                    "Edit personal information: can't have null gender");
        }
        try {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("Signed-in user can't be found");
            }

            String personalKey = userRef.push().getKey();
            // String personalKey = currentUser.getUid(); //use userID as its identifier in database
            if (personalKey == null) {
                throw new RuntimeException("Failed to generate personal key");
            }
            userRef.child(personalKey).child("age").setValue(personal.getAge());
            userRef.child(personalKey).child("height").setValue(personal.getAge());
            userRef.child(personalKey).child("weight").setValue(personal.getAge());
            userRef.child(personalKey).child("gender").setValue(personal.getGender());
            Log.d("Success", String.format("Added %s to the db", personal));

        } catch (Exception e) {
            Log.d("Failure", "Edit Personal Info failure due to: "
                    + e.getLocalizedMessage());
            return new GreenPlateStatus(false, "Edit Personal Info: "
                    + e.getLocalizedMessage());
        }
        return new GreenPlateStatus(true,
                String.format("%s added to database successfully", personal));
    }
    public boolean validatePersonalInformation(String age, String height, String weight,
                                               String gender, EditText ageField,
                                               EditText heightField, EditText weightField,
                                               RadioGroup genderField) {
        boolean valid = true;
        if (age.trim().isEmpty()) {
            ageField.setError("Age cannot be empty.");
            valid = false;
        }
        if (height.trim().isEmpty()) {
            heightField.setError("Height cannot be empty.");
            valid = false;
        }
        if (weight.trim().isEmpty()) {
            weightField.setError("Weight cannot be empty.");
            valid = false;
        }
        if (genderField.getCheckedRadioButtonId() == -1) {
            valid = false;
        }
        return valid;
    }
    public void getUserHeight(TextView view) {
        try {
            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("UserInfoViewModel: There's no user signed in.");
            }
            userRef = database.getReference("user").child(currentUser.getUid())
                    .child("information").child("height");
            userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
            Log.d("Issue", "UserInfoViewModel: " + e.getLocalizedMessage());
        }
    }

    public void getUserWeight(TextView view) {
        try {
            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("UserInfoViewModel: There's no user signed in.");
            }
            userRef = database.getReference("user").child(currentUser.getUid())
                    .child("information").child("weight");
            userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
            Log.d("Issue", "UserInfoViewModel: " + e.getLocalizedMessage());
        }
    }

    public void getUserAge(TextView view) {
        try {
            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("UserInfoViewModel: There's no user signed in.");
            }
            userRef = database.getReference("user").child(currentUser.getUid())
                    .child("information").child("age");
            userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
            Log.d("Issue", "UserInfoViewModel: " + e.getLocalizedMessage());
        }
    }
    public void getUserGender(TextView view) {
        try {
            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("UserInfoViewModel: There's no user signed in.");
            }
            userRef = database.getReference("user").child(currentUser.getUid())
                    .child("information").child("gender");
            userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
            Log.d("Issue", "UserInfoViewModel: " + e.getLocalizedMessage());
        }
    }
}
