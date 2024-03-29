package com.example.greenplate.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.greenplate.R;
import com.example.greenplate.viewmodels.RecipeViewModel;
import android.view.ViewGroup;

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

    private void submitRecipe() {
        String recipeNameStr = recipeNameEditText.getText().toString().trim();
        if (recipeNameStr.isEmpty()) {
            recipeNameEditText.setError("Recipe name is required!");
            return;
        }

        for (int i = 0; i < ingredientsContainer.getChildCount(); i++) {
            View ingredientView = ingredientsContainer.getChildAt(i);
            EditText ingredientName = ingredientView.findViewById(R.id.ingredientName);
            EditText ingredientQuantity = ingredientView.findViewById(R.id.ingredientQuantity);

            String name = ingredientName.getText().toString().trim();
            String quantityStr = ingredientQuantity.getText().toString().trim();

            if (name.isEmpty()) {
                ingredientName.setError("Ingredient name is required!");
                return;
            }

            float quantity;
            try {
                quantity = Float.parseFloat(quantityStr);
            } catch (NumberFormatException e) {
                ingredientQuantity.setError("Please enter a valid number!");
                return;
            }

            if (quantity <= 0) {
                ingredientQuantity.setError("Quantity must be positive!");
                return;
            }

            // TODO: Add ingredient data to a list or similar data structure
        }

        submitRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement your submit logic here...

                // After submitting, finish the activity to go back to the main recipe screen
                finish();
            }
        });

    }

}
