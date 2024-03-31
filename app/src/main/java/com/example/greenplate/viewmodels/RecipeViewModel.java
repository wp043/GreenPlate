package com.example.greenplate.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.models.RetrievableItem;
import com.example.greenplate.viewmodels.adapters.RecipesAdapter;
import com.example.greenplate.viewmodels.listeners.OnDataRetrievedCallback;
import com.example.greenplate.viewmodels.listeners.OnRecipeAddedListener;
import com.example.greenplate.viewmodels.managers.CookbookManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeViewModel extends ViewModel {
    private CookbookManager cookbookManager;

    public RecipeViewModel() {
        cookbookManager = new CookbookManager();
    }

    public void addDefaultRecipes(Context context, RecyclerView rvRecipes) {
        // Add test recipe 1
        Map<Ingredient, Integer> ingredients1 = new HashMap<>();
        ingredients1.put(new Ingredient("Bun"), 2);
        ingredients1.put(new Ingredient("Hamburger Patty"), 1);
        ingredients1.put(new Ingredient("Cheese Slice"), 1);
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
        Map<Ingredient, Integer> ingredients2 = new HashMap<>();
        ingredients2.put(new Ingredient("Bun"), 1);
        ingredients2.put(new Ingredient("Sausage"), 1);
        List<String> instructions2 = new ArrayList<>();
        instructions2.add("Grill sausage.");
        instructions2.add("Put sausage into bun.");
        Recipe recipe2 = new Recipe("Hot dog", ingredients2, instructions2);
        addRecipe(recipe2, success -> {
            // Update RecyclerView
            retrieveAndDisplayIngredients(context, rvRecipes);
        });
    }

    public void addRecipe(Recipe recipe, OnRecipeAddedListener listener) {
        // Check if recipe is already in Cookbook
        cookbookManager.isRecipeDuplicate(recipe, isDuplicate -> {
            if (isDuplicate) {
                Log.d("Failed to add recipe", "RecipeViewModel: "
                        + recipe.getName() + " recipe already exists.");
                listener.onRecipeAdded(false);
            } else {
                cookbookManager.addRecipe(recipe, success -> {
                    listener.onRecipeAdded(success);
                });
            }
        });
    }

    /**
     * get all recipes in the cookbook
     * @param callback Callback to retrieve recipes from the cookbookManager
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


    public void retrieveAndDisplayFiltered(Context context, RecyclerView rvRecipes, String search) {
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
            // Filter from search
            ArrayList<Recipe> filteredList = new ArrayList<>();
            if (search.isEmpty()) {
                // If the search query is empty, show the original list
                filteredList = new ArrayList<>(recipes);
            } else {
                filteredList = new ArrayList<>();
                for (Recipe recipeItem : recipes) {
                    if (recipeItem.getName().toLowerCase().contains(search.toLowerCase())) {
                        filteredList.add(recipeItem);
                    }
                }
            }
            filteredList.sort((r1, r2) -> r1.getName().compareToIgnoreCase(r2.getName()));
            // Use RecyclerView adapter to put list of recipes into RecyclerView (scrollable list)
            RecipesAdapter adapter = new RecipesAdapter(filteredList);
            rvRecipes.setAdapter(adapter);
            rvRecipes.setLayoutManager(new LinearLayoutManager(context));
        });
    }

}