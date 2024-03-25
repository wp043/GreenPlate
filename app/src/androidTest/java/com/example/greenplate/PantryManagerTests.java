package com.example.greenplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Meal;
import com.example.greenplate.viewmodels.IngredientViewModel;
import com.example.greenplate.viewmodels.InputMealViewModel;
import com.example.greenplate.viewmodels.PantryManager;
import com.example.greenplate.viewmodels.listeners.OnIngredientRemoveListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

@RunWith(AndroidJUnit4.class)
public class PantryManagerTests {

    private static final String TEST_EMAIL = "test_account@test.com";
    private static final String TEST_PASSWORD = "password";
    private static final FirebaseAuth A1 = FirebaseAuth.getInstance();
    private static DatabaseReference ref;
    private PantryManager manager = new PantryManager();

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
        ref = FirebaseDatabase.getInstance()
                .getReference(String.format("user/%s/pantries/", A1.getCurrentUser().getUid()));
    }

//    @Before
//    public void clearDB() {
//        // Remove all records for the user
//        ref.removeValue()
//                .addOnSuccessListener(e -> { })
//                .addOnFailureListener(e -> {
//                    throw new RuntimeException("Clear DB: " + e.getMessage());
//                });
//    }

    @Test
    public void testLogin() {
        FirebaseUser currentUser = A1.getCurrentUser();
        assertNotNull(currentUser);
    }

    @Test
    public void testAddNullIngredient() {
        GreenPlateStatus status = manager.addIngredient(null);
        assertFalse(status.isSuccess());
        assertEquals("Can't add a null ingredient.", status.getMessage());
    }

    @Test
    public void testAddInvalidIngredient() {
        Ingredient ingredient = new Ingredient("     ", 180, 4, null);
        GreenPlateStatus status = manager.addIngredient(ingredient);
        assertFalse(status.isSuccess());
        assertEquals("Can't add a ingredient with empty name.", status.getMessage());

        ingredient = new Ingredient(null, 180, 4, null);
        status = manager.addIngredient(ingredient);
        assertFalse(status.isSuccess());
        assertEquals("Can't add a ingredient with empty name.", status.getMessage());

        ingredient = new Ingredient("Test", -180, 4, null);
        status = manager.addIngredient(ingredient);
        assertFalse(status.isSuccess());
        assertEquals("Can't add a ingredient with non-positive calorie.", status.getMessage());

        ingredient = new Ingredient("Test", 180, 0, null);
        status = manager.addIngredient(ingredient);
        assertFalse(status.isSuccess());
        assertEquals("Can't add a ingredient with non-positive multiplicity.", status.getMessage());
    }

    @Test
    public void testAddValidIngredient() {
        Ingredient ingredient = new Ingredient("Test 1", 10, 4, null);
        GreenPlateStatus status = manager.addIngredient(ingredient);
        assertTrue(status.isSuccess());
        assertEquals(String.format("%s added to database successfully", ingredient), status.getMessage());

        ingredient = new Ingredient("Test 2", 10, 3, null);
        status = manager.addIngredient(ingredient);
        assertTrue(status.isSuccess());
        assertEquals(String.format("%s added to database successfully", ingredient), status.getMessage());
    }

    @Test
    public void testAddDuplicateIngredient() {
        Ingredient ingredient = new Ingredient("Test 1", 10, 4, null);
        GreenPlateStatus status = manager.addIngredient(ingredient);
        assertTrue(status.isSuccess());
        assertEquals(String.format("%s added to database successfully", ingredient), status.getMessage());

        ingredient = new Ingredient("Test 1", 10, 3, null);
        manager.isIngredientDuplicate(ingredient, Assert::assertTrue);

        ingredient = new Ingredient("Test 2", 10, 3, null);
        manager.isIngredientDuplicate(ingredient, Assert::assertFalse);
    }

    @Test
    public void testRemoveIngredient() {
        Ingredient ingredient = new Ingredient("Test 1", 10, 4, null);
        GreenPlateStatus status = manager.addIngredient(ingredient);
        assertTrue(status.isSuccess());
        assertEquals(String.format("%s added to database successfully", ingredient), status.getMessage());

        ingredient = new Ingredient("Test 1", 10, 4, null);

        manager.removeIngredient(ingredient, new OnIngredientRemoveListener() {
            @Override
            public void onIngredientRemoveSuccess(GreenPlateStatus status) {}

            @Override
            public void onIngredientRemoveFailure(GreenPlateStatus status) {
                throw new AssertionError("Should not reach here.");
            }
        });
    }
}