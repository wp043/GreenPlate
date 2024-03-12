package com.example.greenplate.viewmodels;

import androidx.lifecycle.ViewModel;

import com.anychart.AnyChart;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import java.util.ArrayList;
import java.util.List;

public class CaloriesLeftViewModel extends ViewModel {

    private long caloriesIntake;
    private int caloriesGoal;

    public void setCaloriesIntake(long intake) {
        caloriesIntake = intake;
    }

    public void setCaloriesGoal(int goal) {
        caloriesGoal = goal;
    }

    public Pie createPieChart() {
        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Calories Consumed", caloriesIntake));
        data.add(new ValueDataEntry("Calories Remaining", caloriesGoal - caloriesIntake));

        Pie pie = AnyChart.pie();
        pie.data(data);

        return pie;
    }
}