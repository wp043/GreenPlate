package com.example.greenplate.viewmodels.observers;

import com.example.greenplate.viewmodels.listeners.OnChartUpdateListenerObserver;
import com.example.greenplate.viewmodels.observable.MealCalorieData;

public class CaloriesLeftDisplay implements OnChartUpdateListenerObserver {

    // Parameters
    private String cookedMealName;
    private int cookedMealCalories;
    private MealCalorieData mealCalorieData;

    public CaloriesLeftDisplay(MealCalorieData mealCalorieData) {
        this.mealCalorieData = mealCalorieData;
        mealCalorieData.registerObserver(this);
    }

    @Override
    public void onChartUpdate(String cookedMealName, int cookedMealCalories) {
        // Update the appropriate values for the Calorie Goal Chart
//        this.cookedMealName = cookedMealName;
        this.cookedMealCalories = cookedMealCalories;
        display();
    }

    public void display() {
        System.out.println("Inputting cooked meal"
                + " with calories of " + cookedMealCalories + ".");
    }
}
