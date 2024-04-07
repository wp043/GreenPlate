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
        pantryManager.isIngredientDuplicate(ingredient, (isDuplicate, duplicateName) -> {
            if (!isDuplicate) {
                pantryManager.addIngredient(ingredient, listener);
                return;
            }
            pantryManager.retrieve(items -> {
                RetrievableItem duplicate = null;
                for (RetrievableItem item : items) {
                    if (item.equals(ingredient)) {
                        duplicate = item;
                        break;
                    }
                }
                duplicate.setMultiplicity(duplicate.getMultiplicity() + ingredient.getMultiplicity());
                updateIngredient((Ingredient) duplicate, listener);
            });
        });
    }

    // updated accordingly.
    public void updateIngredient(Ingredient ingredient, OnIngredientUpdatedListener listener) {
        pantryManager.updateIngredientMultiplicity(ingredient.getName(),
                ingredient.getMultiplicity(), new OnMultiplicityUpdateListener() {
                    @Override
                    public void onMultiplicityUpdateSuccess(GreenPlateStatus status) {
                        listener.onIngredientUpdated(true, "Successful update");
                    }

                    @Override
                    public void onMultiplicityUpdateFailure(GreenPlateStatus status) {
                        Log.d("Failure", status.getMessage());
                        listener.onIngredientUpdated(false, "Error during update");
                    }
                });
    }


    // updated accordingly.
    public void removeIngredient(Ingredient ingredient) {
        pantryManager.removeIngredient(ingredient.getName(), new OnIngredientRemoveListener() {
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
        pantryManager.retrieve(callback);

    }
}