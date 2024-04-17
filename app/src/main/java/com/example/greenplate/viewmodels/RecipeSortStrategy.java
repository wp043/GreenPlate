package com.example.greenplate.viewmodels;

import java.util.List;

public interface RecipeSortStrategy {
    List<RecipeAvailability> sort(List<RecipeAvailability> combinedList);
}

