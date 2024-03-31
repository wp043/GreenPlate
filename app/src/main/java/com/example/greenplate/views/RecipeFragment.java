package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.viewmodels.RecipeViewModel;
import com.example.greenplate.viewmodels.adapters.RecipesAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeFragment extends Fragment {

    // Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecipeViewModel recipeViewModel;

    private ArrayList<Recipe> recipes;
    private ArrayList<Recipe> copyRecipes;

    private RecipesAdapter adapter;



    public RecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecipeFragment.
     */
    // Rename and change types and number of parameters
    public static RecipeFragment newInstance(String param1, String param2) {
        RecipeFragment fragment = new RecipeFragment();
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
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recipeViewModel = new RecipeViewModel();

        SearchView recipeListSearchView = (SearchView) view.findViewById(R.id.recipeListSearchView);
        recipeListSearchView.clearFocus();
        recipeListSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        RecyclerView rvRecipes = (RecyclerView) view.findViewById(R.id.rvRecipes);

        // Demo recipe list
        ArrayList<Recipe> recipes = new ArrayList<>();
        // Add full test recipe 1
        Map<Ingredient, Integer> ingredients1 = new HashMap<>();
        ingredients1.put(new Ingredient("Bun"), 2);
        ingredients1.put(new Ingredient("Hamburger Patty"), 1);
        List<String> instructions1 = new ArrayList<>();
        instructions1.add("Grill hamburger patty.");
        instructions1.add("Put hamburger patty between buns.");
        Recipe fullRecipe1 = new Recipe("Hamburger", ingredients1, instructions1);
        recipes.add(fullRecipe1);
        // Add full test recipe 2
        Map<Ingredient, Integer> ingredients2 = new HashMap<>();
        ingredients2.put(new Ingredient("Bun"), 1);
        ingredients2.put(new Ingredient("Sausage"), 1);
        List<String> instructions2 = new ArrayList<>();
        instructions2.add("Grill sausage.");
        instructions2.add("Put sausage into bun.");
        Recipe fullRecipe2 = new Recipe("Hot dog", ingredients2, instructions2);
        recipes.add(fullRecipe2);
        // Add dummy recipes
        Recipe recipe1 = new Recipe("Milkshake");
        Recipe recipe2 = new Recipe("Fries");
        Recipe recipe3 = new Recipe("Chicken Noodle Soup");
        Recipe recipe4 = new Recipe("Steak");
        Recipe recipe5 = new Recipe("Pasta");
        Recipe recipe6 = new Recipe("Soup");
        Recipe recipe7 = new Recipe("Salad");
        Recipe recipe8 = new Recipe("Cookie");
        Recipe recipe9 = new Recipe("Taco");
        Recipe recipe10 = new Recipe("Dumplings");
        recipes.add(recipe1);
        recipes.add(recipe2);
        recipes.add(recipe3);
        recipes.add(recipe4);
        recipes.add(recipe5);
        recipes.add(recipe6);
        recipes.add(recipe7);
        recipes.add(recipe8);
        recipes.add(recipe9);
        recipes.add(recipe10);

        // Copy of recipes arraylist
        copyRecipes = new ArrayList<>(recipes);

        // Use RecyclerView adapter to put list of recipes into RecyclerView (scrollable list)
        adapter = new RecipesAdapter(recipes);
        rvRecipes.setAdapter(adapter);
        rvRecipes.setLayoutManager(new LinearLayoutManager(view.getContext()));


        Button addRecipeButton = view.findViewById(R.id.btnEnterNewRecipe);
        addRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EnterNewRecipeActivity.class);
                startActivity(intent);
            }
        });

    }

    private void filterList(String newText) {
        ArrayList<Recipe> filteredList = new ArrayList<>();

        if (newText.isEmpty()) {
            // If the search query is empty, show the original list
            filteredList = new ArrayList<>(copyRecipes);
        } else {
            filteredList = new ArrayList<>();
            for (Recipe recipeItem : copyRecipes) {
                if (recipeItem.getName().toLowerCase().contains(newText.toLowerCase())) {
                    filteredList.add(recipeItem);
                }
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(getActivity(), "No matching recipes found", Toast.LENGTH_SHORT).show();
        }
        setFilteredList(filteredList);
    }

    private void setFilteredList (ArrayList<Recipe> filteredList) {
        this.recipes = filteredList;
        filteredList.sort((r1, r2) -> r1.getName().compareToIgnoreCase(r2.getName()));
        RecyclerView rvRecipes = requireView().findViewById(R.id.rvRecipes);
        adapter = new RecipesAdapter(filteredList);
        rvRecipes.setAdapter(adapter);
    }
}