package com.example.greenplate.viewmodels;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.RetrievableItem;
import com.example.greenplate.viewmodels.listeners.OnDataRetrievedCallback;
import com.example.greenplate.viewmodels.listeners.OnIngredientRemoveListener;
import com.example.greenplate.viewmodels.listeners.OnIngredientUpdatedListener;
import com.example.greenplate.viewmodels.listeners.OnMultiplicityUpdateListener;
import com.example.greenplate.viewmodels.managers.PantryManager;
import com.example.greenplate.viewmodels.listeners.OnDuplicateCheckListener;

public class IngredientViewModel extends ViewModel {
    private PantryManager pantryManager;

    /**
     * Constructor for IngredientViewModel
     */
    public IngredientViewModel() {
        pantryManager = new PantryManager();
    }

    public void addIngredient(Ingredient ingredient, OnIngredientUpdatedListener listener) {
        pantryManager.isWrongCalorie(ingredient, (isWrongCalorie, dup) -> {
            if (isWrongCalorie) {
                listener.onIngredientUpdated(false, "Ingredients of "
                        + "the same name must have the same calorie.");
                return;
            }
            pantryManager.isIngredientDuplicate(ingredient, (isDuplicate, duplicateName) -> {
                if (isDuplicate) {
                    listener.onIngredientUpdated(false, "You can't add a "
                            + "duplicate ingredient.");
                    return;
                }
                pantryManager.addIngredient(ingredient, listener);
            });
        });
    }

    public void addIngredientFromShoppingList(Ingredient ingredient, OnIngredientUpdatedListener listener) {
        pantryManager.isIngredientDuplicate(ingredient, (isDuplicate, duplicateItem) -> {
            if (!isDuplicate) {
                pantryManager.addIngredient(ingredient, listener);
                return;
            }
            duplicateItem.setMultiplicity(duplicateItem.getMultiplicity()
                    + ingredient.getMultiplicity());
            updateIngredient((Ingredient) duplicateItem, listener);
        });
    }

    // updated accordingly.
    public void updateIngredient(Ingredient ingredient, OnIngredientUpdatedListener listener) {
        pantryManager.updateIngredientMultiplicity(ingredient,
                ingredient.getMultiplicity(), new OnMultiplicityUpdateListener() {
                    @Override
                    public void onMultiplicityUpdateSuccess(GreenPlateStatus status) {
                        listener.onIngredientUpdated(true, "Successful update");
                    }

                    @Override
                    public void onMultiplicityUpdateFailure(GreenPlateStatus status) {
                        listener.onIngredientUpdated(false, "Error during update");
                    }
                });
    }

    /**
     * get all ingredients in the pantry
     * 
     * @param callback callback
     */
    public void getIngredients(OnDataRetrievedCallback callback) {
        pantryManager.retrieve(callback);
    }
}