package com.example.greenplate.viewmodels.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;
import com.example.greenplate.models.Ingredient;

import java.util.List;
import java.util.Locale;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {
    private List<Ingredient> shoppingList;
    private boolean[] selectedItems;

    private int selectedPosition = RecyclerView.NO_POSITION;

    public ShoppingListAdapter(List<Ingredient> shoppingList) {
        this.shoppingList = shoppingList;
        this.selectedItems = new boolean[shoppingList.size()];
    }

    @Override
    public ShoppingListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View shoppingListView = inflater.inflate(R.layout.item_shopping_list, parent, false);

        // Return a new holder instance
        ShoppingListAdapter.ViewHolder viewHolder =
                new ShoppingListAdapter.ViewHolder(shoppingListView);
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
        TextView infoTextView = holder.infoTextView;

        String info = String.format(Locale.US, "%s, %s",
                ingredient.getName(), ingredient.displayInfo());

        infoTextView.setText(info);

        checkBox.setChecked(selectedItems[position]);
        checkBox.setOnClickListener(v -> {
            selectedItems[position] = !selectedItems[position];
            notifyItemChanged(position);
        });

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

    }

    public boolean isSelected(int position) {
        return selectedItems[position];
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView infoTextView;
        private CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            infoTextView = itemView.findViewById(R.id.ingredient_info);
            checkBox = itemView.findViewById(R.id.checkboxIngredient);
        }
    }
}