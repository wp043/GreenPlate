package com.example.greenplate;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;

import androidx.test.espresso.IdlingResource;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import com.example.greenplate.R;
import org.junit.Before;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.greenplate.viewmodels.LoginViewModel;
import com.example.greenplate.views.LoginActivity;

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
    public void testIsInputDataValidForTest() {
        // Test with valid input
        assertTrue(viewModel.isInputDataValidForTest("test@example.com", "password123"));

        // Test with empty email
        assertFalse(viewModel.isInputDataValidForTest("", "password123"));

        // Test with empty password
        assertFalse(viewModel.isInputDataValidForTest("test@example.com", ""));
    }




}
