package com.example.greenplate.viewmodels.managers;

import android.text.TextUtils;
import android.util.Log;

import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.viewmodels.listeners.OnDataRetrievedCallback;
import com.example.greenplate.viewmodels.listeners.OnDuplicateCheckListener;
import com.example.greenplate.viewmodels.listeners.OnIngredientUpdatedListener;
import com.example.greenplate.viewmodels.listeners.OnMultiplicityUpdateListener;
import com.example.greenplate.viewmodels.listeners.OnIngredientRemoveListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.example.greenplate.models.RetrievableItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PantryManager implements Manager {
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseUser currentUser;

    /**
     * Constructor for PantryManager.
     */
    public PantryManager() {
        try {
            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("PantryManager: There's no user signed in.");
            }
            myRef = database.getReference("user").child(currentUser.getUid())
                    .child("pantries");
        } catch (Exception e) {
            Log.d("Issue", "PantryManager: " + e.getLocalizedMessage());
        }
    }

    @Override
    public void retrieve(OnDataRetrievedCallback callback) {
        List<RetrievableItem> items = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    try {
                        String name = childSnapshot.child("name")
                                .getValue(String.class);
                        double calories = childSnapshot.child("calories")
                                .getValue(Double.class);
                        double multiplicity = childSnapshot.child("multiplicity")
                                .getValue(Double.class);
                        String expirationDateString = childSnapshot
                                .child("expirationDate").getValue(String.class);
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                        Date expirationDate = sdf.parse(expirationDateString);
                        Ingredient ingredient = new Ingredient(name, calories, multiplicity,
                                expirationDate);
                        items.add(ingredient);
                    } catch (Exception e) {
                        Log.d("Issue", "PantryManager failed to read from db for "
                                + e.getLocalizedMessage());
                    }
                }
                callback.onDataRetrieved(items);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("Issue", "PantryManager: error in querying the db.");
                callback.onDataRetrieved(null);
            }
        });
    }

    /**
     * Add an ingredient to the database.
     * 
     * @param ingredient - the ingredient to add
     * @param listener - listener
     */
    public void addIngredient(Ingredient ingredient, OnIngredientUpdatedListener
            listener) {
        if (ingredient == null) {
            listener.onIngredientUpdated(false, "Can't add a null ingredient.");
            return;
        }
        if (ingredient.getName() == null || TextUtils.isEmpty(ingredient.getName().trim())) {
            listener.onIngredientUpdated(false,
                    "Can't add a ingredient with empty name.");
            return;
        }
        if (ingredient.getCalories() <= 0) {
            listener.onIngredientUpdated(false,
                    "Can't add a ingredient with non-positive calorie.");
            return;
        }
        if (ingredient.getMultiplicity() <= 0) {
            listener.onIngredientUpdated(false,
                    "Can't add a ingredient with non-positive multiplicity.");
            return;
        }
        try {
            String ingredientKey = myRef.push().getKey();
            if (ingredientKey == null) {
                throw new RuntimeException("Failed to generate meal key");
            }
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date date = ingredient.getExpirationDate();
            String expDate = dateFormat.format(date);
            myRef.child(ingredientKey).child("name").setValue(ingredient.getName());
            myRef.child(ingredientKey).child("calories").setValue(ingredient.getCalories());
            myRef.child(ingredientKey).child("multiplicity").setValue(ingredient.getMultiplicity());
            myRef.child(ingredientKey).child("expirationDate").setValue(expDate);
            listener.onIngredientUpdated(true, "Successfully added the ingredient.");
        } catch (Exception e) {
            Log.d("Failure", "PantryManager failure due to: " + e.getLocalizedMessage());
            listener.onIngredientUpdated(false, "An unknown error happened.");
        }
    }

    /**
     * Check whether the input ingredient is duplicate.
     * 
     * @param ingredient - the ingredient to check
     * @param listener   - the listener to update
     */
    public void isIngredientDuplicate(Ingredient ingredient, OnDuplicateCheckListener listener) {
        retrieve(items -> {
            int idx = items.indexOf(ingredient);
            boolean isDuplicate = idx != -1;
            RetrievableItem duplicate = isDuplicate ? items.get(idx) : null;
            listener.onDuplicateCheckCompleted(isDuplicate, duplicate);
        });
    }

    /**
     * Check whether the input ingredient is duplicate.
     *
     * @param ingredient - the ingredient to check
     * @param listener   - the listener to update
     */
    public void isWrongCalorie(Ingredient ingredient, OnDuplicateCheckListener listener) {
        retrieve(items -> {
            RetrievableItem duplicate = items.stream()
                    .filter(item -> item.getName().equals(ingredient.getName())
                            && item.getCalories() != ingredient.getCalories())
                    .findFirst()
                    .orElse(null);
            boolean isDuplicate = duplicate != null;
            listener.onDuplicateCheckCompleted(isDuplicate, duplicate);
        });
    }


    public void updateIngredientMultiplicity(Ingredient updated, double updatedMultiplicity,
            OnMultiplicityUpdateListener listener) {
        if (updated == null) {
            listener.onMultiplicityUpdateFailure(
                    new GreenPlateStatus(false, "Can't update null ingredient."));
            return;
        }

        if (updatedMultiplicity < 0) {
            listener.onMultiplicityUpdateFailure(
                    new GreenPlateStatus(false,
                            "Can't update ingredient with negative multiplicity."));
            return;
        }

        if (updatedMultiplicity == 0) {
            this.removeIngredient(updated, new OnIngredientRemoveListener() {
                @Override
                public void onIngredientRemoveSuccess(GreenPlateStatus status) {
                    listener.onMultiplicityUpdateSuccess(status);
                }

                @Override
                public void onIngredientRemoveFailure(GreenPlateStatus status) {
                    listener.onMultiplicityUpdateFailure(
                            new GreenPlateStatus(false, status.getMessage()));
                }
            });
            return;
        }

        Query query = myRef.orderByChild("name").equalTo(updated.getName());
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = sdf.format(updated.getExpirationDate());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean ingredientFound = false;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String name = childSnapshot.child("name").getValue(String.class);
                    String expirationDateString = childSnapshot.child("expirationDate")
                            .getValue(String.class);

                    if (name.equals(updated.getName())
                            && expirationDateString.equals(formattedDate)) {
                        String key = childSnapshot.getKey();
                        myRef.child(key).child("multiplicity")
                                .setValue(updatedMultiplicity)
                                .addOnSuccessListener(e -> listener.onMultiplicityUpdateSuccess(
                                        new GreenPlateStatus(true,
                                                String.format("Successful update %s.",
                                                        updated))))
                                .addOnFailureListener(e -> listener.onMultiplicityUpdateFailure(
                                        new GreenPlateStatus(false, e.getMessage())));
                        ingredientFound = true;
                        break;
                    }
                }
                if (!ingredientFound) {
                    listener.onMultiplicityUpdateFailure(
                            new GreenPlateStatus(false,
                                    "Can't find the ingredient to update"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onMultiplicityUpdateFailure(
                        new GreenPlateStatus(false, "Issue with DB Query"));
            }
        });
    }

    public void removeIngredient(Ingredient toRemove, OnIngredientRemoveListener listener) {

        Query query = myRef.orderByChild("name").equalTo(toRemove.getName());
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = sdf.format(toRemove.getExpirationDate());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean ingredientFound = false;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String name = childSnapshot.child("name").getValue(String.class);
                    String expirationDateString = childSnapshot.child("expirationDate")
                            .getValue(String.class);
                    if (name.equals(toRemove.getName())
                            && expirationDateString.equals(formattedDate)) {
                        String key = childSnapshot.getKey();
                        myRef.child(key).removeValue()
                                .addOnSuccessListener(e -> listener.onIngredientRemoveSuccess(
                                        new GreenPlateStatus(true,
                                                String.format("Successfully removed %s",
                                                        toRemove))))
                                .addOnFailureListener(e -> listener.onIngredientRemoveFailure(
                                        new GreenPlateStatus(false, e.getMessage())));
                        ingredientFound = true;
                        break;
                    }
                }
                if (!ingredientFound) {
                    listener.onIngredientRemoveFailure(
                            new GreenPlateStatus(false,
                                    "Can't remove a non-existing ingredient"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onIngredientRemoveFailure(new GreenPlateStatus(false,
                        databaseError.getMessage()));
            }
        });
    }
}