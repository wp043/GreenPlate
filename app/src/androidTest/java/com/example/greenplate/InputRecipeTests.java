package com.example.greenplate;

import static org.junit.Assert.assertFalse;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.viewmodels.RecipeViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@RunWith(AndroidJUnit4.class)
public class InputRecipeTests {

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
    public void testAddNullRecipe() {
        RecipeViewModel vm = new RecipeViewModel();
        Recipe nullRecipe = null;
        final boolean[] isSuccessful = {false};

        vm.addRecipe(nullRecipe, success -> {
            isSuccessful[0] = success;
        });

        assertFalse(isSuccessful[0]);
    }

    @Test
    public void testAddRecipeWithInvalidQuantity() {
        // Arrange
        RecipeViewModel vm = new RecipeViewModel();
        String name = "test1";
        List<Ingredient> ingredients = new ArrayList<>();
        Ingredient ingredient1 = new Ingredient("test ingredient 1", 100, 0, null);
        ingredients.add(ingredient1);
        List<String> instructions = new ArrayList<>();
        instructions.add("test instruction 1");
        instructions.add("test instruction 2");
        Recipe testRecipe = new Recipe(name, ingredients, instructions);

        final boolean[] isSuccessful = {false}; // Use an array to hold the result
        final String[] message = {null}; // Use an array to hold the message

        // Act
        vm.addRecipe(testRecipe, success -> {
            isSuccessful[0] = success;
            assertFalse("Expected the recipe addition to fail "
                    + "due to invalid quantity.", isSuccessful[0]);
        });


        String name2 = "test2";
        List<Ingredient> ingredients2 = new ArrayList<>();
        Ingredient ingredient2 = new Ingredient("test ingredient 2", 100, -3, null);
        ingredients2.add(ingredient2);
        List<String> instructions2 = new ArrayList<>();
        instructions2.add("test instruction 3");
        instructions2.add("test instruction 4");
        Recipe testRecipe2 = new Recipe(name2, ingredients2, instructions2);

        final boolean[] isSuccessful2 = {false}; // Use an array to hold the result
        final String[] message2 = {null}; // Use an array to hold the message

        // Act
        vm.addRecipe(testRecipe2, success -> {
            isSuccessful[0] = success;
            assertFalse("Expected the recipe addition to "
                    + "fail due to invalid quantity.", isSuccessful[0]);
        });

    }


}