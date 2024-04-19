package com.example.greenplate.viewmodels.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;
import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.viewmodels.ShoppingListViewModel;
import com.example.greenplate.viewmodels.helpers.AvailabilityReportGenerator;
import com.example.greenplate.viewmodels.listeners.OnMultiplicityUpdateListener;
import com.example.greenplate.viewmodels.managers.PantryManager;
import com.example.greenplate.viewmodels.managers.ShoppingListManager;
import com.example.greenplate.viewmodels.observable.MealCalorieData;
import com.example.greenplate.viewmodels.observers.CaloriesLeftDisplay;
import com.example.greenplate.viewmodels.observers.MealBreakdownDisplay;
import com.example.greenplate.views.RecipeFragment;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {
    private List<Recipe> recipeList;
    private List<String> availabilityList;
    private RecipeFragment fragment;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private AvailabilityReportGenerator availabilityReportGenerator;

    public RecipesAdapter(List<Recipe> recipes, List<String> availability,
                          RecipeFragment fragment) {
        recipeList = recipes;
        availabilityList = availability;
        this.fragment = fragment;
        availabilityReportGenerator = AvailabilityReportGenerator.getInstance();
    }

    @Override
    public RecipesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View recipeView = inflater.inflate(R.layout.item_recipe, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(recipeView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RecipesAdapter.ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        String availability = availabilityList.get(position);

        TextView nameTextView = holder.nameTextView;
        TextView availabilityTextView = holder.availabilityTextView;
        TextView numIngredientsTextView = holder.numIngredientsTextView;
        TextView numInstructionsTextView = holder.numInstructionsTextView;

        nameTextView.setText(recipe.getName());
        numIngredientsTextView.setText("Ingredients: "
                + recipe.getIngredients().stream().mapToDouble(e -> e.getMultiplicity()).sum());
        numInstructionsTextView.setText("Instructions: " + recipe.getInstructions().size());
        String availabilityText = availability.equals("Yes") ? "<font color=\"#32CD32\">Yes</font>"
                : "<font color=\"#DC143C\">No</font>";
        availabilityTextView.setText(Html.fromHtml(availabilityText));

        // Check if recipe item is clicked
        holder.itemView.setOnClickListener(v -> {
            int clickedPosition = holder.getAdapterPosition();
            if (clickedPosition != RecyclerView.NO_POSITION) {
                if (selectedPosition != clickedPosition) {
                    if (selectedPosition != RecyclerView.NO_POSITION) {
                        notifyItemChanged(selectedPosition);
                    }
                    selectedPosition = clickedPosition;
                    notifyItemChanged(selectedPosition);
                }
            }
        });
        if (holder.getAdapterPosition() != selectedPosition) {
            holder.nameTextView.setTextColor(Color.BLACK);
            return;
        }
        if ((availabilityTextView.getText()).toString().equals("Yes")) {
            holder.nameTextView.setTextColor(Color.rgb(50, 205, 50));
            Toast.makeText(holder.itemView.getContext(), "Viewing recipe: "
                                    + recipe.getName(), Toast.LENGTH_SHORT).show();

            enoughIngredient(holder, recipe);
        } else {
            Toast.makeText(holder.itemView.getContext(),
                            "Not Enough Ingredients",
                            Toast.LENGTH_SHORT)
                    .show();

            missingIngredient(holder, recipe);
        }
    }

    private void enoughIngredient(RecipesAdapter.ViewHolder holder, Recipe recipe) {
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
        LayoutInflater inflater = fragment.requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_recipe, null);
        TextView nameEditText = dialogView.findViewById(R.id.display_recipe_name);
        TextView ingredientsEditText = dialogView.findViewById(R.id.
                display_missing_ingredients);
        TextView instructionsEditText = dialogView.findViewById(R.id.display_instructions);


        nameEditText.setText(recipe.getName());
        StringBuilder ingredientsText = new StringBuilder();
        recipe.getIngredients().forEach(e -> ingredientsText.append(e.getMultiplicity())
                .append("\t").append(e.getName()).append("\n"));

        ingredientsEditText.setText(ingredientsText.toString());
        StringBuilder instructionsText = new StringBuilder();
        for (int i = 1; i <= recipe.getInstructions().size(); i++) {
            instructionsText.append(i).append(". ")
                    .append(recipe.getInstructions().get(i - 1)).append("\n");
        }
        instructionsEditText.setText(instructionsText.toString());

        //COOK
        builder.setNeutralButton("Cook", (dialog, which) -> {
            updateIngredientAfterCooking(recipe, new UpdateIngredientsCallback() {
                @Override
                public void onComplete(int totalCalories) {
                    Log.d("PRINT", "Recipe calories: " + totalCalories);
                    // Observer Pattern to update input meal and Chart displays
                    MealCalorieData mealCalorieData = new MealCalorieData();

                    CaloriesLeftDisplay caloriesLeftDisplay
                            = new CaloriesLeftDisplay(mealCalorieData);
                    MealBreakdownDisplay mealBreakdownDisplay
                            = new MealBreakdownDisplay(mealCalorieData);

                    // Calls Observer Pattern to update displays with new data
                    mealCalorieData.setMealCalorieData(recipe.getName(), totalCalories);
                    // Update the database with new values

                    // Refresh UI and data
                    if (fragment.isAdded()) {
                        fragment.getActivity().runOnUiThread(() -> fragment.refreshContent());
                    }

                    selectedPosition = RecyclerView.NO_POSITION;
                }

                @Override
                public void onError(Exception e) {
                    //
                }
            });
        });

        builder.setView(dialogView)
                .setNegativeButton("Exit", (dialog, id) -> {
                    // Exit
                    holder.nameTextView.setTextColor(Color.BLACK);
                    selectedPosition = RecyclerView.NO_POSITION;
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void updateIngredientAfterCooking(Recipe recipe, UpdateIngredientsCallback callback) {
        PantryManager pantryManager = new PantryManager();
        final int[] calculatedCookedRecipeCalories = {0};

        pantryManager.retrieve(items -> {
            try {
                for (Ingredient requiredIngredient : recipe.getIngredients()) {
                    List<Ingredient> ingredientsInPantry = items.stream()
                            .map(e -> (Ingredient) e).collect(Collectors.toList());
                    List<Ingredient> matched = ingredientsInPantry.stream().filter(e ->
                                    e.getName().equals(requiredIngredient.getName())
                                            && e.getExpirationDate().after(new Date()))
                            .sorted(Comparator.comparing(Ingredient::getExpirationDate))
                            .collect(Collectors.toList());

                    for (Ingredient i : matched) {
                        double requiredAmount = requiredIngredient.getMultiplicity();
                        if (requiredAmount <= 0) {
                            break;
                        }

                        double takenAmount = Math.min(requiredAmount, i.getMultiplicity());
                        double newMult = i.getMultiplicity() - takenAmount;
                        calculatedCookedRecipeCalories[0] += (int) (takenAmount * i.getCalories());
                        requiredAmount -= takenAmount;

                        pantryManager.updateIngredientMultiplicity(
                                i, newMult,
                                new OnMultiplicityUpdateListener() {
                                    @Override
                                    public void onMultiplicityUpdateSuccess(
                                            GreenPlateStatus status) {
                                        // Handle successful update
                                    }

                                    @Override
                                    public void onMultiplicityUpdateFailure(
                                            GreenPlateStatus status) {
                                        callback.onError(new Exception(
                                                "Failed to update ingredient multiplicity"));
                                    }
                                });

                        if (requiredAmount <= 0) {
                            break;
                        }
                    }
                }
                // Trigger the completion callback after all processing
                callback.onComplete(calculatedCookedRecipeCalories[0]);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }


    private void missingIngredient(RecipesAdapter.ViewHolder holder, Recipe recipe) {
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
        LayoutInflater inflater = fragment.requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_recipe_missing, null);
        TextView nameEditText = dialogView.findViewById(R.id.display_recipe_name);
        TextView missingIngredientsEditText = dialogView.findViewById(R.id.
                display_missing_ingredients);

        nameEditText.setText(recipe.getName());

        availabilityReportGenerator.getMissingElementsForShopping(availabilityReport -> {
            StringBuilder missingIngredientsText = new StringBuilder();
            for (Map.Entry<Ingredient, Double> entry: availabilityReport.get(
                    recipe.getName()).entrySet()) {
                missingIngredientsText.append(entry.getValue())
                        .append("\t").append(entry.getKey().getName()).append("\n");
            }
            missingIngredientsEditText.setText(missingIngredientsText.toString());

            builder.setView(dialogView).setNeutralButton("Add to shopping list",
                            (dialog, id) -> {
                                ShoppingListViewModel shoppingListVM = new ShoppingListViewModel();

                                for (Map.Entry<Ingredient, Double> entry: availabilityReport.get(
                                        recipe.getName()).entrySet()) {
                                    try {
                                        String name = entry.getKey().getName();
                                        double quantity = entry.getValue();
                                        Ingredient newIngredient = new Ingredient(name, 0.,
                                                quantity, null);
                                        shoppingListVM.isItemDuplicate(name, (isDup, dupItem) -> {
                                            if (isDup) {
                                                ShoppingListManager shoppingListManager = new
                                                        ShoppingListManager();
                                                shoppingListManager.addIngredientMultiplicity(name,
                                                        quantity,
                                                        new OnMultiplicityUpdateListener() {
                                                            @Override
                                                            public void onMultiplicityUpdateSuccess(
                                                                    GreenPlateStatus status) { }
                                                            @Override
                                                            public void onMultiplicityUpdateFailure(
                                                                    GreenPlateStatus status) { }
                                                        });
                                                return;
                                            }
                                            shoppingListVM.addIngredient(newIngredient,
                                                    (success, message) -> {
                                                    if (!success) {
                                                        Toast.makeText(holder.itemView.getContext(),
                                                                message, Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(holder.itemView.getContext(),
                                                                "Successfully added "
                                                                        + "missing ingredients",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        });
                                    } catch (Exception e) {
                                        Toast.makeText(holder.itemView.getContext(),
                                                "Failed to add ingredients.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                    .setNegativeButton("Exit", (dialog, id) -> {
                        // Exit
                        holder.nameTextView.setTextColor(Color.BLACK);
                        selectedPosition = RecyclerView.NO_POSITION;
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView availabilityTextView;
        private TextView numIngredientsTextView;
        private TextView numInstructionsTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.recipe_name);
            availabilityTextView = (TextView) itemView.findViewById(R.id.recipe_availability);
            numIngredientsTextView = (TextView) itemView.findViewById(R.id.num_ingredients);
            numInstructionsTextView = (TextView) itemView.findViewById(R.id.num_instructions);
        }
    }
    public interface UpdateIngredientsCallback {
        void onComplete(int totalCalories);
        void onError(Exception e);
    }
}
