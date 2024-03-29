package com.example.greenplate.views;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
        RecyclerView rvRecipes = (RecyclerView) view.findViewById(R.id.rvIngredients);
        addButton = view.findViewById(R.id.addButton);
        editButton = view.findViewById(R.id.editButton);

//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("MM/DD/yyyy");
//
//            // Demo ingredient list
//            List<Ingredient> ingredients = Arrays.asList(
//                    new Ingredient("Apple", 95, 2, sdf.parse("12/31/2023")),
//                    new Ingredient("Beef brisket", 44, 1, sdf.parse("05/08/2025")),
//                    new Ingredient("Salmon"),
//                    new Ingredient("Lettuce", 5, 5, sdf.parse("03/20/2024")),
//                    new Ingredient("Corn"),
//                    new Ingredient("Tomato"),
//                    new Ingredient("Milk"),
//                    new Ingredient("Butter"),
//                    new Ingredient("Tuna"),
//                    new Ingredient("Rice")
//            );
//            IngredientsAdapter adapter = new IngredientsAdapter(ingredients);
//            rvRecipes.setAdapter(adapter);
//            rvRecipes.setLayoutManager(new LinearLayoutManager(view.getContext()));
//
//
//        } catch (ParseException e) {
//
//        }
        ingredientVM.getIngredients(items -> {
            List<Ingredient> ingredients=new ArrayList<>();

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

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_ingredient, null);

                // Expiration date window
                EditText expirationEditText = dialogView.findViewById(R.id.ingredient_expiration);
                expirationEditText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        String date = (month + 1) + "/" + dayOfMonth + "/" + year;
                                        expirationEditText.setText(date);
                                    }
                                }, year, month, day);
                        datePickerDialog.show();
                    }
                });

                builder.setView(dialogView)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // Get user input
                                EditText nameEditText = dialogView.findViewById(R.id.ingredient_name);
                                EditText quantityEditText = dialogView.findViewById(R.id.ingredient_quantity);
                                EditText caloriesEditText = dialogView.findViewById(R.id.ingredient_calories);


                                String name = nameEditText.getText().toString();
                                int quantity = Integer.parseInt(quantityEditText.getText().toString());
                                int calories = Integer.parseInt(caloriesEditText.getText().toString());
                                String expiration = expirationEditText.getText().toString();

                                // change expiration string to date
                                Date expirationDate = null;
                                if (!expiration.isEmpty()) {
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                        expirationDate = sdf.parse(expiration);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Ingredient newIngredient = new Ingredient(name, calories, quantity, expirationDate);
                                // TODO: Didn't successfully add
//                                ingredientVM.addIngredient(newIngredient);
                                ingredientVM.addIngredient(newIngredient, success -> {
                                    if (success) {
                                        // If addition is successful, retrieve the updated list of ingredients
                                        ingredientVM.getIngredients(items -> {
                                            List<Ingredient> ingredients=new ArrayList<>();

                                            if (items != null) {
                                                for (RetrievableItem item : items) {
                                                    if (item instanceof Ingredient) {
                                                        Ingredient ingredient = (Ingredient) item;
                                                        ingredients.add(ingredient);
                                                    }
                                                }
                                            }

                                            // Update the RecyclerView with the updated list of ingredients
                                            IngredientsAdapter adapter = new IngredientsAdapter(ingredients);
                                            rvRecipes.setAdapter(adapter);
                                            rvRecipes.setLayoutManager(new LinearLayoutManager(requireContext()));
                                        });
                                    } else {
                                        // Handle failure to add ingredient
                                        Toast.makeText(requireContext(), "Failed to add ingredient", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Cancel
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_ingredient, null);

                // Expiration date window
                EditText expirationEditText = dialogView.findViewById(R.id.ingredient_expiration);
                expirationEditText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        String date = (month + 1) + "/" + dayOfMonth + "/" + year;
                                        expirationEditText.setText(date);
                                    }
                                }, year, month, day);
                        datePickerDialog.show();
                    }
                });

                builder.setView(dialogView)
                        .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // Get user input
                                EditText nameEditText = dialogView.findViewById(R.id.ingredient_name);
                                EditText quantityEditText = dialogView.findViewById(R.id.ingredient_quantity);
                                EditText caloriesEditText = dialogView.findViewById(R.id.ingredient_calories);


                                String name = nameEditText.getText().toString();
                                int quantity = Integer.parseInt(quantityEditText.getText().toString());
                                int calories = Integer.parseInt(caloriesEditText.getText().toString());
                                String expiration = expirationEditText.getText().toString();

                                // change expiration string to date
                                Date expirationDate = null;
                                if (!expiration.isEmpty()) {
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                        expirationDate = sdf.parse(expiration);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }

                                Ingredient newIngredient = new Ingredient(name, calories, quantity, expirationDate);
                                // TODO: Include remove ingredient logic, call removeIngredient when multiplicity sets to be 0
                                // TODO: Didn't successfully update
//                                ingredientVM.updateIngredient(newIngredient);
                                ingredientVM.updateIngredient(newIngredient);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Cancel
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}