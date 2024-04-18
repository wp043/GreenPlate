package com.example.greenplate.viewmodels.observable;

import com.example.greenplate.viewmodels.listeners.OnChartUpdateListenerObserver;

import java.util.ArrayList;
import java.util.List;

public class MealCalorieData {
    private List<OnChartUpdateListenerObserver> observers;
    // Properties and parameters
    private String cookedMealName;
    private int cookedMealCalories;

    public MealCalorieData() {
        observers = new ArrayList<>();
    }

    public void registerObserver(OnChartUpdateListenerObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(OnChartUpdateListenerObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (OnChartUpdateListenerObserver observer : observers) {
            observer.onChartUpdate(cookedMealName, cookedMealCalories); // Add observer parameters
        }
    }

    public void setMealCalorieData(String cookedMealName, int cookedMealCalories) {
        // set the class parameters
        this.cookedMealName = cookedMealName;
        this.cookedMealCalories = cookedMealCalories;
        notifyObservers();
    }
}
