package com.example.greenplate.viewmodels;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.models.RetrievableItem;
import com.example.greenplate.viewmodels.adapters.RecipesAdapter;
import com.example.greenplate.viewmodels.listeners.OnDataRetrievedCallback;
import com.example.greenplate.viewmodels.listeners.OnRecipeAddedListener;
import com.example.greenplate.viewmodels.managers.CookbookManager;
import com.example.greenplate.views.EnterNewRecipeActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeViewModel extends ViewModel {
    private CookbookManager cookbookManager;
    private boolean defaultRecipesInitialized = false;

    public RecipeViewModel() {
        cookbookManager = new CookbookManager();
    }

    public void addDefaultRecipes(Context context, RecyclerView rvRecipes) {
        // Add test recipe 1
        List<Ingredient> ingredients1 = new ArrayList<>();
        ingredients1.add(new Ingredient("Bun", 100, 2, null));
        ingredients1.add(new Ingredient("Hamburger Patty", 200, 1, null));
        ingredients1.add(new Ingredient("Cheese Slice", 50, 1, null));
        List<String> instructions1 = new ArrayList<>();
        instructions1.add("Grill hamburger patty.");
        instructions1.add("Put cheese slice onto hamburger.");
        instructions1.add("Put hamburger patty between buns.");
        Recipe recipe1 = new Recipe("Cheeseburger", ingredients1, instructions1);
        addRecipe(recipe1, success -> {
            // Update RecyclerView
            retrieveAndDisplayIngredients(context, rvRecipes);
        });

        // Add test recipe 2
        List<Ingredient> ingredients2 = new ArrayList<>();
        ingredients2.add(new Ingredient("Bun", 100, 1, null));
        ingredients2.add(new Ingredient("Sausage", 100, 1, null));
        List<String> instructions2 = new ArrayList<>();
        instructions2.add("Grill sausage.");
        instructions2.add("Put sausage into bun.");
        Recipe recipe2 = new Recipe("Hot dog", ingredients2, instructions2);
        addRecipe(recipe2, success -> {
            // Update RecyclerView
            retrieveAndDisplayIngredients(context, rvRecipes);
        });
        defaultRecipesInitialized = true;
    }

//    public void initializeDefaultRecipesIfNeeded(Context context, RecyclerView rvRecipes) {
//        if (!defaultRecipesInitialized) {
//            addDefaultRecipes(context, rvRecipes);
//            defaultRecipesInitialized = true;
//        }
//    }

    public void addRecipe(Recipe recipe, OnRecipeAddedListener listener) {
        if (recipe.getName() == null) {
            listener.onRecipeAdded(false);
            return;
        }
        if (recipe.getIngredients().isEmpty()) {
            listener.onRecipeAdded(false);
            return;
        }

        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient.getName() == null || ingredient.getName().trim().isEmpty() ||
                    ingredient.getMultiplicity() <= 0 || ingredient.getCalories() < 0) {
                listener.onRecipeAdded(false);
                return;
            }
        }

        cookbookManager.isRecipeDuplicate(recipe, isDuplicate -> {
            if (isDuplicate) {
                listener.onRecipeAdded(false);
            } else {
                cookbookManager.addRecipe(recipe, new OnRecipeAddedListener() {
                    @Override
                    public void onRecipeAdded(boolean success) {
                        listener.onRecipeAdded(success);
                    }
                });
            }
        });
    }

    public GreenPlateStatus validateRecipeData(String recipeName, List<String> instructions, List<Ingredient> ingredients) {
        if (recipeName.trim().isEmpty()) {
            return new GreenPlateStatus(false, "Recipe name cannot be empty");
        }

        boolean hasValidIngredient = true;
        int index = 0;
        for (Ingredient ingredient : ingredients) {
            if (ingredient.getName().trim().isEmpty() || ingredients.get(index).getMultiplicity() <= 0) {
                hasValidIngredient = false;
            }
            index++;
        }

        if (!hasValidIngredient) {
            return new GreenPlateStatus(false, "At least one ingredient with a valid name and quantity is required");
        }

        return new GreenPlateStatus(true, null);
    }
    /**
     * get all recipes in the cookbook
     * @param callback
     */
    public void getRecipes(OnDataRetrievedCallback callback) {
        cookbookManager.retrieve(callback);
    }


    public void retrieveAndDisplayIngredients(Context context, RecyclerView rvRecipes) {
        this.getRecipes(items -> {
            List<Recipe> recipes = new ArrayList<>();
            if (items != null) {
                for (RetrievableItem item : items) {
                    if (item instanceof Recipe) {
                        Recipe recipe = (Recipe) item;
                        recipes.add(recipe);
                    }
                }
            }

            // Use RecyclerView adapter to put list of recipes into RecyclerView (scrollable list)
            RecipesAdapter adapter = new RecipesAdapter(recipes);
            rvRecipes.setAdapter(adapter);
            rvRecipes.setLayoutManager(new LinearLayoutManager(context));
        });
    }
}
