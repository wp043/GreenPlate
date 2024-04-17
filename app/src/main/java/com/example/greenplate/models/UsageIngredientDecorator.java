package com.example.greenplate.models;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UsageIngredientDecorator extends BaseIngredientDecorator {
    private final Set<String> recipes;
    public UsageIngredientDecorator(Ingredient ingredient, List<Recipe> allRecipes) {
        super(ingredient);
        recipes = allRecipes.stream().filter(r -> r.getIngredients()
                        .stream()
                        .anyMatch(i -> i.getName().equals(ingredient.getName())))
                        .map(Recipe::getName)
                        .collect(Collectors.toSet());
    }

    @Override
    public String displayInfo() {
        return this.displayedItem.displayInfo() + "\nCan be used in: " + recipes;
    }
}
