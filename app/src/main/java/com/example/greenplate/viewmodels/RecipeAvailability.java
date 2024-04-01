package com.example.greenplate.viewmodels;

import com.example.greenplate.models.Recipe;

public class RecipeAvailability {
    Recipe recipe;
    String availability;

    RecipeAvailability(Recipe recipe, String availability) {
        this.recipe = recipe;
        this.availability = availability;
    }
}
