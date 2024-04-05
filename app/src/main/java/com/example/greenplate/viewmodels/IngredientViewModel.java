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

public class IngredientViewModel extends ViewModel {
    private PantryManager pantryManager;

    /**
     * Constructor for IngredientViewModel
     */
    public IngredientViewModel() {
        pantryManager = new PantryManager();
    }

    public void addIngredient(Ingredient ingredient, OnIngredientUpdatedListener listener) {
        pantryManager.isIngredientDuplicate(ingredient, (isDuplicate, duplicateName) -> {
            if (isDuplicate) {
                Log.d("Information", "Duplicate found");
                listener.onIngredientUpdated(false);
            } else {
                pantryManager.addIngredient(ingredient, success -> {
                    if (success) {
                        Log.d("Information", "Ingredient added");
                        listener.onIngredientUpdated(true);
                    } else {
                        listener.onIngredientUpdated(false);
                    }
                });

            }
        });
    }

    // updated accordingly.
    public void updateIngredient(Ingredient ingredient, OnIngredientUpdatedListener listener) {
        pantryManager.updateIngredientMultiplicity(ingredient.getName(),
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