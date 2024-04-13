package com.example.greenplate.views;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;
import com.example.greenplate.models.ExpirationWarningIngredientDecorator;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.models.UsageIngredientDecorator;
import com.example.greenplate.viewmodels.IngredientViewModel;
import com.example.greenplate.viewmodels.adapters.IngredientsAdapter;
import com.example.greenplate.viewmodels.managers.CookbookManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class IngredientFragment extends Fragment {

    private IngredientViewModel ingredientVM;
    private Button addButton;
    private Button editButton;
    private RecyclerView rvRecipes;
    private CheckBox showRecipe;

    public IngredientFragment() {
        ingredientVM = new IngredientViewModel();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ingredient, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ingredientVM = new IngredientViewModel();
        rvRecipes = view.findViewById(R.id.rvIngredients);
        addButton = view.findViewById(R.id.addButton);
        editButton = view.findViewById(R.id.editButton);
        showRecipe = view.findViewById(R.id.show_recipe_checkBox);

        showRecipe.setOnCheckedChangeListener((buttonView, isChecked) -> {
            retrieveAndDisplayIngredients(rvRecipes, isChecked);
        });

        // Retrieve and display the list of ingredients
        retrieveAndDisplayIngredients(rvRecipes, showRecipe.isChecked());
        setupAddButton();
        setupEditButton();
    }

    private void retrieveAndDisplayIngredients(RecyclerView rvRecipes, boolean includeRecipe) {
        if (!includeRecipe) {
            setBasicIngredients(rvRecipes, null);
        } else {
            new CookbookManager().retrieve(recipeItems -> {
                List<Recipe> allRecipes = recipeItems.stream().map(e -> (Recipe) e)
                        .collect(Collectors.toList());
                setBasicIngredients(rvRecipes, allRecipes);
            });
        }
    }

    private void setBasicIngredients(RecyclerView rvRecipes, List<Recipe> allRecipes) {
        ingredientVM.getIngredients(items -> {
            List<Ingredient> ingredients = items.stream()
                    .map(e -> (Ingredient) e).collect(Collectors.toList());

            ingredients.replaceAll(ExpirationWarningIngredientDecorator::new);

            if (allRecipes != null) {
                ingredients.replaceAll(ingredient ->
                        new UsageIngredientDecorator(ingredient, allRecipes));
            }

            IngredientsAdapter adapter = new IngredientsAdapter(ingredients);
            rvRecipes.setAdapter(adapter);
            rvRecipes.setLayoutManager(new LinearLayoutManager(requireContext()));
        });
    }

    private void setupAddButton() {
        addButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    getContext());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_ingredient, null);
            // Expiration date window
            EditText expirationEditText = dialogView.findViewById(R.id.ingredient_expiration);
            expirationEditText.setOnClickListener(v1 -> {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year1, month1, dayOfMonth) -> {
                        String date = (month1 + 1) + "/" + dayOfMonth + "/" + year1;
                        expirationEditText.setText(date);
                    }, year, month, day);
                datePickerDialog.show();
            });

            builder.setView(dialogView).setPositiveButton("Add", (dialog, id) -> {
                // Get user input
                EditText nameEditText =
                        dialogView.findViewById(R.id.ingredient_name);
                EditText quantityEditText =
                        dialogView.findViewById(R.id.ingredient_quantity);
                EditText caloriesEditText =
                        dialogView.findViewById(R.id.ingredient_calories);

                try {
                    String name = nameEditText.getText().toString();
                    double quantity = Double.parseDouble(quantityEditText.getText().toString());
                    double calories = Double.parseDouble(caloriesEditText.getText().toString());
                    Date expirationDate = str2Date(expirationEditText.getText().toString());
                    Ingredient newIngredient = new Ingredient(name,
                            calories, quantity, expirationDate);

                    ingredientVM.addIngredient(newIngredient, (success, message) -> {
                        if (!success) {
                            Toast.makeText(requireContext(), message,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        refreshRecycleView();
                    });
                } catch (Exception e) {
                    Toast.makeText(requireContext(),
                            "Failed. All fields must be filled in.",
                            Toast.LENGTH_SHORT).show();
                }
            }).setNegativeButton("Cancel", (dialog, id) -> { });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private void setupEditButton() {
        editButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_ingredient, null);

            IngredientsAdapter oldAdapter = (IngredientsAdapter) rvRecipes.getAdapter();
            if (oldAdapter.getSelectedPosition() < 0
                    || oldAdapter.getSelectedPosition() >= oldAdapter.getRecipeList().size()) {
                Toast.makeText(requireContext(),
                        "Please select an item to update!",
                        Toast.LENGTH_LONG).show();
                return;
            }
            Ingredient selectedIngredient = oldAdapter.getRecipeList()
                    .get(oldAdapter.getSelectedPosition());

            // Expiration date window
            EditText expirationEditText = dialogView.findViewById(R.id.ingredient_expiration);
            EditText nameEditText = dialogView.findViewById(R.id.ingredient_name);
            EditText quantityEditText = dialogView.findViewById(R.id.ingredient_quantity);
            EditText caloriesEditText = dialogView.findViewById(R.id.ingredient_calories);

            nameEditText.setText(selectedIngredient.getName());
            nameEditText.setEnabled(false);

            caloriesEditText.setText(String.valueOf(selectedIngredient.getCalories()));
            caloriesEditText.setEnabled(false);

            expirationEditText.setText(date2Str(selectedIngredient.getExpirationDate()));
            expirationEditText.setEnabled(false);

            expirationEditText.setOnClickListener(v1 -> {
                Calendar calendar = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year1, month1, dayOfMonth) -> {
                        String date = (month1 + 1) + "/" + dayOfMonth + "/" + year1;
                        expirationEditText.setText(date);
                    }, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            });

            builder.setView(dialogView)
                    .setPositiveButton("Edit", (dialog, id) -> {
                        try {
                            String name = nameEditText.getText().toString();
                            double quantity =
                                    Double.parseDouble(quantityEditText.getText().toString());
                            double calories =
                                    Double.parseDouble(caloriesEditText.getText().toString());
                            Date expirationDate = str2Date(expirationEditText.getText().toString());

                            Ingredient newIngredient = new Ingredient(name, calories, quantity,
                                    expirationDate);

                            ingredientVM.updateIngredient(newIngredient, (success, message) -> {
                                if (!success) {
                                    Toast.makeText(requireContext(), message,
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                refreshRecycleView();
                            });
                        } catch (Exception e) {
                            Toast.makeText(requireContext(),
                                    "Failed. All fields must be filled in.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("Cancel", (dialog, id) -> { });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private void refreshRecycleView() {
        ingredientVM.getIngredients(items -> {
            List<Ingredient> ingredients = items
                    .stream()
                    .filter(e -> !(e instanceof Ingredient))
                    .collect(Collectors.toList())
                    .stream().
                    map(e -> (Ingredient) e)
                    .collect(Collectors.toList());

            rvRecipes.setAdapter(new IngredientsAdapter(ingredients));
            this.retrieveAndDisplayIngredients(rvRecipes, showRecipe.isChecked());
        });
    }

    private static Date str2Date(String str) throws ParseException {
        Date d = null;
        if (!str.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            d = sdf.parse(str);
        }
        return d == null ? new Date(Long.MAX_VALUE) : d;
    }

    private static String date2Str(Date date) {
        if (date.getTime() == Long.MAX_VALUE) {
            return "forever away";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(date);
    }
}