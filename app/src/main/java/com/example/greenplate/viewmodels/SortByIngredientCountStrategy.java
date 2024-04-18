package com.example.greenplate.viewmodels;

import java.util.Comparator;
import java.util.List;

public class SortByIngredientCountStrategy implements RecipeSortStrategy {
    @Override
    public List<RecipeAvailability> sort(List<RecipeAvailability> combinedList) {
        // Implement sorting by ingredient count
        combinedList.sort(Comparator.comparingDouble(ra -> ra.getRecipe().
                getIngredients().stream().mapToDouble(e -> e.getMultiplicity()).sum()));
        return combinedList;
    }
}