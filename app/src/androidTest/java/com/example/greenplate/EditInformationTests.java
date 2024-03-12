package com.example.greenplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Personal;
import com.example.greenplate.viewmodels.UserInfoViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class EditInformationTests {

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
    public void testAddNullInfo() {
        UserInfoViewModel vm = new UserInfoViewModel();
        GreenPlateStatus status = vm.updatePersonalInformation(null);
        assertFalse(status.isSuccess());
        assertEquals("Edit Personal Info: can't add a null info", status.getMessage());
    }

    @Test
    public void testAddInfoWithInvalidHeight() throws InterruptedException {
        UserInfoViewModel vm = new UserInfoViewModel();
        GreenPlateStatus status = vm.updatePersonalInformation(new Personal("18", null,
                "60", "female"));
        assertFalse(status.isSuccess());
        assertEquals("Edit Personal Info: can't a personal info with null or blank height",
                status.getMessage());

        status = vm.updatePersonalInformation(new Personal("18", "", "60",
                "female"));
        assertFalse(status.isSuccess());
        assertEquals("Edit Personal Info: can't a personal info with null or blank height",
                status.getMessage());

        status = vm.updatePersonalInformation(new Personal("18", "    ", "60",
                "female"));
        assertFalse(status.isSuccess());
        assertEquals("Edit Personal Info: can't a personal info with null or blank height",
                status.getMessage());

        // Check number of records: should be 0
        // This is async, so we need a CountDownLatch
        CountDownLatch latch = new CountDownLatch(1);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long number = dataSnapshot.getChildrenCount();
                assertEquals(0, number);
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
    public void testAddInfoWithInvalidWeight() throws InterruptedException {
    }
    @Test
    public void testAddInfoWithInvalidGender() throws InterruptedException {

    }
    @Test
    public void testAddValidHeight() throws InterruptedException {
    }
}
