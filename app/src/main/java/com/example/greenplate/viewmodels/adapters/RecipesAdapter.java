package com.example.greenplate.viewmodels.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;
import com.example.greenplate.models.Recipe;

import java.util.List;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {
    private List<Recipe> recipeList;

    public RecipesAdapter(List<Recipe> recipes) {
        recipeList = recipes;
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

        // Set item views based on your views and data model
        TextView nameTextView = holder.nameTextView;
        TextView availabilityTextView = holder.availabilityTextView;
        TextView numIngredientsTextView = holder.numIngredientsTextView;
        TextView numInstructionsTextView = holder.numInstructionsTextView;

        nameTextView.setText(recipe.getName());
        String availabilityText = "<font color=\"#32CD32\">Yes</font>";
        availabilityTextView.setText(Html.fromHtml(availabilityText));
        numIngredientsTextView.setText("Ingredients: " + recipe.getIngredients().size());
        numInstructionsTextView.setText("Instructions: " + recipe.getInstructions().size());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView availabilityTextView;
        public TextView numIngredientsTextView;
        public  TextView numInstructionsTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.recipe_name);
            availabilityTextView = (TextView) itemView.findViewById(R.id.recipe_availability);
            numIngredientsTextView = (TextView) itemView.findViewById(R.id.num_ingredients);
            numInstructionsTextView = (TextView) itemView.findViewById(R.id.num_instructions);
        }
    }
}
