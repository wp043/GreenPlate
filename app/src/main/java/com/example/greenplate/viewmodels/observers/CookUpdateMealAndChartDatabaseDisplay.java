package com.example.greenplate.viewmodels.observers;

import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Meal;
import com.example.greenplate.viewmodels.InputMealViewModel;
import com.example.greenplate.viewmodels.listeners.OnCookUpdateListenerObserver;
import com.example.greenplate.viewmodels.observable.MealCalorieData;

public class CookUpdateMealAndChartDatabaseDisplay implements OnCookUpdateListenerObserver {
    // Parameters
    private String cookedMealName;
    private int cookedMealCalories;
    private MealCalorieData mealCalorieData;
    public CookUpdateMealAndChartDatabaseDisplay(MealCalorieData mealCalorieData) {
        this.mealCalorieData = mealCalorieData;
        mealCalorieData.registerObserver(this);
    }

    @Override
    public void onCookUpdate(String cookedMealName, int cookedMealCalories) {
        // Update the appropriate values for Meal Breakdown Chart
        this.cookedMealName = cookedMealName;
        this.cookedMealCalories = cookedMealCalories;
        display();
    }

    public void display() {
        // Update the database with new values
        Meal currMeal = new Meal(cookedMealName,
                cookedMealCalories);
        InputMealViewModel inputMealVM = new InputMealViewModel();
        GreenPlateStatus status = inputMealVM.addMealToDatabase(currMeal);
        System.out.println("Inputting cooked meal " + cookedMealName
                + " with calories of " + cookedMealCalories + " into the database.");
    }
}
