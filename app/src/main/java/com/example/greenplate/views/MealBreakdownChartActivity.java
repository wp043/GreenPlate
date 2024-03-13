package com.example.greenplate.views;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenplate.R;
import com.example.greenplate.viewmodels.MealBreakdownViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import androidx.lifecycle.ViewModelProvider;



public class MealBreakdownChartActivity extends AppCompatActivity {

    // Chart
    private PieChart pieChart;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_meal_breakdown_chart_activity);
        pieChart = findViewById(R.id.mealChart);

        BottomNavigationView btm = findViewById(R.id.bottomNavigationView);
        btm.setOnNavigationItemSelectedListener(item -> {
            Intent intent = new Intent(MealBreakdownChartActivity.this, NavBarActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            int itemId = item.getItemId();
            intent.putExtra("NAVIGATION_ID", itemId);
            startActivity(intent);
            return true;
        });
        MealBreakdownViewModel viewModel = new ViewModelProvider(this)
                .get(MealBreakdownViewModel.class);

        // Observe the LiveData for pie chart entries
        viewModel.getPieChartEntries().observe(this, pieEntries -> {
            PieDataSet dataSet = new PieDataSet(pieEntries, "Meals Today");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

            PieData data = new PieData(dataSet);
            data.setDrawValues(true);
            data.setValueFormatter(new PercentFormatter(pieChart));
            data.setValueTextSize(12f);
            data.setValueTextColor(Color.BLACK);

            pieChart.setData(data);
            pieChart.invalidate(); // Refresh the pie chart with new data
        });

        // Fetch today's meals
        viewModel.fetchMealsForToday();
    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Today's Meal Breakdown");
        pieChart.setCenterTextSize(24);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void loadPieChartData() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // Assuming you have a method to get meal data. Add your meals here.
        // For example:
        entries.add(new PieEntry(500f, "Breakfast"));
        entries.add(new PieEntry(700f, "Lunch"));
        entries.add(new PieEntry(400f, "Dinner"));
        entries.add(new PieEntry(300f, "Snacks"));

        // Add your data fetching and processing logic here.

        PieDataSet dataSet = new PieDataSet(entries, "Meals Today");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);


        pieChart.setData(data);
        pieChart.invalidate();
    }
}