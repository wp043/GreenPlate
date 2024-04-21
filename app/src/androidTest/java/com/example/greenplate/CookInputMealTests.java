package com.example.greenplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Meal;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.viewmodels.InputMealViewModel;
import com.example.greenplate.viewmodels.RecipeViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

@RunWith(AndroidJUnit4.class)
public class CookInputMealTests {

    private static final String TEST_EMAIL = "test_account@test.com";
    private static final String TEST_PASSWORD = "password";
    private static final FirebaseAuth A1 = FirebaseAuth.getInstance();
    private static DatabaseReference ref;

    @BeforeClass
    public static void setUp() {
        final CountDownLatch latch = new CountDownLatch(1);
        A1.signInWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD)
                .addOnCompleteListener(signInTask -> latch.countDown());
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
                .addOnSuccessListener(e -> { })
                .addOnFailureListener(e -> {
                    throw new RuntimeException("Clear DB: " + e.getMessage());
                });
    }

    // Tests
    // Cook successfully adds to database
    // Cook meal add calories
    // Cook meal add meal

    // Cook Successfully Adds something to the Database
    @Test
    public void cookSuccessfullyAddsToDatabase() {

        // CountDownLatch to wait for Firebase operations
        final CountDownLatch firebaseLatch = new CountDownLatch(1);

        // Variables for expected results
        final int expectedCalories = 600;  // Assuming 200 calories per unit of ingredient
        // and 3 units used

        // Assuming each ingredient's calories are multiplied by its multiplicity in the
        // recipe calculations
        RecipeViewModel recipeViewModel = new RecipeViewModel();
        assertNotNull(recipeViewModel);
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Mcdonalds", 600, 10, null));
        List<String> instructions = new ArrayList<>();
        Recipe recipe = new Recipe("Stack of McDonalds", ingredients, instructions);
        recipeViewModel.addRecipe(recipe, success -> {
            assertTrue("Recipe added successfully!", success);
        });

        // Add to input meal database
        Meal currMeal = new Meal(recipe.getName(), recipe.getCalories());
        InputMealViewModel inputMealVM = new InputMealViewModel();
        GreenPlateStatus status = inputMealVM.addMealToDatabase(currMeal);

        // Testing to see if added successfully via status message
        assertTrue(status.isSuccess());


    }


    @Test
    public void cookAddMealCalories() {

        // CountDownLatch to wait for Firebase operations
        final CountDownLatch firebaseLatch = new CountDownLatch(1);

        // Variables for expected results
        final int expectedCalories = 600;  // Assuming 200 calories per unit of ingredient
        // and 3 units used

        // Assuming each ingredient's calories are multiplied by its multiplicity in the
        // recipe calculations
        RecipeViewModel recipeViewModel = new RecipeViewModel();
        assertNotNull(recipeViewModel);
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Rice", 200, 3, null));
        List<String> instructions = new ArrayList<>();
        Recipe recipe = new Recipe("A lot of Rice", ingredients, instructions);
        recipeViewModel.addRecipe(recipe, success -> {
            assertTrue("Recipe added successfully!", success);
        });

        // Add to input meal database
        Meal currMeal = new Meal(recipe.getName(), recipe.getCalories());
        InputMealViewModel inputMealVM = new InputMealViewModel();
        GreenPlateStatus status = inputMealVM.addMealToDatabase(currMeal);

        // Testing to see if added successfully via status message
        assertTrue(status.isSuccess());

        // Fetch the meal data from Firebase and verify calories
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String formattedDate = new SimpleDateFormat("MM-dd-yyyy",
                        Locale.getDefault()).format(new Date());

                for (DataSnapshot mealSnapshot : snapshot.child("meals")
                        .child(formattedDate).getChildren()) {
                    String mealName = mealSnapshot.child("name").getValue(String.class);
                    Long calories = mealSnapshot.child("calories").getValue(Long.class);
                    if (mealSnapshot.child("name").getValue(String.class)
                            .equals("A lot of Rice")) {
                        // If the added calories are the same
                        double testCookedMealCalories = mealSnapshot.child("calories")
                                .getValue(Double.class);
                        assertEquals(600, testCookedMealCalories);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // No need to implement
            }
        });
    }

    @Test
    public void cookAddMealName() {

        // CountDownLatch to wait for Firebase operations
        final CountDownLatch firebaseLatch = new CountDownLatch(1);

        // Variables for expected results
        final int expectedCalories = 600;  // Assuming 200 calories per unit of ingredient
        // and 3 units used

        // Assuming each ingredient's calories are multiplied by its multiplicity in the
        // recipe calculations
        RecipeViewModel recipeViewModel = new RecipeViewModel();
        assertNotNull(recipeViewModel);
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Hot Dog", 600, 10, null));
        List<String> instructions = new ArrayList<>();
        Recipe recipe = new Recipe("A lot of Hot Dogs", ingredients, instructions);
        recipeViewModel.addRecipe(recipe, success -> {
            assertTrue("Recipe added successfully!", success);
        });

        // Add to input meal database
        Meal currMeal = new Meal(recipe.getName(), recipe.getCalories());
        InputMealViewModel inputMealVM = new InputMealViewModel();
        GreenPlateStatus status = inputMealVM.addMealToDatabase(currMeal);

        // Testing to see if added successfully via status message
        assertTrue(status.isSuccess());

        // Fetch the meal data from Firebase and verify added cooked recipe and meal name
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String formattedDate = new SimpleDateFormat("MM-dd-yyyy",
                        Locale.getDefault()).format(new Date());

                for (DataSnapshot mealSnapshot : snapshot.child("meals")
                        .child(formattedDate).getChildren()) {
                    String mealName = mealSnapshot.child("name").getValue(String.class);
                    if (mealSnapshot.child("name").getValue(String.class)
                            .equals("A lot of Hot Dogs")) {
                        String cookedRecipeInDatabase = mealSnapshot.child("name")
                                .getValue(String.class);
                        assertEquals("A lot of Hot Dogs", cookedRecipeInDatabase);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // No need to implement
            }
        });
    }
}

