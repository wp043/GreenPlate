package com.example.greenplate.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.models.RetrievableItem;
import com.example.greenplate.viewmodels.adapters.RecipesAdapter;
import com.example.greenplate.viewmodels.listeners.OnDataRetrievedCallback;
import com.example.greenplate.viewmodels.listeners.OnRecipeAddedListener;
import com.example.greenplate.viewmodels.managers.CookbookManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeViewModel extends ViewModel {
    private CookbookManager cookbookManager;
    private boolean defaultRecipesInitialized = false;

    public RecipeViewModel() {
        cookbookManager = new CookbookManager();
    }

    public void addDefaultRecipes(Context context, RecyclerView rvRecipes) {
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
            retrieveAndDisplayIngredients(context, rvRecipes);
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
            retrieveAndDisplayIngredients(context, rvRecipes);
        });
        defaultRecipesInitialized = true;
    }

    public void addRecipe(Recipe recipe, OnRecipeAddedListener listener) {
        if (recipe.getName() == null) {
            listener.onRecipeAdded(false);
            return;
        }
        if (recipe.getIngredients().isEmpty()) {
            listener.onRecipeAdded(false);
            return;
        }

        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient.getName() == null || ingredient.getName().trim().isEmpty() ||
                    ingredient.getMultiplicity() <= 0 || ingredient.getCalories() < 0) {
                listener.onRecipeAdded(false);
                return;
            }
        }

        cookbookManager.isRecipeDuplicate(recipe, isDuplicate -> {
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

    public GreenPlateStatus validateRecipeData(String recipeName, List<String> instructions, List<Ingredient> ingredients) {
        if (recipeName.trim().isEmpty()) {
            return new GreenPlateStatus(false, "Recipe name cannot be empty");
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
    /**
     * get all recipes in the cookbook
     * @param callback Callback to retrieve recipes from the cookbookManager
     */
    public void getRecipes(OnDataRetrievedCallback callback) {
        cookbookManager.retrieve(callback);
    }


    public void retrieveAndDisplayIngredients(Context context, RecyclerView rvRecipes) {
        this.getRecipes(itemsRecipe -> {
            List<Recipe> recipes = new ArrayList<>();
            if (itemsRecipe != null) {
                for (RetrievableItem item : itemsRecipe) {
                    if (item instanceof Recipe) {
                        Recipe recipe = (Recipe) item;
                        recipes.add(recipe);
                    }
                }
            }

            new IngredientViewModel().getIngredients(itemsIngredient -> {
                List<String> availability = new ArrayList<>();
                Map<String, Double> ingredients = new HashMap<>();

                for (RetrievableItem item: itemsIngredient) {
                    ingredients.put(item.getName(), item.getMultiplicity());
                }

                for (Recipe recipe: recipes) {
                    boolean enoughIngredients = true;
                    for (Ingredient ingredient: recipe.getIngredients()) {
                        Log.d(recipe.getName() + ", " + ingredient.getName(),
                                "Recipe: " + ingredient.getMultiplicity() + " Database: " + ingredients.get(ingredient.getName()));
                        if (!ingredients.containsKey(ingredient.getName()) || (ingredients.get(ingredient.getName()) < recipe.getMultiplicity())) {
                            enoughIngredients = false;
                            break;
                        }
                    }
                    if (enoughIngredients) {
                        availability.add("Yes");
                    } else {
                        availability.add("No");
                    }
                }

                // Use RecyclerView adapter to put list of recipes into RecyclerView (scrollable list)
                RecipesAdapter adapter = new RecipesAdapter(recipes, availability);
                rvRecipes.setAdapter(adapter);
                rvRecipes.setLayoutManager(new LinearLayoutManager(context));
            });
        });
    }


    public void retrieveAndDisplayFiltered(Context context, RecyclerView rvRecipes, String search) {
        this.getRecipes(items -> {
            List<Recipe> recipes = new ArrayList<>();
            if (items != null) {
                for (RetrievableItem item : items) {
                    if (item instanceof Recipe) {
                        Recipe recipe = (Recipe) item;
                        recipes.add(recipe);
                    }
                }
            }

            new IngredientViewModel().getIngredients(itemsIngredient -> {
                List<String> availability = new ArrayList<>();
                Map<String, Double> ingredients = new HashMap<>();

                for (RetrievableItem item: itemsIngredient) {
                    ingredients.put(item.getName(), item.getMultiplicity());
                }

                for (Recipe recipe: recipes) {
                    boolean enoughIngredients = true;
                    for (Ingredient ingredient: recipe.getIngredients()) {
                        Log.d(recipe.getName() + ", " + ingredient.getName(),
                                "Recipe: " + ingredient.getMultiplicity() + " Database: " +
                                        ingredients.get(ingredient.getName()));
                        if (!ingredients.containsKey(ingredient.getName())
                                || (ingredients.get(ingredient.getName())
                                < recipe.getMultiplicity())) {
                            enoughIngredients = false;
                            break;
                        }
                    }
                    if (enoughIngredients) {
                        availability.add("Yes");
                    } else {
                        availability.add("No");
                    }
                }

                // Filter from search
                ArrayList<Recipe> filteredList = new ArrayList<>();
                List<String> filteredAvailability = new ArrayList<>();
                if (search.isEmpty()) {
                    // If the search query is empty, show the original list
                    filteredList = new ArrayList<>(recipes);
                    filteredAvailability = new ArrayList<>(availability);
                } else {
                    filteredList = new ArrayList<>();
                    for (int i = 0; i <= recipes.size() - 1; i++) {
                        Recipe recipeItem = recipes.get(i);
                        if (recipeItem.getName().toLowerCase().contains(search.toLowerCase())) {
                            filteredList.add(recipeItem);
                            filteredAvailability.add(availability.get(i));
                        }
                    }
                }


//                filteredList.sort((r1, r2) -> r1.getName().compareToIgnoreCase(r2.getName()));
                // Use RecyclerView adapter to put list of recipes into RecyclerView (scrollable list)
                RecipesAdapter adapter = new RecipesAdapter(filteredList, filteredAvailability);
                rvRecipes.setAdapter(adapter);
                rvRecipes.setLayoutManager(new LinearLayoutManager(context));
            });
        });
    }


    public void retrieveAndDisplaySortedByName(Context context, RecyclerView rvRecipes) {
        this.getRecipes(itemsRecipe -> {
            List<Recipe> recipes = new ArrayList<>();
            if (itemsRecipe != null) {
                for (RetrievableItem item : itemsRecipe) {
                    if (item instanceof Recipe) {
                        Recipe recipe = (Recipe) item;
                        recipes.add(recipe);
                    }
                }
            }

            new IngredientViewModel().getIngredients(itemsIngredient -> {
                List<String> availability = new ArrayList<>();
                Map<String, Double> ingredients = new HashMap<>();

                for (RetrievableItem item : itemsIngredient) {
                    ingredients.put(item.getName(), item.getMultiplicity());
                }

                List<RecipeAvailability> combinedList = new ArrayList<>();
                for (Recipe recipe : recipes) {
                    boolean enoughIngredients = true;
                    for (Ingredient ingredient : recipe.getIngredients()) {
                        if (!ingredients.containsKey(ingredient.getName()) || (ingredients.get(ingredient.getName()) < recipe.getMultiplicity())) {
                            enoughIngredients = false;
                            break;
                        }
                    }
                    combinedList.add(new RecipeAvailability(recipe, enoughIngredients ? "Yes" : "No"));
                }

                // Sorting the combinedList based on Recipe name
                SortByNameStrategy sortingStrat = new SortByNameStrategy();
                combinedList = sortingStrat.sort(combinedList);

                // Extracting the sorted lists
                List<Recipe> sortedRecipes = new ArrayList<>();
                List<String> sortedAvailability = new ArrayList<>();
                for (RecipeAvailability ra : combinedList) {
                    sortedRecipes.add(ra.recipe);
                    sortedAvailability.add(ra.availability);
                }

                // Use RecyclerView adapter to put list of recipes into RecyclerView (scrollable list)
                RecipesAdapter adapter = new RecipesAdapter(sortedRecipes, sortedAvailability);
                rvRecipes.setAdapter(adapter);
                rvRecipes.setLayoutManager(new LinearLayoutManager(context));
            });
        });
    }

    public void retrieveAndDisplaySortedByIngredients(Context context, RecyclerView rvRecipes) {
        this.getRecipes(itemsRecipe -> {
            List<Recipe> recipes = new ArrayList<>();
            if (itemsRecipe != null) {
                for (RetrievableItem item : itemsRecipe) {
                    if (item instanceof Recipe) {
                        Recipe recipe = (Recipe) item;
                        recipes.add(recipe);
                    }
                }
            }

            new IngredientViewModel().getIngredients(itemsIngredient -> {
                List<String> availability = new ArrayList<>();
                Map<String, Double> ingredients = new HashMap<>();

                for (RetrievableItem item : itemsIngredient) {
                    ingredients.put(item.getName(), item.getMultiplicity());
                }

                List<RecipeAvailability> combinedList = new ArrayList<>();
                for (Recipe recipe : recipes) {
                    boolean enoughIngredients = true;
                    for (Ingredient ingredient : recipe.getIngredients()) {
                        if (!ingredients.containsKey(ingredient.getName()) || (ingredients.get(ingredient.getName()) < recipe.getMultiplicity())) {
                            enoughIngredients = false;
                            break;
                        }
                    }
                    combinedList.add(new RecipeAvailability(recipe, enoughIngredients ? "Yes" : "No"));
                }

                // Sorting the combinedList based on Ingredient Number
                SortByIngredientCountStrategy sortingStrat = new SortByIngredientCountStrategy();
                combinedList = sortingStrat.sort(combinedList);

                // Extracting the sorted lists
                List<Recipe> sortedRecipes = new ArrayList<>();
                List<String> sortedAvailability = new ArrayList<>();
                for (RecipeAvailability ra : combinedList) {
                    sortedRecipes.add(ra.recipe);
                    sortedAvailability.add(ra.availability);
                }

                // Use RecyclerView adapter to put list of recipes into RecyclerView (scrollable list)
                RecipesAdapter adapter = new RecipesAdapter(sortedRecipes, sortedAvailability);
                rvRecipes.setAdapter(adapter);
                rvRecipes.setLayoutManager(new LinearLayoutManager(context));
            });
        });
    }


}