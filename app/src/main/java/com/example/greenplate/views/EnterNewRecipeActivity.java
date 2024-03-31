package com.example.greenplate.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.greenplate.R;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.viewmodels.RecipeViewModel;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class EnterNewRecipeActivity extends AppCompatActivity {

    private RecipeViewModel recipeViewModel;
    private LinearLayout ingredientsContainer;
    private Button buttonAddIngredient;
    private Button submitRecipe;
    private EditText recipeNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_new_recipe);

        recipeViewModel = new RecipeViewModel(); // Initialize your RecipeViewModel
        recipeNameEditText = findViewById(R.id.recipe_name);
        ingredientsContainer = findViewById(R.id.ingredients_container);
        buttonAddIngredient = findViewById(R.id.btn_add_ingredient);
        submitRecipe = findViewById(R.id.submit_recipe);

        buttonAddIngredient.setOnClickListener(v -> addIngredientField());
        submitRecipe.setOnClickListener(v -> submitRecipe());

        Button cancelRecipeButton = findViewById(R.id.cancelRecipe);
        cancelRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Just finish the current activity to return to the previous screen
                finish();
            }
        });
        Button submitRecipe = findViewById(R.id.submit_recipe);
        submitRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRecipe();
            }
        });
    }

    private void addIngredientField() {
        LayoutInflater inflater = LayoutInflater.from(this);
        final ViewGroup ingredientView = (ViewGroup) inflater.inflate(
                R.layout.recipe_ingredient_list, ingredientsContainer, false);


        EditText ingredientName = ingredientView.findViewById(R.id.ingredientName);
        EditText ingredientQuantity = ingredientView.findViewById(R.id.ingredientQuantity);

        // Optionally set up a button to remove this ingredient field
        Button removeButton = new Button(this);
        removeButton.setText("-");
        removeButton.setOnClickListener(v -> ingredientsContainer.removeView(ingredientView));

        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ingredientView.addView(removeButton, buttonLayoutParams);

        ingredientsContainer.addView(ingredientView);
    }

//    private void submitRecipe() {
//        String recipeNameStr = recipeNameEditText.getText().toString().trim();
//        if (recipeNameStr.isEmpty()) {
//            recipeNameEditText.setError("Recipe name is required!");
//            showToast("Recipe name is required!");
//            return;
//        }
//
//        List<Ingredient> ingredients = collectIngredients();
//        if (ingredients.isEmpty()) {
//            showToast("At least one ingredient required");
//            return;
//        }
//
//        // Validate ingredients using ViewModel
//        String validationError = recipeViewModel.validateIngredients(ingredients);
//        if (validationError != null) {
//            showToast(validationError);
//            return;
//        }
//
////        // Assuming the ViewModel has a method to handle the recipe submission
////        Recipe recipe = new Recipe(recipeNameStr); // Update as needed to match your Recipe class
////        boolean submitSuccess = recipeViewModel.submitRecipe(recipe, ingredients);
////
////        if (submitSuccess) {
////            showToast("Recipe submitted successfully!");
////            finish(); // Go back to the main screen
////        } else {
////            showToast("Failed to submit the recipe. Please try again.");
////        }
//    }
    private void submitRecipe() {
        String recipeNameStr = recipeNameEditText.getText().toString().trim();
        EditText recipeInstructionsEditText = findViewById(R.id.recipe_instructions);
        String recipeInstructionsStr = recipeInstructionsEditText.getText().toString().trim();

        // Check if recipe name is empty
        if (recipeNameStr.isEmpty()) {
            recipeNameEditText.setError("Recipe name is required!");
            showToast("Recipe name is required!");
            return;
        }

        // Check if instructions are empty
        if (recipeInstructionsStr.isEmpty()) {
            recipeInstructionsEditText.setError("Instructions are required!");
            showToast("Instructions are required!");
            return;
        }

        // Collect and validate ingredients
        List<Ingredient> ingredients = collectIngredients();
        if (ingredients == null || ingredients.isEmpty()) {
            showToast("At least one ingredient required");
            return;
        }

        String validationError = recipeViewModel.validateIngredients(ingredients);
        if (validationError != null) {
            showToast(validationError);
            return;
        }

        // Create a Recipe object and attempt to add it
        Recipe recipe = new Recipe(recipeNameStr); // Assume constructor or setters to set ingredients and instructions
        // You need to modify the Recipe class or use its methods accordingly
        // Assume method to set ingredients and instructions exist in Recipe class
        recipe.addInstruction(recipeInstructionsStr); // Split instructions by new lines
        for (Ingredient ingredient : ingredients) {
            recipe.addIngredient(ingredient, ingredient.getMultiplicity());
        }

        // Add recipe via ViewModel
    }

    private List<Ingredient> collectIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        return ingredients;
    }
    private void showToast(String message) {
        Toast.makeText(EnterNewRecipeActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
