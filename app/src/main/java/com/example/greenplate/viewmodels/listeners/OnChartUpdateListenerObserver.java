package com.example.greenplate.viewmodels.listeners;
// Observer Pattern
public interface OnChartUpdateListenerObserver {
    public void onChartUpdate(String cookedMealName, int cookedMealCalories);
}
