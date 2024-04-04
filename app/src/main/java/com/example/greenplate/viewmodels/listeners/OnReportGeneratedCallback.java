package com.example.greenplate.viewmodels.listeners;

import com.example.greenplate.models.Ingredient;

import java.util.List;
import java.util.Map;

public interface OnReportGeneratedCallback {
    void onReportGenerated(Map<String, Map<Ingredient, Double>> report);
}
