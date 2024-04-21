package com.example.greenplate.viewmodels.observers;

import android.widget.Toast;

import com.example.greenplate.viewmodels.listeners.OnCookUpdateListenerObserver;
import com.example.greenplate.viewmodels.observable.MealCalorieData;
import com.example.greenplate.views.RecipeFragment;

public class CookUpdateToastMessageDisplay implements OnCookUpdateListenerObserver {

    // Parameters
    private String cookedMealName;
    private int cookedMealCalories;
    private MealCalorieData mealCalorieData;
    private RecipeFragment fragment;

    public CookUpdateToastMessageDisplay(MealCalorieData mealCalorieData, RecipeFragment fragment) {
        this.mealCalorieData = mealCalorieData;
        this.fragment = fragment;
        mealCalorieData.registerObserver(this);
    }

    @Override
    public void onCookUpdate(String cookedMealName, int cookedMealCalories) {
        // Update the appropriate values for the Calorie Goal Chart
        this.cookedMealName = cookedMealName;
        this.cookedMealCalories = cookedMealCalories;
        display();
    }

    public void display() {
        Toast.makeText(fragment.getContext(),
                "Added cooked meal " + cookedMealName
                        + " with " + cookedMealCalories + " calories to Input Meal Screen!",
                Toast.LENGTH_SHORT).show();

        System.out.println("Displaying toast message.");
    }
}
