package com.example.greenplate.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.viewmodels.IngredientViewModel;
import com.example.greenplate.viewmodels.adapters.IngredientsAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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

//        recipeViewModel = new RecipeViewModel();
        RecyclerView rvRecipes = (RecyclerView) view.findViewById(R.id.rvIngredients);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/DD/yyyy");

            // Demo ingredient list
            List<Ingredient> ingredients = Arrays.asList(
                    new Ingredient("Apple", 95, 2, sdf.parse("12/31/2023")),
                    new Ingredient("Beef brisket", 44, 1, sdf.parse("05/08/2025")),
                    new Ingredient("Salmon"),
                    new Ingredient("Lettuce", 5, 5, sdf.parse("03/20/2024")),
                    new Ingredient("Corn"),
                    new Ingredient("Tomato"),
                    new Ingredient("Milk"),
                    new Ingredient("Butter"),
                    new Ingredient("Tuna"),
                    new Ingredient("Rice")
            );
            IngredientsAdapter adapter = new IngredientsAdapter(ingredients);
            rvRecipes.setAdapter(adapter);
            rvRecipes.setLayoutManager(new LinearLayoutManager(view.getContext()));
        } catch (ParseException e) {

        }
    }
}