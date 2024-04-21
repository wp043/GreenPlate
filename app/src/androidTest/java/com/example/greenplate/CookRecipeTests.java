package com.example.greenplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.viewmodels.IngredientViewModel;
import com.example.greenplate.viewmodels.RecipeViewModel;
import com.example.greenplate.viewmodels.adapters.RecipesAdapter;
import com.example.greenplate.viewmodels.helpers.AvailabilityReportGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@RunWith(AndroidJUnit4.class)
public class CookRecipeTests {

    private static final String TEST_EMAIL = "test_account@test.com";
    private static final String TEST_PASSWORD = "password";
    private static final FirebaseAuth A1 = FirebaseAuth.getInstance();
    private static DatabaseReference ref;

    @BeforeClass
    public static void setUp() {
        final CountDownLatch latch = new CountDownLatch(1);
        A1.signInWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> signInTask) {
                        if (signInTask.isSuccessful()) {
                            latch.countDown();
                        } else {
                            latch.countDown();
                        }
                    }
                });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ref = FirebaseDatabase.getInstance()
                .getReference(String.format("user/%s/information", A1.getCurrentUser().getUid()));
    }

    @Before
    public void clearDB() {
        ref.removeValue()
                .addOnSuccessListener(e -> { })
                .addOnFailureListener(e -> {
                    throw new RuntimeException("Clear DB: " + e.getMessage());
                });
    }

    @Test
    public void subtractIngredient() {
        RecipeViewModel recipeViewModel = new RecipeViewModel();
        assertNotNull(recipeViewModel);
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Tomato", 100, 2, null));
        List<String> instructions = new ArrayList<>();
        Recipe recipe = new Recipe("Tomato Salad", ingredients, instructions);
        recipeViewModel.addRecipe(recipe, success -> {
            assertTrue("Recipe added successfully!", success);
        });

        // set multiplicity changes the amount in pantry database
        ingredients.get(0).setMultiplicity(3);
        IngredientViewModel ingredientViewModel = new IngredientViewModel();
        ingredientViewModel.addIngredient(ingredients.get(0), (success, message) -> {
            Assert.assertTrue(success);
        });

        RecipesAdapter adapter = new RecipesAdapter(Arrays.asList(recipe),
                new ArrayList<>(), null);
        adapter.updateIngredientAfterCooking(recipe,
                new RecipesAdapter.UpdateIngredientsCallback() {
                @Override
                public void onComplete(int totalCalories) { }

                @Override
                public void onError(Exception e) { }
            });
        AvailabilityReportGenerator availabilityReportGenerator
                = AvailabilityReportGenerator.getInstance();
        availabilityReportGenerator.getAvailable(report -> {
            Map<Ingredient, Double> available = report.get("Tomato Salad");
            assertEquals(1.0, available.get("Tomato"), 0.01);
        });
    }

    @Test
    public void removeIngredient() {
        RecipeViewModel recipeViewModel = new RecipeViewModel();
        assertNotNull(recipeViewModel);
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Tomato", 100, 2, null));
        List<String> instructions = new ArrayList<>();
        Recipe recipe = new Recipe("Tomato Salad", ingredients, instructions);
        recipeViewModel.addRecipe(recipe, success -> {
            assertTrue("Recipe added successfully!", success);
        });

        // set multiplicity changes the amount in pantry database
        ingredients.get(0).setMultiplicity(2);
        IngredientViewModel ingredientViewModel = new IngredientViewModel();
        ingredientViewModel.addIngredient(ingredients.get(0), (success, message) -> {
            Assert.assertTrue(success);
        });

        RecipesAdapter adapter = new RecipesAdapter(Arrays.asList(recipe),
                new ArrayList<>(), null);
        adapter.updateIngredientAfterCooking(recipe,
                new RecipesAdapter.UpdateIngredientsCallback() {
                @Override
                public void onComplete(int totalCalories) { }

                @Override
                public void onError(Exception e) { }
            });
        AvailabilityReportGenerator availabilityReportGenerator
                = AvailabilityReportGenerator.getInstance();
        availabilityReportGenerator.getAvailable(report -> {
            Map<Ingredient, Double> available = report.get("Tomato Salad");
            boolean found = false;
            for (Ingredient i : available.keySet()) {
                if (i.getName().equals("Tomato")) {
                    found = true;
                    break;
                }
            }
            assertFalse("Cannot find tomato in available ingredients", found);
        });
    }


}
