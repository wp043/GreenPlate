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
import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.viewmodels.RecipeViewModel;
import com.example.greenplate.viewmodels.listeners.OnRecipeAddedListener;
import com.example.greenplate.viewmodels.managers.CookbookManager;

import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnterNewRecipeActivity extends AppCompatActivity {

    private RecipeViewModel recipeViewModel;
    private LinearLayout ingredientsContainer;
    private Button buttonAddIngredient;
    private Button submitRecipe;
    private EditText recipeNameEditText;
    private OnRecipeAddedListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_new_recipe);

        recipeViewModel = new RecipeViewModel(); // Initialize your RecipeViewModel
        recipeNameEditText = findViewById(R.id.recipe_name);
        ingredientsContainer = findViewById(R.id.ingredients_container);
        buttonAddIngredient = findViewById(R.id.btn_add_ingredient);
        submitRecipe = findViewById(R.id.submit_recipe);
        submitRecipe.setEnabled(false);
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

        // Enable the submit button if it's not already enabled
        if (!submitRecipe.isEnabled()) {
            submitRecipe.setEnabled(true);
        }
    }

    private void submitRecipe() {
        String recipeNameStr = recipeNameEditText.getText().toString().trim();
        EditText recipeInstructionsEditText = findViewById(R.id.recipe_instructions);
        String recipeInstructionsStr = recipeInstructionsEditText.getText().toString().trim();

        List<String> instructions = new ArrayList<>();
        instructions.add(recipeInstructionsStr); // Add instructions (you may need to split by newline)

        List<Ingredient> ingredients = collectIngredients();

        Recipe recipe = new Recipe(recipeNameStr, ingredients, instructions);

        GreenPlateStatus validationStatus = recipeViewModel.validateRecipeData(recipeNameStr, instructions, ingredients);

        if (!validationStatus.isSuccess()) {
            showToast(validationStatus.getMessage());
            return;
        }

        // Add the recipe to the Firebase Realtime Database
        CookbookManager cookbookManager = new CookbookManager();
        cookbookManager.addRecipe(recipe, listener);

        // Show a success message and return to the RecipeFragment
        showToast("Recipe added successfully!");
        finish();
    }

//    private Map<Ingredient, Double> collectIngredients() {
//        Map<Ingredient, Double> ingredients = new HashMap<>();
//        return ingredients;
//    }
    private List<Ingredient> collectIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientsContainer.getChildCount(); i++) {
            View ingredientView = ingredientsContainer.getChildAt(i);
            EditText ingredientNameEditText = ingredientView.findViewById(R.id.ingredientName);
            EditText ingredientQuantityEditText = ingredientView.findViewById(R.id.ingredientQuantity);
            EditText ingredientCaloriesEditText = ingredientView.findViewById(R.id.ingredientCalories);

            String ingredientName = ingredientNameEditText.getText().toString().trim();
            double ingredientQuantity = Double.parseDouble(ingredientQuantityEditText.getText().toString());
            double ingredientCalorie = Double.parseDouble(ingredientCaloriesEditText.getText().toString());

            if (!ingredientName.isEmpty() && ingredientQuantity > 0) {
                ingredients.add(new Ingredient(ingredientName, ingredientCalorie, ingredientQuantity, null));
            }
        }
        return ingredients;
    }
    private void showToast(String message) {
        Toast.makeText(EnterNewRecipeActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
