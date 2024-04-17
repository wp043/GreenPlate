package com.example.greenplate.viewmodels;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.viewmodels.adapters.RecipesAdapter;
import com.example.greenplate.viewmodels.helpers.AvailabilityReportGenerator;
import com.example.greenplate.viewmodels.listeners.OnDataRetrievedCallback;
import com.example.greenplate.viewmodels.listeners.OnRecipeAddedListener;
import com.example.greenplate.viewmodels.managers.CookbookManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RecipeViewModel extends ViewModel {
    private CookbookManager cookbookManager;
    private boolean defaultRecipesInitialized = false;

    public RecipeViewModel() {
        cookbookManager = new CookbookManager();
    }

    public void addDefaultRecipes(RecyclerView rvRecipes, Fragment fragment) {
        // Add test recipe 1
        List<Ingredient> ingredients1 = new ArrayList<>();
        ingredients1.add(new Ingredient("Bun", 100, 2, null));
        ingredients1.add(new Ingredient("Hamburger Patty", 200, 1, null));
        ingredients1.add(new Ingredient("Cheese Slice", 50, 1, null));
        List<String> instructions1 = new ArrayList<>();
        instructions1.add("Grill hamburger patty.");
        instructions1.add("Put cheese slice onto hamburger.");
        instructions1.add("Put hamburger patty between buns.");
        Recipe recipe1 = new Recipe("Cheeseburger", ingredients1, instructions1);
        addRecipe(recipe1, success -> {
            // Update RecyclerView
            retrieveAndDisplayIngredients(rvRecipes, fragment);
        });

        // Add test recipe 2
        List<Ingredient> ingredients2 = new ArrayList<>();
        ingredients2.add(new Ingredient("Bun", 100, 1, null));
        ingredients2.add(new Ingredient("Sausage", 100, 1, null));
        List<String> instructions2 = new ArrayList<>();
        instructions2.add("Grill sausage.");
        instructions2.add("Put sausage into bun.");
        Recipe recipe2 = new Recipe("Hot dog", ingredients2, instructions2);
        addRecipe(recipe2, success -> {
            // Update RecyclerView
            retrieveAndDisplayIngredients(rvRecipes, fragment);
        });
        defaultRecipesInitialized = true;
    }

    public void addRecipe(Recipe recipe, OnRecipeAddedListener listener) {
        if (recipe == null) {
            listener.onRecipeAdded(false);
            return;
        }
        if (recipe.getName() == null) {
            listener.onRecipeAdded(false);
            return;
        }
        if (recipe.getIngredients().isEmpty()) {
            listener.onRecipeAdded(false);
            return;
        }

        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient.getName() == null || ingredient.getName().trim().isEmpty()
                    || ingredient.getMultiplicity() <= 0 || ingredient.getCalories() < 0) {
                listener.onRecipeAdded(false);
                return;
            }
        }

        cookbookManager.isRecipeDuplicate(recipe, (isDuplicate, duplicateName) -> {
            if (isDuplicate) {
                listener.onRecipeAdded(false);
            } else {
                cookbookManager.addRecipe(recipe, new OnRecipeAddedListener() {
                    @Override
                    public void onRecipeAdded(boolean success) {
                        listener.onRecipeAdded(success);
                    }
                });
            }
        });
    }


    public GreenPlateStatus validateRecipeData(String recipeName, List<String>
            instructions, List<Ingredient> ingredients) {
        if (recipeName.trim().isEmpty()) {
            return new GreenPlateStatus(false, "Recipe name cannot be empty");
        }

        boolean hasValidIngredient = true;
        int index = 0;
        for (Ingredient ingredient : ingredients) {
            if (ingredient.getName().trim().isEmpty()
                    || ingredients.get(index).getMultiplicity() <= 0) {
                hasValidIngredient = false;
            }
            index++;
        }

        if (!hasValidIngredient) {
            return new GreenPlateStatus(false,
                    "At least one ingredient with a valid name and quantity is required");
        }

        return new GreenPlateStatus(true, null);
    }

    /**
     * get all recipes in the cookbook
     *
     * @param callback Callback to retrieve recipes from the cookbookManager

     */
    public void getRecipes(OnDataRetrievedCallback callback) {
        cookbookManager.retrieve(callback);
    }

    public void retrieveAndDisplayIngredients(RecyclerView rvRecipes, Fragment fragment) {
        this.getRecipes(itemsRecipe -> {
            List<Recipe> recipes = itemsRecipe.stream().map(e -> (Recipe) e)
                    .collect(Collectors.toList());

            List<String> availability = new ArrayList<>();
            AvailabilityReportGenerator.getInstance().getMissingElementsForShopping(report -> {
                recipes.forEach(recipe -> availability.add(
                        report.containsKey(recipe.getName()) ? "No" : "Yes"));
                RecipesAdapter adapter = new RecipesAdapter(recipes, availability, fragment);
                rvRecipes.setAdapter(adapter);
                rvRecipes.setLayoutManager(new LinearLayoutManager(fragment.getContext()));
            });
        });
    }


    public void retrieveAndDisplayFiltered(
            RecyclerView rvRecipes, String search, Fragment fragment) {
        this.getRecipes(itemsRecipe -> {
            List<Recipe> recipes = itemsRecipe.stream().map(e -> (Recipe) e)
                    .collect(Collectors.toList());

            List<String> availability = new ArrayList<>();
            AvailabilityReportGenerator.getInstance().getMissingElementsForShopping(report -> {
                recipes.forEach(recipe -> availability.add(
                        report.containsKey(recipe.getName()) ? "No" : "Yes"));

                ArrayList<Recipe> filteredList = new ArrayList<>();
                List<String> filteredAvailability = new ArrayList<>();
                if (search.isEmpty()) {
                    filteredList = new ArrayList<>(recipes);
                    filteredAvailability = new ArrayList<>(availability);
                } else {
                    for (int i = 0; i < recipes.size(); i++) {
                        Recipe recipeItem = recipes.get(i);
                        if (recipeItem.getName().toLowerCase().contains(search.toLowerCase())) {
                            filteredList.add(recipeItem);
                            filteredAvailability.add(availability.get(i));
                        }
                    }
                }
                // filteredList.sort((r1, r2) -> r1.getName().compareToIgnoreCase(r2.getName()));
                // Use RecyclerView adapter to put list of recipes into RecyclerView
                RecipesAdapter adapter = new RecipesAdapter(
                        filteredList, filteredAvailability, fragment);
                rvRecipes.setAdapter(adapter);
                rvRecipes.setLayoutManager(new LinearLayoutManager(fragment.getContext()));
            });
        });
    }

    private void retrieveAndDisplaySorted(Context context, RecyclerView rvRecipes,
                                          Fragment fragment, RecipeSortStrategy sorter) {
        this.getRecipes(itemsRecipe -> {
            List<Recipe> recipes = itemsRecipe.stream().map(e -> (Recipe) e)
                    .collect(Collectors.toList());

            List<String> availability = new ArrayList<>();
            AvailabilityReportGenerator.getInstance().getMissingElementsForShopping(report -> {
                recipes.forEach(recipe -> availability.add(
                        report.containsKey(recipe.getName()) ? "No" : "Yes"));

                List<RecipeAvailability> combinedList = IntStream.range(0, recipes.size())
                        .mapToObj(i -> new RecipeAvailability(recipes.get(i), availability.get(i)))
                        .collect(Collectors.toList());

                combinedList = sorter.sort(combinedList);
                List<Recipe> sortedRecipes = combinedList.stream()
                        .map(RecipeAvailability::getRecipe).collect(Collectors.toList());
                List<String> sortedAvailability = combinedList.stream()
                        .map(RecipeAvailability::getAvailability).collect(Collectors.toList());

                RecipesAdapter adapter = new RecipesAdapter(sortedRecipes,
                        sortedAvailability, fragment);
                rvRecipes.setAdapter(adapter);
                rvRecipes.setLayoutManager(new LinearLayoutManager(context));
            });
        });
    }

    public void retrieveAndDisplaySortedByName(
            Context context, RecyclerView rvRecipes, Fragment fragment) {
        retrieveAndDisplaySorted(context, rvRecipes, fragment, new SortByNameStrategy());
    }

    public void retrieveAndDisplaySortedByIngredients(
            Context context, RecyclerView rvRecipes, Fragment fragment) {
        retrieveAndDisplaySorted(context, rvRecipes, fragment, new SortByIngredientCountStrategy());
    }
    public void updateRecipeAvailability(RecyclerView rvRecipes, Fragment fragment) {
        AvailabilityReportGenerator.getInstance().getMissingElementsForShopping(report -> {
            retrieveAndDisplayIngredients(rvRecipes, fragment);
        });
    }

}

