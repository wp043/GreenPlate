package com.example.greenplate.viewmodels;

import java.util.Comparator;
import java.util.List;

public class SortByIngredientCountStrategy implements RecipeSortStrategy {
    @Override
    public List<RecipeAvailability> sort(List<RecipeAvailability> combinedList) {
        // Implement sorting by ingredient count
        combinedList.sort(Comparator.comparingInt(ra -> ra.getRecipe().getIngredients().size()));
        return combinedList;
    }
}