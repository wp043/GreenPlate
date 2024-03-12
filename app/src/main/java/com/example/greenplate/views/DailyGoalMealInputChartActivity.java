package com.example.greenplate.views;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenplate.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;


public class DailyGoalMealInputChartActivity extends AppCompatActivity {

    // Chart
    private BarChart goalChart;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_daily_goal_meal_input_chart_activity);

        goalChart = findViewById(R.id.goalChart);
//        setupPieChart();

//        backButton = findViewById(R.id.buttonBackToHome);
//        backButton.setOnClickListener(v -> finish());

        BottomNavigationView btm = findViewById(R.id.bottomNavigationView);
        btm.setOnNavigationItemSelectedListener(item -> {
            Intent intent = new Intent(DailyGoalMealInputChartActivity.this, NavBarActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            int itemId = item.getItemId();
            intent.putExtra("NAVIGATION_ID", itemId);
            startActivity(intent);
            return true;
        });

        // Your caloric data
        float dailyGoal = 2000f; // Daily calorie goal
        float caloriesConsumed = 2000f; // Example calories consumed
        float caloriesRemaining = Math.max(dailyGoal - caloriesConsumed, 0); // Remaining calories or zero

        // Bar entries
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, caloriesConsumed));
        if (caloriesConsumed < dailyGoal) {
            entries.add(new BarEntry(1, caloriesRemaining));
        }

        // BarDataSet setup
        BarDataSet barDataSet = new BarDataSet(entries, "");
        barDataSet.setColors(new int[] {ColorTemplate.getHoloBlue(), Color.GREEN});
        barDataSet.setValueTextSize(16f);
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.0f cal", value);
            }
        });

        // BarData setup
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);
        goalChart.setData(barData);

        // Chart general settings
        goalChart.getDescription().setEnabled(false);
        goalChart.getAxisRight().setEnabled(false);
        goalChart.setExtraBottomOffset(100f); // Adjust as needed


        // YAxis setup
        YAxis leftAxis = goalChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f); // Start at zero

        // Adjust the axis maximum value to be greater than or equal to the goal or the highest value
        float axisMaximum = Math.max(caloriesConsumed, dailyGoal);
        axisMaximum += axisMaximum * 0.1; // Add 10% to the maximum for some spacing
        leftAxis.setAxisMaximum(axisMaximum);

        // LimitLine for the daily goal
        LimitLine limitLine = new LimitLine(dailyGoal);
        limitLine.setLineWidth(4f);
        limitLine.enableDashedLine(10f, 10f, 0f);
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limitLine.setTextSize(12f);
        limitLine.setTypeface(Typeface.DEFAULT_BOLD);
        limitLine.setLineColor(Color.RED);
        leftAxis.addLimitLine(limitLine);
        leftAxis.setDrawLimitLinesBehindData(false);

        // XAxis setup
        XAxis xAxis = goalChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(entries.size());
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0) return "Consumed";
                else if (value == 1) return "Remaining";
                return "";
            }
        });

        // Refresh the chart
        goalChart.invalidate();
    }

    private void setupPieChart() {

    }




//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_daily_goal_meal_input_chart_activity,
//        container, false);
//    }
}