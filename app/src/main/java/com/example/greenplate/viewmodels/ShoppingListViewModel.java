package com.example.greenplate.viewmodels;


import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.models.RetrievableItem;
import com.example.greenplate.viewmodels.adapters.RecipesAdapter;
import com.example.greenplate.viewmodels.helpers.AvailabilityReportGenerator;
import com.example.greenplate.viewmodels.listeners.OnDataRetrievedCallback;
import com.example.greenplate.viewmodels.listeners.OnMultiplicityUpdateListener;
import com.example.greenplate.viewmodels.listeners.OnRecipeAddedListener;
import com.example.greenplate.viewmodels.listeners.OnReportGeneratedCallback;
import com.example.greenplate.viewmodels.managers.CookbookManager;
import com.example.greenplate.viewmodels.managers.PantryManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


}