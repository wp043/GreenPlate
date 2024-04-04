package com.example.greenplate.models;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class Recipe extends RetrievableItem {
    private List<Ingredient> ingredients;
    private List<String> instructions;

    /**
     * Constructor for a Recipe.
     * @param name - name of recipe
     * @param ingredients - map of ingredients and their multiplicities
     * @param instructions - list of instructions for the recipe
     */
    public Recipe(String name,
                  List<Ingredient> ingredients, List<String> instructions) {
        super(name, Recipe.calculateTotalCalorie(ingredients), 1);
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    /**
     * 3-arg constructor for a Recipe: create an empty recipe.
     * @param name - name of recipe
     */
    public Recipe(String name) {
        this(name, new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Add an ingredient to the ingredients map.
     * @param ingredient - the ingredient to add
     * @return whether the operation succeeds
     */
    public GreenPlateStatus addIngredient(Ingredient ingredient) {
        if (ingredient == null) {
            return new GreenPlateStatus(false,
                    "Can't add null to a recipe");
        }
        if (ingredient.getMultiplicity() <= 0) {
            return new GreenPlateStatus(false,
                    "Can't add an ingredient with non-positive multiplicity.");
        }
        this.ingredients.add(ingredient);
        return new GreenPlateStatus(true,
                String.format("Successfully added %s to a recipe", ingredient));
    }

    /**
     * Calculate the total calorie of a given ingredients map.
     * @param ingredients - map of ingredients
     * @return total calorie of all ingredients in the map
     */
    private static double calculateTotalCalorie(List<Ingredient> ingredients) {
        return ingredients.stream()
                .mapToDouble(entry -> entry.getCalories() * entry.getMultiplicity())
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
        if (!this.ingredients.contains(ingredient)) {
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
    public GreenPlateStatus updateIngredient(Ingredient ingredient, double multiplicity) {
        if (ingredient == null) {
            return new GreenPlateStatus(false,
                    "Can't update null in a recipe");
        }
        if (!this.ingredients.contains(ingredient)) {
            return new GreenPlateStatus(false,
                    "Can't update a non-existing ingredient in a recipe");
        }
        int index = 0;
        for (Ingredient i : this.ingredients) {
            if (!i.equals(ingredient)) {
                index++;
            } else {
                break;
            }
        }
        double oldMult = this.ingredients.get(index).getMultiplicity();
        this.ingredients.get(index).setMultiplicity(multiplicity);
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
     * Getter for ingredient list within a recipe
     * @return a list containing ingredients
     */
    public List<Ingredient> getIngredients() {
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
