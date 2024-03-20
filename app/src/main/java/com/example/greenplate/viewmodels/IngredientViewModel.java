package com.example.greenplate.viewmodels;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.viewmodels.listeners.OnIngredientRemoveListener;
import com.example.greenplate.viewmodels.listeners.OnMultiplicityUpdateListener;


public class IngredientViewModel extends ViewModel {
    private PantryManager pantryManager;

    /*
        Test w/o UI: insert in the constructor to test
        pantryManager.retrieve(items -> {
            if (items != null) {
                Log.d("Items", items.toString());
            }
        });
        Calendar calendar = Calendar.getInstance();

        // Set the year, month, and day
        calendar.set(Calendar.YEAR, 2024);
        calendar.set(Calendar.MONTH, Calendar.MARCH);
        calendar.set(Calendar.DAY_OF_MONTH, 19);

        // Set the hour, minute, second, and millisecond to zero
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Get the Date object from the Calendar
        Date date = calendar.getTime();

        Ingredient newIngredient = new Ingredient(
                "Test Ingredient 2",
                125,
                2,
                date
        );
        Ingredient updatedIngredient = new Ingredient(
                "Test Ingredient 2",
                125,
                3,
                date
        );
        updateIngredient(newIngredient);
        removeIngredient(newIngredient);
     */

    /**
     * Constructor for IngredientViewModel
     */
    public IngredientViewModel() {
        pantryManager = new PantryManager();
    }

    // TODO: This method can take a UI component as extra input, which will be updated accordingly.
    public void addIngredient(Ingredient ingredient) {
        pantryManager.isIngredientDuplicate(ingredient, isDuplicate -> {
            if (isDuplicate) {
                Log.d("Information", "Duplicate found");
            } else {
                pantryManager.addIngredient(ingredient);
                Log.d("Information", "Ingredient added");
            }
        });
    }

    // TODO: This method can take a UI component as extra input, which will be updated accordingly.
    public void updateIngredient(Ingredient ingredient) {
        pantryManager.updateIngredientMultiplicity(ingredient, new OnMultiplicityUpdateListener() {
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

    // TODO: This method can take a UI component as extra input, which will be updated accordingly.
    public void removeIngredient(Ingredient ingredient) {
        pantryManager.removeIngredient(ingredient, new OnIngredientRemoveListener() {
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
}