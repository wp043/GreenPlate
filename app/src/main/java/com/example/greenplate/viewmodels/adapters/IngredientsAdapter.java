package com.example.greenplate.viewmodels.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;
import com.example.greenplate.models.Ingredient;

import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {
    private List<Ingredient> recipeList;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public IngredientsAdapter(List<Ingredient> recipes) {
        recipeList = recipes;
    }

    @Override
    public IngredientsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View recipeView = inflater.inflate(R.layout.item_ingredient, parent, false);

        // Return a new holder instance
        IngredientsAdapter.ViewHolder viewHolder = new IngredientsAdapter.ViewHolder(recipeView);
        return viewHolder;
    }

    public List<Ingredient> getRecipeList() {
        return recipeList;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(IngredientsAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Ingredient ingredient = recipeList.get(position);

        // Set item views based on your views and data model
        TextView nameTextView = holder.nameTextView;
        TextView infoTextView = holder.infoTextView;
        nameTextView.setText(ingredient.getName());

        String info = ingredient.displayInfo();
        infoTextView.setText(info);

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

        // Set the text color based on the selection status
        if (holder.getAdapterPosition() == selectedPosition) {
            holder.nameTextView.setTextColor(Color.RED);
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
        private TextView infoTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> {

                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    nameTextView.setTextColor(Color.RED);
                }
            });
            nameTextView = itemView.findViewById(R.id.ingredient_name);
            infoTextView = itemView.findViewById(R.id.ingredient_info);
        }
    }
}