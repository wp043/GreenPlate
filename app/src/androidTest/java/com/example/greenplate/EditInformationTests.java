package com.example.greenplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Personal;
import com.example.greenplate.viewmodels.UserInfoViewModel;
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

import java.util.concurrent.CountDownLatch;

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
                .getReference(String.format("user/%s/information", A1.getCurrentUser().getUid()));
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
        UserInfoViewModel vm = UserInfoViewModel.getInstance();
        GreenPlateStatus status = vm.updatePersonalInformation(null);
        assertFalse(status.isSuccess());
        assertEquals("Edit personal information: can't add null information",
                status.getMessage());
    }

    @Test
    public void testAddInfoWithInvalidHeight() throws InterruptedException {
        UserInfoViewModel vm = UserInfoViewModel.getInstance();
        GreenPlateStatus status = vm.updatePersonalInformation(new Personal(18, -3,
                60, "female"));
        assertFalse(status.isSuccess());
        assertEquals("Edit personal information: can't have negative height",
                status.getMessage());

        status = vm.updatePersonalInformation(new Personal(18, -100000000,
                60, "female"));
        assertFalse(status.isSuccess());
        assertEquals("Edit personal information: can't have negative height",
                status.getMessage());
    }

    @Test
    public void testAddInfoWithInvalidWeight() throws InterruptedException {
        UserInfoViewModel vm = UserInfoViewModel.getInstance();
        GreenPlateStatus status = vm.updatePersonalInformation(new Personal(70, 2,
                -10, "male"));
        assertFalse(status.isSuccess());
        assertEquals("Edit personal information: can't have negative weight",
                status.getMessage());

        status = vm.updatePersonalInformation(new Personal(18, 100000000,
                -100000000, "female"));
        assertFalse(status.isSuccess());
        assertEquals("Edit personal information: can't have negative weight",
                status.getMessage());

        status = vm.updatePersonalInformation(new Personal(18, 120,
                -1, "female"));
        assertFalse(status.isSuccess());
        assertEquals("Edit personal information: can't have negative weight",
                status.getMessage());

        status = vm.updatePersonalInformation(new Personal(18, 10,
                -5, "female"));
        assertFalse(status.isSuccess());
        assertEquals("Edit personal information: can't have negative weight",
                status.getMessage());

    }
    @Test
    public void testAddInfoWithInvalidGender() throws InterruptedException {
        UserInfoViewModel vm = UserInfoViewModel.getInstance();
        GreenPlateStatus status = vm.updatePersonalInformation(new Personal(20, 165,
                10, null));
        assertFalse(status.isSuccess());
        assertEquals("Edit personal information: can't have null gender",
                status.getMessage());
        status = vm.updatePersonalInformation(new Personal(18, 10,
                10, ""));
        assertFalse(status.isSuccess());
        assertEquals("Edit personal information: can't have null gender",
                status.getMessage());

    }
}
