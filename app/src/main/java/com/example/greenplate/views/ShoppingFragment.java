package com.example.greenplate.views;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.RetrievableItem;
import com.example.greenplate.viewmodels.IngredientViewModel;
import com.example.greenplate.viewmodels.RecipeViewModel;
import com.example.greenplate.viewmodels.ShoppingListViewModel;
import com.example.greenplate.viewmodels.adapters.ShoppingListAdapter;
import com.example.greenplate.viewmodels.helpers.AvailabilityReportGenerator;
import com.example.greenplate.viewmodels.listeners.OnIngredientUpdatedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingFragment extends Fragment {
    private ShoppingListViewModel shoppingListVM;
    private RecipeViewModel recipeVM;
    private IngredientViewModel ingredientVM;
    private Button addButton;
    private Button buyButton;
    private RecyclerView rvShopping;

    public ShoppingFragment() {
        recipeVM = new RecipeViewModel();
        shoppingListVM = new ShoppingListViewModel();
        ingredientVM = new IngredientViewModel();

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShoppingFragment.
     */
    // Rename and change types and number of parameters
    public static ShoppingFragment newInstance(String param1, String param2) {
        ShoppingFragment fragment = new ShoppingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shopping, container, false);
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AvailabilityReportGenerator.getInstance()
                .getMissingElementsForShopping(AvailabilityReportGenerator::logReport);
        shoppingListVM = new ShoppingListViewModel();
        ingredientVM = new IngredientViewModel();
        recipeVM = new RecipeViewModel();
        rvShopping = (RecyclerView) view.findViewById(R.id.rvIngredients);
        addButton = view.findViewById(R.id.addButton);
        buyButton = view.findViewById(R.id.buyButton);

        // Retrieve and display the list of ingredients
        retrieveAndDisplayIngredients(rvShopping);
        setupAddButton();
        setupBuyButton();
    }

    private void retrieveAndDisplayIngredients(RecyclerView rvRecipes) {
        shoppingListVM.getIngredients(items -> {
            List<Ingredient> ingredients = new ArrayList<>();
            if (items != null) {
                for (RetrievableItem item : items) {
                    if (item instanceof Ingredient) {
                        Ingredient ingredient = (Ingredient) item;
                        ingredients.add(ingredient);
                    }
                }
            }
            ShoppingListAdapter adapter = new ShoppingListAdapter(ingredients);
            rvRecipes.setAdapter(adapter);
            rvRecipes.setLayoutManager(new LinearLayoutManager(requireContext()));
        });
    }

    private void setupAddButton() {
        addButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    getContext());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_shoppinglist_ingredient, null);
            // Expiration date window


            builder.setView(dialogView).setPositiveButton("Add", (dialog, id) -> {
                // Get user input
                EditText nameEditText =
                        dialogView.findViewById(R.id.shopping_ingredient_name);
                EditText quantityEditText =
                        dialogView.findViewById(R.id.shopping_ingredient_quantity);
                EditText caloriesEditText =
                        dialogView.findViewById(R.id.shopping_ingredient_calorie);

                try {
                    String name = nameEditText.getText().toString();
                    double quantity = Double.parseDouble(quantityEditText.getText().toString());
                    double calories = Double.parseDouble(caloriesEditText.getText().toString());
                    Ingredient newIngredient = new Ingredient(name, quantity, calories, null);

                    shoppingListVM.addIngredient(newIngredient, (success, message) -> {
                        if (!success) {
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
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

    private void setupBuyButton() {
        buyButton.setOnClickListener(v -> {
            ShoppingListAdapter adapter = (ShoppingListAdapter) rvShopping.getAdapter();
            List<Ingredient> ingredients = adapter.getShoppingList();
            List<Ingredient> selectedIngredients = new ArrayList<>();

            for (int i = 0; i < ingredients.size(); i++) {
                if (adapter.isSelected(i)) {
                    selectedIngredients.add(ingredients.get(i));
                }
            }

            for (Ingredient ingredient : selectedIngredients) {
                setupBuyToIngredient(ingredient, (success, message) -> {
                    if (success) {
                        Log.d("IngredientAddition", message);
                        shoppingListVM.removeIngredient(ingredient);
                    }
                });

            }

            refreshRecycleView();

            if (getActivity() != null) {
                RecipeFragment recipeFragment = (RecipeFragment) getActivity()
                        .getSupportFragmentManager()
                        .findFragmentByTag("YourRecipeFragmentTag");
                if (recipeFragment != null) {
                    recipeVM.updateRecipeAvailability(recipeFragment.getRvRecipes(),
                            recipeFragment.getFragment());
                }
            }
        });
    }

    private void setupBuyToIngredient(Ingredient ingredient, OnIngredientUpdatedListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

        // Get user input fields
        EditText nameEditText = dialogView.findViewById(R.id.ingredient_name);
        EditText quantityEditText = dialogView.findViewById(R.id.ingredient_quantity);
        EditText caloriesEditText = dialogView.findViewById(R.id.ingredient_calories);

        // Set default values and disable editing
        nameEditText.setText(ingredient.getName());
        nameEditText.setEnabled(false);

        quantityEditText.setText(String.valueOf(ingredient.getMultiplicity()));
        quantityEditText.setEnabled(false);

        builder.setView(dialogView).setPositiveButton("Add", (dialog, id) -> {
            try {
                String name = nameEditText.getText().toString();
                double quantity = Double.parseDouble(quantityEditText.getText().toString());
                double calories = Double.parseDouble(caloriesEditText.getText().toString());
                Date expirationDate = str2Date(expirationEditText.getText().toString());
                Ingredient newIngredient = new Ingredient(name, calories, quantity, expirationDate);

                ingredientVM.addIngredientFromShoppingList(newIngredient, listener);
            } catch (Exception e) {
                Toast.makeText(requireContext(),
                        "Failed. All fields must be filled in.", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void refreshRecycleView() {
        shoppingListVM.getIngredients(items -> {
            List<Ingredient> ingredients = items
                    .stream()
                    .filter(e -> !(e instanceof Ingredient))
                    .collect(Collectors.toList())
                    .stream().
                    map(e -> (Ingredient) e)
                    .collect(Collectors.toList());

            rvShopping.setAdapter(new ShoppingListAdapter(ingredients));
            this.retrieveAndDisplayIngredients(rvShopping);
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
        String formattedDate = sdf.format(date);
        return formattedDate;
    }
}