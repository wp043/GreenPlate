package com.example.greenplate;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.greenplate.viewmodels.LoginViewModel;

@RunWith(AndroidJUnit4.class)
public class LoginTests {
    private LoginViewModel viewModel;


    @Before
    public void setUp() {
        viewModel = new LoginViewModel();
    }


    @Test
    public void testUpdateRemainingAttempts() {
        int initialAttempts = viewModel.getRemainingAttempts();
        viewModel.updateRemainingAttempts();
        assertEquals(initialAttempts - 1, viewModel.getRemainingAttempts());
    }

    @Test
    public void testEmptyEmail() {
        // Test with valid input
        assertTrue(viewModel.isInputDataValidForTest("test@example.com", "password123"));

        // Test with empty email
        assertFalse(viewModel.isInputDataValidForTest("", "password123"));

    }
    @Test
    public void testEmptyPassword() {
        // Test with valid input
        assertTrue(viewModel.isInputDataValidForTest("test@example.com", "password123"));



        assertFalse(viewModel.isInputDataValidForTest("test@example.com", ""));
    }
    @Test
    public void testInvalidPassword() {
        // Test with valid input
        assertTrue(viewModel.isInputDataValidForTest("test@example.com", "password123"));
        assertFalse(viewModel.isInputDataValidForTest("test@example.com", "123"));
    }
}
