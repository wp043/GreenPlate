package com.example.greenplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Meal;
import com.example.greenplate.viewmodels.InputMealViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class InputMealTests {

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
        ref = FirebaseDatabase.getInstance()
                .getReference(String.format("user/%s/meals", A1.getCurrentUser().getUid()));
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

    @Test
    public void testLogin() {
        FirebaseUser currentUser = A1.getCurrentUser();
        assertNotNull(currentUser);
    }

    @Test
    public void testAddNullMeal() {
        InputMealViewModel vm = new InputMealViewModel();
        GreenPlateStatus status = vm.addMealToDatabase(null);
        assertFalse(status.isSuccess());
        assertEquals("Add meal: can't add a null meal", status.getMessage());
    }

    @Test
    public void testAddMealWithInvalidName() throws InterruptedException {
        InputMealViewModel vm = new InputMealViewModel();
        GreenPlateStatus status = vm.addMealToDatabase(new Meal(null, 50.));
        assertFalse(status.isSuccess());
        assertEquals("Add meal: can't a meal with null or blank name", status.getMessage());

        status = vm.addMealToDatabase(new Meal("", 50.));
        assertFalse(status.isSuccess());
        assertEquals("Add meal: can't a meal with null or blank name", status.getMessage());

        status = vm.addMealToDatabase(new Meal("    ", 50.));
        assertFalse(status.isSuccess());
        assertEquals("Add meal: can't a meal with null or blank name", status.getMessage());

        // Check number of records: should be 0
        // This is async, so we need a CountDownLatch
        CountDownLatch latch = new CountDownLatch(1);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long numberOfMeals = dataSnapshot.getChildrenCount();
                assertEquals(0, numberOfMeals);
                // Release the latch to signal completion
                latch.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw new AssertionError("Error when connecting with DB.");
            }
        });

        assertTrue("Latch was not released", latch.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void testAddMealWithInvalidCalorie() throws InterruptedException {
        InputMealViewModel vm = new InputMealViewModel();
        GreenPlateStatus status = vm.addMealToDatabase(new Meal("Invalid", -50.));
        assertFalse(status.isSuccess());
        assertEquals("Add meal: can't a meal with negative calorie", status.getMessage());

        // Check number of records: should be 0
        // This is async, so we need a CountDownLatch
        CountDownLatch latch = new CountDownLatch(1);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long numberOfMeals = dataSnapshot.getChildrenCount();
                assertEquals(0, numberOfMeals);
                // Release the latch to signal completion
                latch.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw new AssertionError("Error when connecting with DB.");
            }
        });

        assertTrue("Latch was not released", latch.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void testAddValidMeal() throws InterruptedException {
        InputMealViewModel vm = new InputMealViewModel();

        String format = "%s added to database successfully";

        Meal meal1 = new Meal("Test meal 1", 50.25);
        GreenPlateStatus status = vm.addMealToDatabase(meal1);
        assertTrue(status.isSuccess());
        assertEquals(String.format(format, meal1), status.getMessage());

        Meal meal2 = new Meal("Test meal 2", 47.251);
        status = vm.addMealToDatabase(meal2);
        assertTrue(status.isSuccess());
        assertEquals(String.format(format, meal2), status.getMessage());

        Meal meal3 = new Meal("Test meal 2", 100.041);
        status = vm.addMealToDatabase(meal3);
        assertTrue(status.isSuccess());
        assertEquals(String.format(format, meal3), status.getMessage());

        // Check number of records: should be 3
        // This is async, so we need a CountDownLatch
        CountDownLatch latch = new CountDownLatch(1);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long numberOfMeals = dataSnapshot.getChildrenCount();
                assertEquals(3, numberOfMeals);
                // Release the latch to signal completion
                latch.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw new AssertionError("Error when connecting with DB.");
            }
        });

        assertTrue("Latch was not released", latch.await(10, TimeUnit.SECONDS));
    }
}
