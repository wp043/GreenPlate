package com.example.greenplate.viewmodels;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.viewmodels.managers.CookbookManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeViewModel extends ViewModel {
    private CookbookManager cookbookManager;

    public RecipeViewModel() {
        cookbookManager = new CookbookManager();

        // Add test recipe 1
        Map<Ingredient, Integer> ingredients1 = new HashMap<>();
        ingredients1.put(new Ingredient("Bun"), 2);
        ingredients1.put(new Ingredient("Hamburger Patty"), 1);
        List<String> instructions1 = new ArrayList<>();
        instructions1.add("Grill hamburger patty.");
        instructions1.add("Put hamburger patty between buns.");
        Recipe recipe1 = new Recipe("Hamburger", ingredients1, instructions1);
        addRecipe(recipe1);

        // Add test recipe 2
        Map<Ingredient, Integer> ingredients2 = new HashMap<>();
        ingredients2.put(new Ingredient("Bun"), 1);
        ingredients2.put(new Ingredient("Sausage"), 1);
        List<String> instructions2 = new ArrayList<>();
        instructions2.add("Grill sausage.");
        instructions2.add("Put sausage into bun.");
        Recipe recipe2 = new Recipe("Hot dog", ingredients2, instructions2);
        addRecipe(recipe2);
    }

    public void addRecipe(Recipe recipe) {
        // Check if recipe is already in Cookbook
        cookbookManager.isRecipeDuplicate(recipe, isDuplicate -> {
            if (isDuplicate) {
                Log.d("Failed to add recipe", "RecipeViewModel: "
                        + recipe.getName() + " recipe already exists.");
            } else {
                cookbookManager.addRecipe(recipe);
            }
        });
    }
}
