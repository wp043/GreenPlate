package com.example.greenplate.viewmodels;


import androidx.lifecycle.ViewModel;

import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.viewmodels.helpers.AvailabilityReportGenerator;
import com.example.greenplate.viewmodels.listeners.OnMultiplicityUpdateListener;
import com.example.greenplate.viewmodels.managers.CookbookManager;
import com.example.greenplate.viewmodels.managers.PantryManager;

import java.util.List;

public class ShoppingListViewModel extends ViewModel {

    private PantryManager pantryManager;
    private CookbookManager cookbookManager;
    private List<Ingredient> ingredientsInShoppingCart;


    public ShoppingListViewModel() {
        pantryManager = new PantryManager();
        cookbookManager = new CookbookManager();
        AvailabilityReportGenerator.getInstance().getMissingElementsForShopping(
                AvailabilityReportGenerator::logReport);
    }

    public void addToShoppingCart(Ingredient toAdd) {
        if (ingredientsInShoppingCart.contains(toAdd)) {
            // TODO: Give an error message
            return;
        }
        ingredientsInShoppingCart.add(toAdd);
    }

    private void purchaseIngredient(Ingredient newItem) {
        pantryManager.isIngredientDuplicate(newItem, (isDuplicate, duplicate) -> {
            if (isDuplicate) {
                pantryManager.updateIngredientMultiplicity(duplicate.getName(),
                        duplicate.getMultiplicity() + newItem.getMultiplicity(),
                        new OnMultiplicityUpdateListener() {
                            @Override
                            public void onMultiplicityUpdateSuccess(GreenPlateStatus status) {
                                // TODO
                            }

                            @Override
                            public void onMultiplicityUpdateFailure(GreenPlateStatus status) {
                                // TODO
                            }
                        });
                return;
            }
            pantryManager.addIngredient(newItem, listener -> {
                // TODO
            });
        });
    }

    public void purchaseItems(List<Ingredient> items) {
        items.forEach(this::purchaseIngredient);
    }

    public void removeFromCart(Ingredient toRemove) {
        ingredientsInShoppingCart.remove(toRemove);
    }
}