package com.example.greenplate.viewmodels.managers;

import android.text.TextUtils;
import android.util.Log;

import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.RetrievableItem;
import com.example.greenplate.viewmodels.listeners.OnDataRetrievedCallback;
import com.example.greenplate.viewmodels.listeners.OnDuplicateCheckListener;
import com.example.greenplate.viewmodels.listeners.OnIngredientRemoveListener;
import com.example.greenplate.viewmodels.listeners.OnIngredientUpdatedListener;
import com.example.greenplate.viewmodels.listeners.OnMultiplicityUpdateListener;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShoppingListManager implements Manager {
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseUser currentUser;

    /**
     * Constructor for PantryManager.
     */
    public ShoppingListManager() {
        try {
            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("ShoppingListManager: There's no user signed in.");
            }
            myRef = database.getReference("user").child(currentUser.getUid())
                    .child("shopping list");
        } catch (Exception e) {
            Log.d("Issue", "ShoppingListManager: " + e.getLocalizedMessage());
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
                        double multiplicity = childSnapshot.child("multiplicity")
                                .getValue(Double.class);
                        Ingredient ingredient = new Ingredient(name, multiplicity);
                        items.add(ingredient);
                    } catch (Exception e) {
                        Log.d("Issue", "ShoppingListManager failed to read from db for "
                                + e.getLocalizedMessage());
                    }
                }
                callback.onDataRetrieved(items);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("Issue", "ShoppingListManager: error in querying the db.");
                callback.onDataRetrieved(null);
            }
        });
    }

    /**
     * Add an ingredient to the database.
     * 
     * @param ingredient - the ingredient to add
     * @param listener - listener
     * @return the status of the operation
     *
     */
    public void addIngredient(Ingredient ingredient, OnIngredientUpdatedListener
            listener) {
        if (ingredient == null) {
            listener.onIngredientUpdated(false, "Can't add a null ingredient.");
            return;
        }
        if (ingredient.getName() == null || TextUtils.isEmpty(ingredient.getName().trim())) {
            listener.onIngredientUpdated(false, "Can't add a ingredient with empty name.");
            return;
        }
        if (ingredient.getMultiplicity() <= 0) {
            listener.onIngredientUpdated(false, "Can't add a ingredient with non-positive multiplicity.");
            return;
        }
        try {
            String ingredientKey = myRef.push().getKey();
            if (ingredientKey == null) {
                throw new RuntimeException("Failed to generate meal key");
            }

            myRef.child(ingredientKey).child("name").setValue(ingredient.getName());
            myRef.child(ingredientKey).child("multiplicity").setValue(ingredient.getMultiplicity());
            listener.onIngredientUpdated(true, "Success");
        } catch (Exception e) {
            Log.d("Failure", "ShoppingListManager failure due to: " + e.getLocalizedMessage());
            listener.onIngredientUpdated(false, "An error occurred");
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
            boolean isDuplicate = false;
            RetrievableItem duplicate = null;
            for (RetrievableItem item : items) {
                if (item.equals(ingredient)) {
                    isDuplicate = true;
                    duplicate = item;
                    break;
                }
            }
            listener.onDuplicateCheckCompleted(isDuplicate, duplicate);
        });
    }


    public void updateIngredientMultiplicity(String ingredientName, double updatedMultiplicity,
            OnMultiplicityUpdateListener listener) {
        if (ingredientName == null) {
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
            this.removeIngredient(ingredientName, new OnIngredientRemoveListener() {
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

        Query query = myRef.orderByChild("name").equalTo(ingredientName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean ingredientFound = false;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String name = childSnapshot.child("name").getValue(String.class);

                    if (name.equals(ingredientName)) {
                        String key = childSnapshot.getKey();
                        myRef.child(key).child("multiplicity")
                                .setValue(updatedMultiplicity)
                                .addOnSuccessListener(e -> listener.onMultiplicityUpdateSuccess(
                                        new GreenPlateStatus(true,
                                                String.format("Successful update %s.",
                                                        ingredientName))))
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

    public void removeIngredient(String removedName, OnIngredientRemoveListener listener) {

        Query query = myRef.orderByChild("name").equalTo(removedName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean ingredientFound = false;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String name = childSnapshot.child("name").getValue(String.class);
                    if (name.equals(removedName)) {
                        String key = childSnapshot.getKey();
                        myRef.child(key).removeValue()
                                .addOnSuccessListener(e -> listener.onIngredientRemoveSuccess(
                                        new GreenPlateStatus(true,
                                                String.format("Successfully removed %s",
                                                        removedName))))
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