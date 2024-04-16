package com.example.greenplate.viewmodels.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;
import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.viewmodels.ShoppingListViewModel;
import com.example.greenplate.viewmodels.helpers.AvailabilityReportGenerator;
import com.example.greenplate.viewmodels.listeners.OnMultiplicityUpdateListener;
import com.example.greenplate.viewmodels.managers.ShoppingListManager;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {
    private List<Recipe> recipeList;
    private List<String> availabilityList;
    private Fragment fragment;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private AvailabilityReportGenerator availabilityReportGenerator;

    public RecipesAdapter(List<Recipe> recipes, List<String> availability, Fragment fragment) {
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
        // Get the data model based on position
        Recipe recipe = recipeList.get(position);
        String availability = availabilityList.get(position);

        // Set item views based on your views and data model
        TextView nameTextView = holder.nameTextView;
        TextView availabilityTextView = holder.availabilityTextView;
        TextView numIngredientsTextView = holder.numIngredientsTextView;
        TextView numInstructionsTextView = holder.numInstructionsTextView;

        nameTextView.setText(recipe.getName());
        numIngredientsTextView.setText("Ingredients: " +
                recipe.getIngredients().stream().mapToDouble(e -> e.getMultiplicity()).sum());
        numInstructionsTextView.setText("Instructions: " + recipe.getInstructions().size());
        String availabilityText;
        if (availability.equals("Yes")) {
            availabilityText = "<font color=\"#32CD32\">Yes</font>";
        } else {
            availabilityText = "<font color=\"#DC143C\">No</font>";
        }
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

        if (holder.getAdapterPosition() == selectedPosition) {
            if ((availabilityTextView.getText()).toString().equals("Yes")) {
                holder.nameTextView.setTextColor(Color.rgb(50, 205, 50));
                Toast.makeText(holder.itemView.getContext(),
                                "Viewing recipe: " + recipe.getName(),
                                Toast.LENGTH_SHORT)
                        .show();

                AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
                LayoutInflater inflater = fragment.requireActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_recipe, null);
                TextView nameEditText = dialogView.findViewById(R.id.display_recipe_name);
                TextView ingredientsEditText = dialogView.findViewById(R.id.display_missing_ingredients);
                TextView instructionsEditText = dialogView.findViewById(R.id.display_instructions);
//                Button cookButton = dialogView.findViewById(R.id.button_cook);


                nameEditText.setText(recipe.getName());
                String ingredientsText = "";
                for (Ingredient ingredient: recipe.getIngredients()) {
                    ingredientsText = ingredientsText
                            + ingredient.getMultiplicity() + "\t" + ingredient.getName() + "\n";
                }
                ingredientsEditText.setText(ingredientsText);
                String instructionsText = "";
                for (int i = 1; i <= recipe.getInstructions().size(); i++) {
                    instructionsText = instructionsText
                            + i + ". " + recipe.getInstructions().get(i - 1) + "\n";
                }
                instructionsEditText.setText(instructionsText);

                builder.setNeutralButton("Cook", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Implement your "Cook" action here
                        // Update the meal database
                        // Subtract ingredients from the pantry
                        // Any other related actions

                        // After cooking, reset the selected position so the recipe can be reselected
                        selectedPosition = RecyclerView.NO_POSITION;
                        // Optionally, update the UI or notify other parts of the app that cooking has occurred
                        // notifyDataSetChanged(); // If you want to refresh the whole list
                        // notifyItemChanged(position); // If you just want to refresh the cooked item
                    }
                });

                builder.setView(dialogView)
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Exit
                            holder.nameTextView.setTextColor(Color.BLACK);
                            selectedPosition = RecyclerView.NO_POSITION;
                        }
                    });

                AlertDialog dialog = builder.create();
                dialog.show();

//                cookButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // Perform the necessary actions to "cook" the recipe
//                        // For example, update the meal database and subtract ingredients from the pantry
//                        // You may need to create a new method in your ViewModel or activity to handle this logic
//
//                        Toast.makeText(fragment.getContext(), "Cooked " + recipe.getName(), Toast.LENGTH_SHORT).show();
//
//                        // Close the dialog after cooking
//                        dialog.dismiss();
//
//                        // Optionally, you can update the UI or notify other parts of the app that cooking has occurred
//                    }
//                });

            } else {
                // holder.nameTextView.setTextColor(Color.rgb(220, 20, 60));
                Toast.makeText(holder.itemView.getContext(),
                                "Not Enough Ingredients",
                                Toast.LENGTH_SHORT)
                        .show();

                AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
                LayoutInflater inflater = fragment.requireActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_recipe_missing, null);
                TextView nameEditText = dialogView.findViewById(R.id.display_recipe_name);
                TextView missingIngredientsEditText = dialogView.findViewById(R.id.display_missing_ingredients);

                nameEditText.setText(recipe.getName());

                availabilityReportGenerator.getMissingElementsForShopping(availabilityReport -> {
                    String missingIngredientsText = "";
                    for (Map.Entry<Ingredient, Double> entry: availabilityReport.get(recipe.getName()).entrySet()) {
                        missingIngredientsText += entry.getValue() + "\t" + entry.getKey().getName() + "\n";
                    }
                    missingIngredientsEditText.setText(missingIngredientsText);

                    builder.setView(dialogView).setPositiveButton("Add to shopping list", (dialog, id) -> {
                        ShoppingListViewModel shoppingListVM = new ShoppingListViewModel();

                        for (Map.Entry<Ingredient, Double> entry: availabilityReport.get(recipe.getName()).entrySet()) {
                            try {
                                String name = entry.getKey().getName();
                                double quantity = entry.getValue();
                                Ingredient newIngredient = new Ingredient(name, 0., quantity, null);
                                shoppingListVM.isItemDuplicate(name, (isDup, dupItem) -> {
                                    if (isDup) {
                                        ShoppingListManager shoppingListManager = new ShoppingListManager();
                                        shoppingListManager.addIngredientMultiplicity(name, quantity, new OnMultiplicityUpdateListener() {
                                            @Override
                                            public void onMultiplicityUpdateSuccess(GreenPlateStatus status) {

                                            }

                                            @Override
                                            public void onMultiplicityUpdateFailure(GreenPlateStatus status) {

                                            }
                                        });
                                        return;
                                    }
                                    shoppingListVM.addIngredient(newIngredient, (success, message) -> {
                                        if (!success) {
                                            Toast.makeText(holder.itemView.getContext(),
                                                    message, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(holder.itemView.getContext(),
                                                    "Successfully added missing ingredients",
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
                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Exit
                                holder.nameTextView.setTextColor(Color.BLACK);
                                selectedPosition = RecyclerView.NO_POSITION;
                            }
                        });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                });
            }
        } else {
            holder.nameTextView.setTextColor(Color.BLACK);
        }
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
}
