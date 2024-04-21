package com.example.greenplate;

import static org.junit.Assert.assertNotNull;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    @Test
    public void testLogin() {
        FirebaseUser currentUser = A1.getCurrentUser();
        assertNotNull(currentUser);
    }


}

