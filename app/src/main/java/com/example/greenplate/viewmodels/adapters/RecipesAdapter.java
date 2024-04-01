package com.example.greenplate.viewmodels.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.models.RetrievableItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {
    private List<Recipe> recipeList;
    private List<String> availabilityList;
    private Fragment fragment;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public RecipesAdapter(List<Recipe> recipes, List<String> availability, Fragment fragment) {
        recipeList = recipes;
        availabilityList = availability;
        this.fragment = fragment;
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
        numIngredientsTextView.setText("Ingredients: " + recipe.getIngredients().size());
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
                TextView ingredientsEditText = dialogView.findViewById(R.id.display_ingredients);
                TextView instructionsEditText = dialogView.findViewById(R.id.display_instructions);

                nameEditText.setText(recipe.getName());
                String ingredientsText = "";
                for (Ingredient ingredient: recipe.getIngredients()) {
                    ingredientsText = ingredientsText + ingredient.getMultiplicity() + "\t" + ingredient.getName() + "\n";
                }
                ingredientsEditText.setText(ingredientsText);
                String instructionsText = "";
                for (int i = 1; i <= recipe.getInstructions().size(); i++) {
                    instructionsText = instructionsText + i + ". " + recipe.getInstructions().get(i - 1) + "\n";
                }
                instructionsEditText.setText(instructionsText);

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

            } else {
                // holder.nameTextView.setTextColor(Color.rgb(220, 20, 60));
                Toast.makeText(holder.itemView.getContext(),
                                "Not Enough Ingredients",
                                Toast.LENGTH_SHORT)
                        .show();
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
