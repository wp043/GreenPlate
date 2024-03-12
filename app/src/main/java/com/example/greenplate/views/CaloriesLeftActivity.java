package com.example.greenplate.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.anychart.AnyChartView;
import com.example.greenplate.R;
import com.example.greenplate.viewmodels.CaloriesLeftViewModel;

public class CaloriesLeftActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calories_left);

        CaloriesLeftViewModel caloriesLeftViewModel = new ViewModelProvider(this).get(CaloriesLeftViewModel.class);

        // 从InputMealFragment获取卡路里摄入量和目标值
        long caloriesIntake = getIntent().getLongExtra("caloriesIntake", 0);
        int caloriesGoal = getIntent().getIntExtra("caloriesGoal", 0);

        caloriesLeftViewModel.setCaloriesIntake(caloriesIntake);
        caloriesLeftViewModel.setCaloriesGoal(caloriesGoal);

        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        anyChartView.setChart(caloriesLeftViewModel.createPieChart());
    }
}