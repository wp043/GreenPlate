
package com.example.greenplate.viewmodels;

import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Personal;
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
     *         height: 
     *         weight:
     *         gender:
     */
    public UserInfoViewModel() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference("user")
                    .child(currentUser.getUid()); 
            // Fetch the personal information every time PersonalActivity is navigated to
            fetchPersonalInformation(); 
        } else {
            throw new RuntimeException("UserInfoViewModel: There's no user signed in.");
        }
    }

    private void fetchPersonalInformation() {
        //this func is called anytime new data is added to the database
        // Fetch the personal information from Firebase and post the value to userPersonalInfo LiveData
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null) {
                    String height = dataSnapshot.child("height").getValue(String.class);
                    String weight = dataSnapshot.child("weight").getValue(String.class);
                    String gender = dataSnapshot.child("gender").getValue(String.class);
                    Personal personal = new Personal(height, weight,gender);
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
        if (personal != null) {
            userRef.setValue(personal);
        }
        if (personal.getHeight() == null || TextUtils.isEmpty(personal.getHeight().trim())) {
            return new GreenPlateStatus(false,
                    "Edit personal information: can't have null height");
        }
        if (personal.getWeight() == null || TextUtils.isEmpty(personal.getWeight().trim())) {
            return new GreenPlateStatus(false,
                    "Edit personal information: can't have null weight");
        }
        if (personal.getWeight() == null || TextUtils.isEmpty(personal.getWeight().trim())) {
            return new GreenPlateStatus(false,
                    "Edit personal information: can't have null gender");
        }
        try {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("Signed-in user can't be found");
            }

            // String personalKey = userRef.push().getKey();
           String personalKey=currentUser.getUid(); //use userID as its identifier in database
            if (personalKey == null) {
                throw new RuntimeException("Failed to generate personal key");
            }
            userRef.child(personalKey).child("height").setValue(personal.getHeight());
            userRef.child(personalKey).child("weight").setValue(personal.getWeight());
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
    public boolean validatePersonalInformation(String height, String weight, String gender) {
        return !TextUtils.isEmpty(height) && !TextUtils.isEmpty(weight)
                && !TextUtils.isEmpty(gender);
    }
}
