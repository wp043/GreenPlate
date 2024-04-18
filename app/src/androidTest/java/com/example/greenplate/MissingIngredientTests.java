package com.example.greenplate;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.viewmodels.IngredientViewModel;
import com.example.greenplate.viewmodels.RecipeViewModel;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@RunWith(AndroidJUnit4.class)
public class MissingIngredientTests {
    private static final String TEST_EMAIL = "test_missing_recipe@test.com";
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
                            // Sign in successful
                            latch.countDown(); // Release the latch
                        } else {
                            // Handle sign in failure
                            latch.countDown(); // Release the latch even in failure case
                        }
                    }
                });
        try {
            latch.await(); // Wait until the latch is released
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Date date = new Date();
        String currDate = dateFormat.format(date);
        ref = FirebaseDatabase.getInstance()
                .getReference(String.format("user/%s/meals/%s/",
                        A1.getCurrentUser().getUid(), currDate));
    }

    @Before
    public void clearDB() {
        // Remove all records for the user
        ref.removeValue()
                .addOnSuccessListener(e -> {
                })
                .addOnFailureListener(e -> {
                    throw new RuntimeException("Clear DB: " + e.getMessage());
                });
    }

    @Test
    public void testGenerateAvailability() {
        RecipeViewModel recipeViewModel = new RecipeViewModel();
        assertNotNull(recipeViewModel);
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Bun", 100, 2, null));
        ingredients.add(new Ingredient("Hamburger Patty", 200, 1, null));
        ingredients.add(new Ingredient("Cheese Slice", 50, 1, null));
        List<String> instructions = new ArrayList<>();
        instructions.add("Grill hamburger patty.");
        instructions.add("Put cheese slice onto hamburger.");
        instructions.add("Put hamburger patty between buns.");
        Recipe recipe = new Recipe("Cheeseburger", ingredients, instructions);
        recipeViewModel.addRecipe(recipe, success -> {
            // Update RecyclerView
            assertTrue("Recipe added successfully!", success);
        });

        IngredientViewModel ingredientViewModel = new IngredientViewModel();
        ingredientViewModel.addIngredient(ingredients.get(0), (success, message) -> {
            Assert.assertTrue(success);
        });

        AvailabilityReportGenerator availabilityReportGenerator = AvailabilityReportGenerator.getInstance();
        availabilityReportGenerator.getMissingElementsForShopping(report -> {
            Map<Ingredient, Double> missing = report.get("Cheeseburger");
            assertEquals(2, missing.size());
        });
    }

    @Test
    public void testMissingCountInsufficient() {
        RecipeViewModel recipeViewModel = new RecipeViewModel();
        assertNotNull(recipeViewModel);
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Bun", 100, 2, null));
        ingredients.add(new Ingredient("Hamburger Patty", 200, 1, null));
        ingredients.add(new Ingredient("Cheese Slice", 50, 1, null));
        List<String> instructions = new ArrayList<>();
        instructions.add("Grill hamburger patty.");
        instructions.add("Put cheese slice onto hamburger.");
        instructions.add("Put hamburger patty between buns.");
        Recipe recipe = new Recipe("Cheeseburger", ingredients, instructions);
        recipeViewModel.addRecipe(recipe, success -> {
            // Update RecyclerView
            assertTrue("Recipe added successfully!", success);
        });

        ingredients.get(0).setMultiplicity(1);
        IngredientViewModel ingredientViewModel = new IngredientViewModel();
        ingredientViewModel.addIngredient(ingredients.get(0), (success, message) -> {
            Assert.assertTrue(success);
        });

        AvailabilityReportGenerator availabilityReportGenerator = AvailabilityReportGenerator.getInstance();
        availabilityReportGenerator.getMissingElementsForShopping(report -> {
            Map<Ingredient, Double> missing = report.get("Cheeseburger");
            assertEquals(1.0, missing.get("Bun"));
        });
    }

    @Test
    public void testMissingCountSufficient() {
        RecipeViewModel recipeViewModel = new RecipeViewModel();
        assertNotNull(recipeViewModel);
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Bun", 100, 2, null));
        ingredients.add(new Ingredient("Hamburger Patty", 200, 1, null));
        ingredients.add(new Ingredient("Cheese Slice", 50, 1, null));
        List<String> instructions = new ArrayList<>();
        instructions.add("Grill hamburger patty.");
        instructions.add("Put cheese slice onto hamburger.");
        instructions.add("Put hamburger patty between buns.");
        Recipe recipe = new Recipe("Cheeseburger", ingredients, instructions);
        recipeViewModel.addRecipe(recipe, success -> {
            // Update RecyclerView
            assertTrue("Recipe added successfully!", success);
        });

        IngredientViewModel ingredientViewModel = new IngredientViewModel();
        ingredientViewModel.addIngredient(ingredients.get(0), (success, message) -> {
            Assert.assertTrue(success);
        });
        ingredientViewModel.addIngredient(ingredients.get(1), (success, message) -> {
            Assert.assertTrue(success);
        });
        ingredientViewModel.addIngredient(ingredients.get(2), (success, message) -> {
            Assert.assertTrue(success);
        });

        AvailabilityReportGenerator availabilityReportGenerator = AvailabilityReportGenerator.getInstance();
        availabilityReportGenerator.getMissingElementsForShopping(report -> {
            Map<Ingredient, Double> missing = report.get("Cheeseburger");
            assertNull(missing);
        });
    }

    @Test
    public void testMissingCountZero() {
        RecipeViewModel recipeViewModel = new RecipeViewModel();
        assertNotNull(recipeViewModel);
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Bun", 100, 2, null));
        ingredients.add(new Ingredient("Hamburger Patty", 200, 1, null));
        ingredients.add(new Ingredient("Cheese Slice", 50, 1, null));
        List<String> instructions = new ArrayList<>();
        instructions.add("Grill hamburger patty.");
        instructions.add("Put cheese slice onto hamburger.");
        instructions.add("Put hamburger patty between buns.");
        Recipe recipe = new Recipe("Cheeseburger", ingredients, instructions);
        recipeViewModel.addRecipe(recipe, success -> {
            // Update RecyclerView
            assertTrue("Recipe added successfully!", success);
        });

        AvailabilityReportGenerator availabilityReportGenerator = AvailabilityReportGenerator.getInstance();
        availabilityReportGenerator.getMissingElementsForShopping(report -> {
            Map<Ingredient, Double> missing = report.get("Cheeseburger");
            assertEquals(3, missing.size());
        });
    }

    @Test
    public void testAddMissingIngredientsShoppingListOne() {
        RecipeViewModel recipeViewModel = new RecipeViewModel();
        assertNotNull(recipeViewModel);
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Bun", 100, 2, null));
        ingredients.add(new Ingredient("Hamburger Patty", 200, 1, null));
        ingredients.add(new Ingredient("Cheese Slice", 50, 1, null));
        List<String> instructions = new ArrayList<>();
        instructions.add("Grill hamburger patty.");
        instructions.add("Put cheese slice onto hamburger.");
        instructions.add("Put hamburger patty between buns.");
        Recipe recipe = new Recipe("Cheeseburger", ingredients, instructions);
        recipeViewModel.addRecipe(recipe, success -> {
            // Update RecyclerView
            assertTrue("Recipe added successfully!", success);
        });

        ingredients.get(0).setMultiplicity(1);
        IngredientViewModel ingredientViewModel = new IngredientViewModel();
        ingredientViewModel.addIngredient(ingredients.get(0), (success, message) -> {
            Assert.assertTrue(success);
        });
        ingredientViewModel.addIngredient(ingredients.get(1), (success, message) -> {
            Assert.assertTrue(success);
        });
        ingredientViewModel.addIngredient(ingredients.get(2), (success, message) -> {
            Assert.assertTrue(success);
        });

        AvailabilityReportGenerator availabilityReportGenerator = AvailabilityReportGenerator.getInstance();
        availabilityReportGenerator.getMissingElementsForShopping(report -> {
            Map<Ingredient, Double> missing = report.get("Cheeseburger");
            assertEquals(1, missing.size());
            assertEquals(1.0, missing.get("Bun"));
        });
    }

    @Test
    public void testAddMissingIngredientsShoppingListMultiple() {
        RecipeViewModel recipeViewModel = new RecipeViewModel();
        assertNotNull(recipeViewModel);
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Bun", 100, 2, null));
        ingredients.add(new Ingredient("Hamburger Patty", 200, 1, null));
        ingredients.add(new Ingredient("Cheese Slice", 50, 1, null));
        List<String> instructions = new ArrayList<>();
        instructions.add("Grill hamburger patty.");
        instructions.add("Put cheese slice onto hamburger.");
        instructions.add("Put hamburger patty between buns.");
        Recipe recipe = new Recipe("Cheeseburger", ingredients, instructions);
        recipeViewModel.addRecipe(recipe, success -> {
            // Update RecyclerView
            assertTrue("Recipe added successfully!", success);
        });

        ingredients.get(0).setMultiplicity(1);
        IngredientViewModel ingredientViewModel = new IngredientViewModel();
        ingredientViewModel.addIngredient(ingredients.get(0), (success, message) -> {
            Assert.assertTrue(success);
        });

        AvailabilityReportGenerator availabilityReportGenerator = AvailabilityReportGenerator.getInstance();
        availabilityReportGenerator.getMissingElementsForShopping(report -> {
            Map<Ingredient, Double> missing = report.get("Cheeseburger");
            assertEquals(3, missing.size());
            assertEquals(1.0, missing.get("Bun"));
        });
    }

    @Test
    public void testAddMissingIngredientsShoppingListAll() {
        RecipeViewModel recipeViewModel = new RecipeViewModel();
        assertNotNull(recipeViewModel);
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Bun", 100, 2, null));
        ingredients.add(new Ingredient("Hamburger Patty", 200, 1, null));
        ingredients.add(new Ingredient("Cheese Slice", 50, 1, null));
        List<String> instructions = new ArrayList<>();
        instructions.add("Grill hamburger patty.");
        instructions.add("Put cheese slice onto hamburger.");
        instructions.add("Put hamburger patty between buns.");
        Recipe recipe = new Recipe("Cheeseburger", ingredients, instructions);
        recipeViewModel.addRecipe(recipe, success -> {
            // Update RecyclerView
            assertTrue("Recipe added successfully!", success);
        });

        IngredientViewModel ingredientViewModel = new IngredientViewModel();
        ingredientViewModel.addIngredient(ingredients.get(0), (success, message) -> {
            Assert.assertTrue(success);
        });

        AvailabilityReportGenerator availabilityReportGenerator = AvailabilityReportGenerator.getInstance();
        availabilityReportGenerator.getMissingElementsForShopping(report -> {
            Map<Ingredient, Double> missing = report.get("Cheeseburger");
            assertEquals(3, missing.size());
            assertEquals(1.0, missing.get("Bun"));
        });
    }
}
