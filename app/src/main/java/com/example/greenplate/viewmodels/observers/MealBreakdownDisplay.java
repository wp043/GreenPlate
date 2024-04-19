package com.example.greenplate.viewmodels.observers;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.greenplate.viewmodels.MealBreakdownViewModel;
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
        // Update the appropriate values for Meal Breakdown Chart
        this.cookedMealName = cookedMealName;
        this.cookedMealCalories = cookedMealCalories;
        display();
    }

    public void display() {
        System.out.println("Inputting cooked meal " + cookedMealName
                + " with calories of " + cookedMealCalories + ".");
        MealBreakdownViewModel viewModel = new ViewModelProvider((ViewModelStoreOwner) this)
                .get(MealBreakdownViewModel.class);
        viewModel.fetchMealsForToday();
    }
}
