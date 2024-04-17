package com.example.greenplate.models;

import com.example.greenplate.viewmodels.helpers.DateUtils;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CalorieExpIngredientDecorator extends BaseIngredientDecorator {
    public CalorieExpIngredientDecorator(Ingredient ingredient) {
        super(ingredient);
    }

    @Override
    public String displayInfo() {
        return String.format(Locale.US, "%s, Calorie: %.2f, Expiration date: %s",
                this.displayedItem.displayInfo(), this.getCalories(),
                DateUtils.date2Str(this.getExpirationDate()));
    }
}
