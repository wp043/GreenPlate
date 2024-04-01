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
import com.example.greenplate.viewmodels.adapters.IngredientsAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IngredientFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IngredientFragment extends Fragment {

    // Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private IngredientViewModel ingredientVM;

    private Button addButton;
    private Button editButton;
    private RecyclerView rvRecipes;

    public IngredientFragment() {
        ingredientVM = new IngredientViewModel();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IngredientFragment.
     */
    // Rename and change types and number of parameters
    public static IngredientFragment newInstance(String param1, String param2) {
        IngredientFragment fragment = new IngredientFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        rvRecipes = (RecyclerView) view.findViewById(R.id.rvIngredients);
        addButton = view.findViewById(R.id.addButton);
        editButton = view.findViewById(R.id.editButton);

        // Retrieve and display the list of ingredients
        retrieveAndDisplayIngredients(rvRecipes);
        setupAddButton();
        setupEditButton();
    }

    private void retrieveAndDisplayIngredients(RecyclerView rvRecipes) {
        ingredientVM.getIngredients(items -> {
            List<Ingredient> ingredients = new ArrayList<>();
            if (items != null) {
                for (RetrievableItem item : items) {
                    if (item instanceof Ingredient) {
                        Ingredient ingredient = (Ingredient) item;
                        ingredients.add(ingredient);
                    }
                }
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
            Log.d("TAG", "-1");
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
                    int quantity = Integer.parseInt(quantityEditText.getText().toString());
                    int calories = Integer.parseInt(caloriesEditText.getText().toString());
                    Date expirationDate = str2Date(expirationEditText.getText().toString());
                    Ingredient newIngredient = new Ingredient(name,
                            calories, quantity, expirationDate);

                    ingredientVM.addIngredient(newIngredient, success -> {
                        if (!success) {
                            Toast.makeText(requireContext(), "Failed to add ingredient",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        refreshRecycleView();
                    });
                } catch (Exception e) {
                    Log.d("TAG", e.getLocalizedMessage());
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
                            int quantity = Integer.parseInt(quantityEditText.getText().toString());
                            double calories =
                                    Double.parseDouble(caloriesEditText.getText().toString());
                            Date expirationDate = str2Date(expirationEditText.getText().toString());

                            Ingredient newIngredient = new Ingredient(name, calories, quantity,
                                    expirationDate);

                            ingredientVM.updateIngredient(newIngredient, success -> {
                                if (!success) {
                                    Toast.makeText(requireContext(),
                                            "Failed. Name, Calorie, "
                                                    + "expiration date must match "
                                                    + "the ingredient to be edited.",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                refreshRecycleView();
                            });
                        } catch (Exception e) {
                            Log.d("TAG", e.getMessage());
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
            this.retrieveAndDisplayIngredients(rvRecipes);
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