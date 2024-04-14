package com.example.greenplate.models;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ExpirationWarningIngredientDecorator extends BaseIngredientDecorator {
    public ExpirationWarningIngredientDecorator(Ingredient ingredient) {
        super(ingredient);
    }

    @Override
    public String displayInfo() {
        Date today = new Date();
        Date expirationDate = this.getExpirationDate();

        long differenceInMillis = expirationDate.getTime() - today.getTime();
        long daysDifference = TimeUnit.DAYS.convert(differenceInMillis, TimeUnit.MILLISECONDS);

        String symbol = " ";
        if (daysDifference < 0) {
            symbol += "❌";
        } else if (daysDifference <= 5) {
            symbol += "⚠️";
        } else {
            symbol += "✅";
        }
        return this.displayedItem.displayInfo() + symbol;
    }
}
