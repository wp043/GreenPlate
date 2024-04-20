package com.example.greenplate.viewmodels.helpers;

import android.util.Log;

import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.viewmodels.UserInfoViewModel;
import com.example.greenplate.viewmodels.listeners.OnReportGeneratedCallback;
import com.example.greenplate.viewmodels.managers.CookbookManager;
import com.example.greenplate.viewmodels.managers.PantryManager;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AvailabilityReportGenerator {
    private Map<String, Map<Ingredient, Double>> missingIngredientsForRecipes;
    private PantryManager pantryManager;
    private CookbookManager cookbookManager;
    private static volatile AvailabilityReportGenerator instance;

    private AvailabilityReportGenerator() {
        pantryManager = new PantryManager();
        cookbookManager = new CookbookManager();
    }

    public static AvailabilityReportGenerator getInstance() {
        if (instance == null) {
            synchronized (UserInfoViewModel.class) {
                if (instance == null) {
                    instance = new AvailabilityReportGenerator();
                }
            }
        }
        return instance;
    }

    public void getMissingElementsForShopping(OnReportGeneratedCallback callback) {
        Map<String, Map<Ingredient, Double>> availabilityReport = new HashMap<>();
        cookbookManager.retrieve(itemsRecipe -> {
            List<Recipe> recipes = itemsRecipe
                    .stream().map(e -> ((Recipe) e)).collect(Collectors.toList());

            pantryManager.retrieve(itemsIngredient -> {
                List<Ingredient> currentIngredientsInPantry = itemsIngredient
                        .stream().map(e -> ((Ingredient) e)).collect(Collectors.toList());
                for (Recipe recipe : recipes) {
                    Map<Ingredient, Double> missingIngredients = new HashMap<>();
                    for (Ingredient requiredIngredient : recipe.getIngredients()) {
                        double requiredNum = requiredIngredient.getMultiplicity();
                        double numInPantry = currentIngredientsInPantry.stream()
                                .filter(e -> e.getName().equals(requiredIngredient.getName())
                                        && e.getExpirationDate().after(new Date()))
                                .mapToDouble(e -> e.getMultiplicity()).sum();
                        if (numInPantry < requiredNum) {
                            missingIngredients.put(new Ingredient(requiredIngredient),
                                    requiredNum - numInPantry);
                        }
                    }
                    if (missingIngredients.keySet().size() != 0) {
                        availabilityReport.put(recipe.getName(), missingIngredients);
                    }
                }
                callback.onReportGenerated(availabilityReport);
            });
        });
    }

    public void getAvailable(OnReportGeneratedCallback callback) {
        Map<String, Map<Ingredient, Double>> availabilityReport = new HashMap<>();
        cookbookManager.retrieve(itemsRecipe -> {
            List<Recipe> recipes = itemsRecipe
                    .stream().map(e -> ((Recipe) e)).collect(Collectors.toList());

            pantryManager.retrieve(itemsIngredient -> {
                List<Ingredient> currentIngredientsInPantry = itemsIngredient
                        .stream().map(e -> ((Ingredient) e)).collect(Collectors.toList());
                for (Recipe recipe : recipes) {
                    Map<Ingredient, Double> hasIngredient = new HashMap<>();
                    for (Ingredient requiredIngredient : recipe.getIngredients()) {
                        double requiredNum = requiredIngredient.getMultiplicity();
                        double numInPantry = currentIngredientsInPantry.stream()
                                .filter(e -> e.getName().equals(requiredIngredient.getName())
                                        && e.getExpirationDate().after(new Date()))
                                .mapToDouble(e -> e.getMultiplicity()).sum();
                        if (numInPantry >= requiredNum) {
                            hasIngredient.put(new Ingredient(requiredIngredient),
                                    numInPantry - requiredNum);
                        }
                    }
                    if (hasIngredient.keySet().size() != 0) {
                        availabilityReport.put(recipe.getName(), hasIngredient);
                    }
                }
                callback.onReportGenerated(availabilityReport);
            });
        });
    }

    public static void logReport(Map<String, Map<Ingredient, Double>> availabilityReport) {
        StringBuilder sb = new StringBuilder("Report:\n");
        for (Map.Entry<String, Map<Ingredient, Double>> reportEntry
                : availabilityReport.entrySet()) {
            sb.append("Missing ingredients for ").append(reportEntry.getKey()).append(":\n");
            Map<Ingredient, Double> missingElements = reportEntry.getValue();
            for (Map.Entry<Ingredient, Double> entry : missingElements.entrySet()) {
                sb.append(entry.getKey().getName()).append(": need ")
                        .append(entry.getValue()).append(" more\n");
            }
        }
        sb.append("End of report.");
        Log.d("AvaReport", sb.toString());
    }
}
