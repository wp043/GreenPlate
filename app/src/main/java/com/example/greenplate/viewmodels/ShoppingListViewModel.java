package com.example.greenplate.viewmodels;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.RetrievableItem;
import com.example.greenplate.viewmodels.listeners.OnDataRetrievedCallback;
import com.example.greenplate.viewmodels.listeners.OnDuplicateCheckListener;
import com.example.greenplate.viewmodels.listeners.OnIngredientRemoveListener;
import com.example.greenplate.viewmodels.listeners.OnIngredientUpdatedListener;
import com.example.greenplate.viewmodels.listeners.OnMultiplicityUpdateListener;
import com.example.greenplate.viewmodels.managers.ShoppingListManager;

import java.util.List;
import java.util.stream.Collectors;

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
                this.updateIngredient(ingredient, listener);
            } else {
                shoppingListManager.addIngredient(ingredient, listener);
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
                        listener.onIngredientUpdated(true, status.getMessage());
                    }

                    @Override
                    public void onMultiplicityUpdateFailure(GreenPlateStatus status) {
                        Log.d("Failure", status.getMessage());
                        listener.onIngredientUpdated(false, status.getMessage());
                    }
                });
    }

    // updated accordingly.
    public void removeIngredient(Ingredient ingredient) {
        shoppingListManager.removeIngredient(ingredient.getName(),
                new OnIngredientRemoveListener() {
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

    public void isItemDuplicate(String name, OnDuplicateCheckListener listener) {
        getIngredients(items -> {
            boolean isDup = false;
            RetrievableItem dupItem = null;
            for (RetrievableItem item : items) {
                Log.d("Ava", item.getName() + ", " + name);
                if (item.getName().equals(name)) {
                    isDup = true;
                    dupItem = item;
                }
            }
            listener.onDuplicateCheckCompleted(isDup, dupItem);
        });
    }

    public void updateMultiplicity(String ingredientName, double updatedMultiplicity) {
        shoppingListManager.updateIngredientMultiplicity(ingredientName, updatedMultiplicity, new OnMultiplicityUpdateListener() {
            @Override
            public void onMultiplicityUpdateSuccess(GreenPlateStatus status) {
                Log.d("Success", status.getMessage());
            }

            @Override
            public void onMultiplicityUpdateFailure(GreenPlateStatus status) {
                Log.d("Failure", status.getMessage());
            }
        });
    }
}