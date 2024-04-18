package com.example.greenplate.viewmodels.observers;

import com.example.greenplate.viewmodels.listeners.OnChartUpdateListenerObserver;
import com.example.greenplate.viewmodels.observable.MealCalorieData;

public class MealBreakdownDisplay implements OnChartUpdateListenerObserver {
    // Parameters
    private String cookedMealName;
    private int cookedMealCalories;
    private MealCalorieData mealCalorieData;
    public MealBreakdownDisplay(MealCalorieData mealCalorieData) {
        this.mealCalorieData = mealCalorieData;
        mealCalorieData.registerObserver(this);
    }

    @Override
    public void onChartUpdate(String cookedMealName, int cookedMealCalories) {
        // Update the appropriate database values for Meal Breakdown Chart
        this.cookedMealName = cookedMealName;
        this.cookedMealCalories = cookedMealCalories;
        display();
    }

    public void display() {
        System.out.println("Inputting cooked meal " + cookedMealName
                + " with calories of " + cookedMealCalories + ".");
    }
}
