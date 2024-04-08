package com.example.greenplate.models;

import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;

public class Ingredient extends RetrievableItem {
    private Date expirationDate;


    /**
     * Constructor for an ingredient.
     * @param name - name of the ingredient
     * @param calories - calories of the ingredient
     * @param multiplicity - multiplicity of the ingredient
     * @param expirationDate - expiration date of the ingredient. Default to infinitely away.
     */
    public Ingredient(String name, double calories, double multiplicity, Date expirationDate) {
        super(name, calories, multiplicity);
        this.expirationDate = expirationDate != null ? expirationDate : new Date(Long.MAX_VALUE);
    }

    public Ingredient(String name, double multiplicity){
        super(name, multiplicity);
    }

    public Ingredient(Ingredient toCopy) {
        super(toCopy);
        this.expirationDate = toCopy.expirationDate;
    }

    /**
     * 1-arg constructor for an ingredient
     * @param name - name of the ingredient
     */
    public Ingredient(String name) {
        this(name, 0, 1, null);
    }

    /**
     * Getter for expiration date.
     * @return the expiration date of the ingredient
     */
    public Date getExpirationDate() {
        return expirationDate;
    }

    /**
     * Setter for expiration Date.
     * @param expirationDate the expiration date to set to
     */
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + (expirationDate == null ? 0 : expirationDate.hashCode());
    }

    @Override
    public String toString() {
        return super.toString() + ", expire date: " + expirationDate.toString();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        Ingredient that = (Ingredient) obj;

        return isSameDate(this.expirationDate, that.expirationDate);
    }

    private static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        int year1 = cal1.get(Calendar.YEAR);
        int month1 = cal1.get(Calendar.MONTH);
        int day1 = cal1.get(Calendar.DAY_OF_MONTH);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

//        Log.d("AvaReport", String.format("date1=%d/%d/%d, date2=%d/%d/%d",
//                month1, day1, year1, cal2.get(Calendar.MONTH), cal2.get(Calendar.DAY_OF_MONTH),
//                cal2.get(Calendar.YEAR)));

        return year1 == cal2.get(Calendar.YEAR)
                && month1 == cal2.get(Calendar.MONTH)
                && day1 == cal2.get(Calendar.DAY_OF_MONTH);
    }
}
