package com.example.greenplate.viewmodels.managers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.models.RetrievableItem;
import com.example.greenplate.viewmodels.listeners.OnDataRetrievedCallback;
import com.example.greenplate.viewmodels.listeners.OnDuplicateCheckListener;
import com.example.greenplate.viewmodels.listeners.OnRecipeAddedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CookbookManager implements Manager {
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseUser currentUser;


    /**
     * Constructor for PantryManager.
     */
    public CookbookManager() {
        try {
            database = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("CookbookManager: There's no user signed in.");
            }
            myRef = database.getReference();
        } catch (Exception e) {
            Log.d("Issue", "CookbookManager: " + e.getLocalizedMessage());
        }
    }

    @Override
    public void retrieve(OnDataRetrievedCallback callback) {
        List<RetrievableItem> items = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot cookbookSnapshot = dataSnapshot.child("Cookbook");
                for (DataSnapshot recipeSnapshot : cookbookSnapshot.getChildren()) {
                    try {
                        // Query name of recipe from database
                        String name = recipeSnapshot.getKey();

                        // Query ingredients of recipe from database
                        List<Ingredient> ingredients = new ArrayList<>();
                        DataSnapshot ingredientsSnapshot = recipeSnapshot.child("ingredients");
                        for (DataSnapshot ingredientSnapshot: ingredientsSnapshot.getChildren()) {
                            String ingredientName = ingredientSnapshot.child("name")
                                    .getValue(String.class);
                            double quantity = ingredientSnapshot.child("quantity")
                                    .getValue(Double.class);
                            ingredients.add(new Ingredient(ingredientName));
                        }

                        // Query instructions of recipe from database
                        List<String> instructions = new ArrayList<>();
                        DataSnapshot instructionsSnapshot = recipeSnapshot.child("instructions");
                        for (DataSnapshot instructionSnapshot: instructionsSnapshot.getChildren()) {
                            String instruction = instructionSnapshot.getValue(String.class);
                            instructions.add(instruction);
                        }

                        Recipe recipe = new Recipe(name, ingredients, instructions);
                        items.add(recipe);
                    } catch (Exception e) {
                        Log.d("Issue", "CookbookManager failed to read from db for "
                                + e.getLocalizedMessage());
                    }
                }
                callback.onDataRetrieved(items);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("Issue", "CookbookManager: error in querying the db.");
                callback.onDataRetrieved(null);
            }
        });
    }

    /**
     * Add a recipe to the database.
     * @param recipe - the recipe to add
     * @param listener - the listener to check when to add the recipe
     * @return the status of the operation
     */
    public GreenPlateStatus addRecipe(Recipe recipe, OnRecipeAddedListener listener) {
        try {
            DatabaseReference recipeRef = myRef.child("Cookbook").child(recipe.getName());

            // Add ingredients of recipe to database
            DatabaseReference ingredientsRef = recipeRef.child("ingredients");
            for (Ingredient ingredient: recipe.getIngredients()) {
                String ingredientKey = ingredientsRef.push().getKey();
                if (ingredientKey == null) {
                    throw new RuntimeException("Failed to generate ingredient key");
                }
                ingredientsRef.child(ingredientKey).child("name")
                        .setValue(ingredient.getName());
                ingredientsRef.child(ingredientKey).child("quantity")
                        .setValue((double) ingredient.getMultiplicity());
                ingredientsRef.child(ingredientKey).child("calories")
                        .setValue(ingredient.getCalories());
            }

            DatabaseReference instructionsRef = recipeRef.child("instructions");
            for (String instruction: recipe.getInstructions()) {
                String instructionKey = instructionsRef.push().getKey();
                if (instructionKey == null) {
                    throw new RuntimeException("Failed to generate instruction key");
                }
                instructionsRef.child(instructionKey).setValue(instruction);
            }

        } catch (Exception e) {
            Log.d("Failure", "CookbookManager failure due to: " + e.getLocalizedMessage());
            listener.onRecipeAdded(false);
            return new GreenPlateStatus(false, "Add meal: " + e.getLocalizedMessage());
        }
        listener.onRecipeAdded(true);
        return new GreenPlateStatus(true,
                String.format("%s recipe added to database successfully", recipe));
    }

    /**
     * Check whether the input recipe is duplicate.
     * @param recipe - the recipe to check
     * @param listener - the listener to update
     */
    public void isRecipeDuplicate(Recipe recipe, OnDuplicateCheckListener listener) {
        retrieve(items -> {
            boolean isDuplicate = false;
            for (RetrievableItem item : items) {
                if (item.getName().equals(recipe.getName())) {
                    isDuplicate = true;
                    break;
                }
            }
            listener.onDuplicateCheckCompleted(isDuplicate);
        });
    }
}
