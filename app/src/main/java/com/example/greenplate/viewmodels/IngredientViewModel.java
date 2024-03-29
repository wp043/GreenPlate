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
        calendar.set(Calendar.DAY_OF_MONTH, 21);

        // Set the hour, minute, second, and millisecond to zero
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Ingredient newIngredient = new Ingredient(
                "Test Ingredient 2",
                125,
                2,
                calendar.getTime()
        );
        Ingredient updatedIngredient = new Ingredient(
                "Test Ingredient",
                125,
                3,
                calendar.getTime()
        );
        addIngredient(newIngredient);
        addIngredient(updatedIngredient);
        updateIngredient(updatedIngredient);
        removeIngredient(updatedIngredient);
     */

    /**
     * Constructor for IngredientViewModel
     */
    public IngredientViewModel() {
        pantryManager = new PantryManager();
    }

    // TODO: This method can take a UI component as extra input, which will be updated accordingly.
//    public void addIngredient(Ingredient ingredient) {
//        pantryManager.isIngredientDuplicate(ingredient, isDuplicate -> {
//            if (isDuplicate) {
//                Log.d("Information", "Duplicate found");
//            } else {
//                pantryManager.addIngredient(ingredient);
//                Log.d("Information", "Ingredient added");
//            }
//        });
//    }
    public void addIngredient(Ingredient ingredient, OnIngredientUpdatedListener listener) {
        pantryManager.isIngredientDuplicate(ingredient, isDuplicate -> {
            if (isDuplicate) {
                Log.d("Information", "Duplicate found");
                listener.onIngredientUpdated(false);
            } else {
                pantryManager.addIngredient(ingredient, success -> {
                    if (success){
                        Log.d("Information", "Ingredient added");
                        listener.onIngredientUpdated(true);
                    } else{
                        listener.onIngredientUpdated(false);
                    }
                });

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

    /**
     *
     * @param callback
     */
    public void getIngredients(OnDataRetrievedCallback callback){
//        List<Ingredient> ingredientList = new ArrayList<>();
//        pantryManager.retrieve(items -> {
//            if (items != null) {
//                // Do something with the retrieved ingredients
//                for (RetrievableItem item : items) {
//                    if (item instanceof Ingredient) {
//                        Ingredient ingredient = (Ingredient) item;
//                        // Add the ingredient to the list
//                        ingredientList.add(ingredient);
//                    }
//                }
//            }
//        });
//        return ingredientList;
        pantryManager.retrieve(callback);

    }
}