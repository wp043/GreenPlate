package com.example.greenplate.viewmodels;

import com.example.greenplate.models.Recipe;

public class RecipeAvailability {
    private Recipe recipe;
    private String availability;

    RecipeAvailability(Recipe recipe, String availability) {
        this.recipe = recipe;
        this.availability = availability;
    }

    public Recipe getRecipe() {
        return this.recipe;
    }

    public String getAvailability() {
        return this.availability;
    }
}
