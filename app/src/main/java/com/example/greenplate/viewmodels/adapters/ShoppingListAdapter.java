package com.example.greenplate.viewmodels.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;
import com.example.greenplate.models.Ingredient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {
    private List<Ingredient> shoppingList;

    private int selectedPosition = RecyclerView.NO_POSITION;

    public ShoppingListAdapter(List<Ingredient> shoppingList) {
        this.shoppingList = shoppingList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView infoTextView;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.ingredient_name);
            infoTextView = itemView.findViewById(R.id.ingredient_info);
            checkBox = itemView.findViewById(R.id.checkboxIngredient);
        }
    }

    @Override
    public ShoppingListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View shoppingListView = inflater.inflate(R.layout.item_shopping_list, parent, false);

        // Return a new holder instance
        ShoppingListAdapter.ViewHolder viewHolder = new ShoppingListAdapter.ViewHolder(shoppingListView);
        return viewHolder;
    }

    public List<Ingredient> getShoppingList() {
        return shoppingList;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    public void onBindViewHolder(ShoppingListAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Ingredient ingredient = shoppingList.get(position);

        // Set item views based on your views and data model
        CheckBox checkBox = holder.checkBox;
        TextView nameTextView = holder.nameTextView;
        TextView infoTextView = holder.infoTextView;
        nameTextView.setText(ingredient.getName());

        String info = String.format("count: %.2f",
                ingredient.getMultiplicity());

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

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

}
