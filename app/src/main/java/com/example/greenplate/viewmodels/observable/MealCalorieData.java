package com.example.greenplate.viewmodels.observable;

import com.example.greenplate.viewmodels.listeners.OnCookUpdateListenerObserver;

import java.util.ArrayList;
import java.util.List;

public class MealCalorieData {
    private List<OnCookUpdateListenerObserver> observers;
    // Properties and parameters
    private String cookedMealName;
    private int cookedMealCalories;

    public MealCalorieData() {
        observers = new ArrayList<>();
    }

    public void registerObserver(OnCookUpdateListenerObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(OnCookUpdateListenerObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (OnCookUpdateListenerObserver observer : observers) {
            observer.onCookUpdate(cookedMealName, cookedMealCalories); // Add observer parameters
        }
    }

    public void setMealCalorieData(String cookedMealName, int cookedMealCalories) {
        // set the class parameters
        this.cookedMealName = cookedMealName;
        this.cookedMealCalories = cookedMealCalories;
        notifyObservers();
    }
}
