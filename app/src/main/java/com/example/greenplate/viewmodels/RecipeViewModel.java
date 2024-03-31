package com.example.greenplate.viewmodels;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.viewmodels.managers.CookbookManager;
import com.example.greenplate.views.EnterNewRecipeActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeViewModel extends ViewModel {
    private CookbookManager cookbookManager;

    public RecipeViewModel() {
        cookbookManager = new CookbookManager();

        // Add test recipe 1
        List<Ingredient> ingredients1 = new ArrayList<>();
        ingredients1.add(new Ingredient("Bun", 100, 2, null));
        ingredients1.add(new Ingredient("Hamburger Patty", 200, 1, null));
        List<String> instructions1 = new ArrayList<>();
        instructions1.add("Grill hamburger patty.");
        instructions1.add("Put hamburger patty between buns.");
        Recipe recipe1 = new Recipe("Hamburger", ingredients1, instructions1);
        addRecipe(recipe1);

        // Add test recipe 2
        List<Ingredient> ingredients2 = new ArrayList<>();
        ingredients2.add(new Ingredient("Bun", 100, 1, null));
        ingredients2.add(new Ingredient("Sausage", 100, 1, null));
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
                Log.d("Add recipe", "Successfully");
            }
        });
    }

    public GreenPlateStatus validateRecipeData(String recipeName, List<String> instructions, List<Ingredient> ingredients) {
        if (recipeName.trim().isEmpty()) {
            return new GreenPlateStatus(false, "Recipe name cannot be empty");
        }

        if (instructions.isEmpty()) {
            return new GreenPlateStatus(false, "At least one instruction is required");
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
}
