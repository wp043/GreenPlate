package com.example.greenplate.viewmodels;

import android.util.Log;

import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.viewmodels.listeners.OnDataRetrievedCallback;
import com.example.greenplate.viewmodels.listeners.OnDuplicateCheckListener;
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
                                .getValue(Integer.class);
                        int multiplicity = childSnapshot.child("multiplicity")
                                .getValue(Integer.class);
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
     * @param ingredient - the ingredient to add
     * @return the status of the operation
     */
    public GreenPlateStatus addIngredient(Ingredient ingredient) {
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
        } catch (Exception e) {
            Log.d("Failure", "PantryManager failure due to: " + e.getLocalizedMessage());
            return new GreenPlateStatus(false, "Add meal: " + e.getLocalizedMessage());
        }
        return new GreenPlateStatus(true,
                String.format("%s added to database successfully", ingredient));
    }

    /**
     * Check whether the input ingredient is duplicate.
     * @param ingredient - the ingredient to check
     * @param listener - the listener to update
     */
    public void isIngredientDuplicate(Ingredient ingredient, OnDuplicateCheckListener listener) {
        retrieve(items -> {
            boolean isDuplicate = false;
            for (RetrievableItem item : items) {
                if (item.equals(ingredient)) {
                    isDuplicate = true;
                    break;
                }
            }
            listener.onDuplicateCheckCompleted(isDuplicate);
        });
    }

    /**
     * Update the multiplicity of the input ingredient.
     * @param ingredient - the ingredient to update
     * @param listener - the listener to update
     */
    public void updateIngredientMultiplicity(Ingredient ingredient,
                                             OnMultiplicityUpdateListener listener) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = sdf.format(ingredient.getExpirationDate());

        Query query = myRef.orderByChild("name").equalTo(ingredient.getName());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean ingredientFound = false;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String name = childSnapshot.child("name").getValue(String.class);
                    double calories = childSnapshot.child("calories").getValue(Double.class);
                    String expirationDateString = childSnapshot.child("expirationDate")
                            .getValue(String.class);

                    if (calories == ingredient.getCalories()
                            && name.equals(ingredient.getName())
                            && expirationDateString.equals(formattedDate)) {
                        String key = childSnapshot.getKey();
                        myRef.child(key).child("multiplicity")
                                .setValue(ingredient.getMultiplicity())
                                .addOnSuccessListener(e -> listener.onMultiplicityUpdateSuccess(
                                        new GreenPlateStatus(true,
                                                String.format("Successful update %s.",
                                                        ingredient))))
                                .addOnFailureListener(e -> listener.onMultiplicityUpdateFailure(
                                        new GreenPlateStatus(false, e.getMessage()
                                        )));
                        ingredientFound = true;
                        break;
                    }
                }
                if (!ingredientFound) {
                    listener.onMultiplicityUpdateFailure(
                            new GreenPlateStatus(false,
                                    "Can't find the ingredient to update")
                    );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onMultiplicityUpdateFailure(
                        new GreenPlateStatus(false, "Issue with DB Query")
                );
            }
        });
    }

    /**
     * Remove the input ingredient from the db.
     * @param ingredient - the ingredient to remove
     * @param listener - the listener to update
     */
    public void removeIngredient(Ingredient ingredient, OnIngredientRemoveListener listener) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = sdf.format(ingredient.getExpirationDate());

        Query query = myRef.orderByChild("name").equalTo(ingredient.getName());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean ingredientFound = false;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String name = childSnapshot.child("name").getValue(String.class);
                    double calories = childSnapshot.child("calories").getValue(Double.class);
                    String expirationDateString = childSnapshot.child("expirationDate")
                            .getValue(String.class);

                    if (calories == ingredient.getCalories()
                            && name.equals(ingredient.getName())
                            && expirationDateString.equals(formattedDate)) {
                        String key = childSnapshot.getKey();
                        myRef.child(key).removeValue()
                            .addOnSuccessListener(e -> listener.onIngredientRemoveSuccess(
                                            new GreenPlateStatus(true,
                                                    String.format("Successfully removed %s",
                                                            ingredient))
                                    )
                            ).addOnFailureListener(e ->
                                    listener.onIngredientRemoveFailure(
                                            new GreenPlateStatus(false, e.getMessage())
                                    )
                            );
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
