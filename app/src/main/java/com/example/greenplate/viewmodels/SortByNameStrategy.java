package com.example.greenplate.viewmodels;

import java.util.Comparator;
import java.util.List;

public class SortByNameStrategy implements RecipeSortStrategy {
    @Override
    public List<RecipeAvailability> sort(List<RecipeAvailability> combinedList) {
        // Implement sorting by name
        combinedList.sort(Comparator.comparing(o -> o.recipe.getName()));
        return combinedList;
    }
}