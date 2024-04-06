package com.example.greenplate.viewmodels;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.viewmodels.listeners.OnDataRetrievedCallback;
import com.example.greenplate.viewmodels.listeners.OnIngredientRemoveListener;
import com.example.greenplate.viewmodels.listeners.OnIngredientUpdatedListener;
import com.example.greenplate.viewmodels.listeners.OnMultiplicityUpdateListener;
import com.example.greenplate.viewmodels.managers.PantryManager;
import com.example.greenplate.viewmodels.managers.ShoppingListManager;

public class ShoppingListViewModel extends ViewModel {
    private ShoppingListManager shoppingListManager;

    /**
     * Constructor for IngredientViewModel
     */
    public ShoppingListViewModel() {
        shoppingListManager = new ShoppingListManager();
    }

    public void addIngredient(Ingredient ingredient, OnIngredientUpdatedListener listener) {
        shoppingListManager.isIngredientDuplicate(ingredient, (isDuplicate, duplicateName) -> {
            if (isDuplicate) {
                Log.d("Information", "Duplicate found");
//                shoppingListManager.updateIngredientMultiplicity(ingredient.getName(),ingredient.getMultiplicity(),);
                this.updateIngredient(ingredient, new OnIngredientUpdatedListener() {
                    @Override
                    public void onIngredientUpdated(boolean success) {
                        if (success) {
                            // Update was successful
                            // Handle success scenario
                            Log.d("IngredientUpdate", "Ingredient updated successfully.");
                        } else {
                            // Update failed
                            // Handle failure scenario
                            Log.d("IngredientUpdate", "Failed to update ingredient.");
                        }
                    }
                });
                listener.onIngredientUpdated(true);
            } else {
                shoppingListManager.addIngredient(ingredient, success -> {
                    Log.d("TAG", "1");
                    if (success) {
                        Log.d("Information", "Ingredient added");
                        Log.d("TAG", "2");
                        listener.onIngredientUpdated(true);
                    } else {
                        listener.onIngredientUpdated(false);
                        Log.d("TAG", "3");
                    }
                });

            }
        });
    }

    // updated accordingly.
    public void updateIngredient(Ingredient ingredient, OnIngredientUpdatedListener listener) {
        shoppingListManager.updateIngredientMultiplicity(ingredient.getName(),
                ingredient.getMultiplicity(), new OnMultiplicityUpdateListener() {
                    @Override
                    public void onMultiplicityUpdateSuccess(GreenPlateStatus status) {
                        Log.d("Success", status.getMessage());
                        listener.onIngredientUpdated(true);
                    }

                    @Override
                    public void onMultiplicityUpdateFailure(GreenPlateStatus status) {
                        Log.d("Failure", status.getMessage());
                        listener.onIngredientUpdated(false);
                    }
                });
    }


    // updated accordingly.
    public void removeIngredient(Ingredient ingredient) {
        shoppingListManager.removeIngredient(ingredient.getName(), new OnIngredientRemoveListener() {
            @Override
            public void onIngredientRemoveSuccess(GreenPlateStatus status) {
                Log.d("Success", status.getMessage());
            }

            @Override
            public void onIngredientRemoveFailure(GreenPlateStatus status) {
                Log.d("Failure", status.getMessage());
            }
        });
    }

    /**
     * get all ingredients in the pantry
     * 
     * @param callback callback
     */
    public void getIngredients(OnDataRetrievedCallback callback) {
        shoppingListManager.retrieve(callback);

    }
}