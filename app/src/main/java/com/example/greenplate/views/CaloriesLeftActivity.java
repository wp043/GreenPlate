package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.example.greenplate.R;
import com.example.greenplate.viewmodels.InputMealViewModel;

import java.util.ArrayList;
import java.util.List;

public class CaloriesLeftActivity extends AppCompatActivity {
    private TextView backToHome;

    private static CaloriesLeftActivity caloriesLeftActivity;

    private CaloriesLeftActivity(){}

    public static CaloriesLeftActivity getCaloriesLeftActivity(){
        if(caloriesLeftActivity == null){
            caloriesLeftActivity = new CaloriesLeftActivity();
        }
        return caloriesLeftActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calories_left);

        Intent intent = getIntent();

        long caloriesIntake = intent.getLongExtra("caloriesIntake", 0);
        int caloriesGoal = intent.getIntExtra("caloriesGoal", 0);
        if(caloriesGoal < caloriesIntake){
            caloriesIntake = caloriesGoal;
        }
        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);
        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Calories Consumed", caloriesIntake));
        data.add(new ValueDataEntry("Calories left", caloriesGoal - caloriesIntake));

        Pie pie = AnyChart.pie();
        pie.data(data);

        pie.title("Calories left today");

        pie.legend()
                .position("right")
                .itemsLayout(LegendLayout.VERTICAL)
                .align(Align.CENTER);


        anyChartView.setChart(pie);
        pie.draw(true);

        backToHome = findViewById(R.id.back_to_home);

        backToHome.setOnClickListener(event -> {
            startActivity(new Intent(CaloriesLeftActivity.this,
                    HomeFragment.class));
        });

    }


}