package com.example.greenplate.models;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Recipe extends RetrievableItem {
    private Map<Ingredient, Integer> ingredients;
    private List<String> instructions;

    /**
     * Constructor for a Recipe.
     * @param name - name of recipe
     * @param ingredients - map of ingredients and their multiplicities
     * @param instructions - list of instructions for the recipe
     */
    public Recipe(String name,
                  Map<Ingredient, Integer> ingredients, List<String> instructions) {
        super(name, Recipe.calculateTotalCalorie(ingredients), 1);
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    /**
     * 3-arg constructor for a Recipe: create an empty recipe.
     * @param name - name of recipe
     */
    public Recipe(String name) {
        this(name, new HashMap<>(), new ArrayList<>());
    }

    /**
     * Add an ingredient to the ingredients map.
     * @param ingredient - the ingredient to add
     * @param multiplicity - the multiplicity to update
     * @return whether the operation succeeds
     */
    public GreenPlateStatus addIngredient(Ingredient ingredient, int multiplicity) {
        if (ingredient == null) {
            return new GreenPlateStatus(false,
                    "Can't add null to a recipe");
        }
        if (multiplicity <= 0) {
            return new GreenPlateStatus(false,
                    "Can't add an ingredient with non-positive multiplicity.");
        }
        this.ingredients.put(ingredient, multiplicity);
        return new GreenPlateStatus(true,
                String.format("Successfully added %s to a recipe", ingredient));
    }

    /**
     * Calculate the total calorie of a given ingredients map.
     * @param ingredients - map of ingredients
     * @return total calorie of all ingredients in the map
     */
    private static double calculateTotalCalorie(Map<Ingredient, Integer> ingredients) {
        return ingredients.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getCalories() * entry.getValue())
                .sum();
    }

    /**
     * Remove an ingredient from the ingredients map.
     * @param ingredient - the ingredient to remove
     * @return whether the operation succeeds
     */
    public GreenPlateStatus removeIngredient(Ingredient ingredient) {
        if (ingredient == null) {
            return new GreenPlateStatus(false,
                    "Can't remove null from a recipe");
        }
        if (!this.ingredients.containsKey(ingredient)) {
            return new GreenPlateStatus(false,
                    "Can't remove a non-existing ingredient from a recipe");
        }
        this.ingredients.remove(ingredient);
        return new GreenPlateStatus(false,
                String.format("Successfully removed %s from a recipe", ingredient));
    }

    /**
     * Update the multiplicity of an ingredient.
     * @param ingredient - the ingredient to update
     * @param multiplicity - the new multiplicity of the ingredient
     * @return whether the operation succeeds
     */
    public GreenPlateStatus updateIngredient(Ingredient ingredient, int multiplicity) {
        if (ingredient == null) {
            return new GreenPlateStatus(false,
                    "Can't update null in a recipe");
        }
        if (!this.ingredients.containsKey(ingredient)) {
            return new GreenPlateStatus(false,
                    "Can't update a non-existing ingredient in a recipe");
        }
        int oldMult = this.ingredients.put(ingredient, multiplicity);
        return new GreenPlateStatus(true,
                String.format("Update multiplicity of %s from %d to %d",
                ingredient, oldMult, multiplicity));
    }

    /**
     * Add an instruction to the instructions of the recipe.
     * @param instruction - the instruction to add
     * @return whether the operation succeeds
     */
    public GreenPlateStatus addInstruction(String instruction) {
        if (instruction == null || TextUtils.isEmpty(instruction.trim())) {
            return new GreenPlateStatus(false,
                    "Can't add null/empty/blank string as an instruction.");
        }
        this.instructions.add(instruction);
        return new GreenPlateStatus(true,
                String.format("Added \"%s\" as an instruction to a recipe.", instruction));
    }

    /**
     * Remove an instruction from the instructions of the recipe.
     * @param instruction - the instruction to remove
     * @return whether the operation succeeds
     */
    public GreenPlateStatus removeInstruction(String instruction) {
        if (instruction == null || TextUtils.isEmpty(instruction.trim())) {
            return new GreenPlateStatus(false,
                    "Can't remove null/empty/blank string as an instruction.");
        }
        boolean res = this.instructions.remove(instruction);
        return res ? new GreenPlateStatus(true,
                String.format("Removed \"%s\" from instructions of a recipe.", instruction))
                : new GreenPlateStatus(false,
                String.format("Failed to remove \"%s\" from instructions of a recipe,"
                        + "as it's not found.", instruction));
    }


    /**
     * Getter for ingredient map.
     * @return a map containing ingredients and their multiplicities of the recipe
     */
    public Map<Ingredient, Integer> getIngredients() {
        return ingredients;
    }

    /**
     * Getter for instructions list.
     * @return a list containing the instructions for the recipe
     */
    public List<String> getInstructions() {
        return instructions;
    }
}
