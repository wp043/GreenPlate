package com.example.greenplate.models;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Ingredient extends RetrievableItem implements Displayable {
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

    public Ingredient(String name, double multiplicity) {
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

        return year1 == cal2.get(Calendar.YEAR)
                && month1 == cal2.get(Calendar.MONTH)
                && day1 == cal2.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public String displayInfo() {
        return String.format("Calorie: %.2f, count: %.2f, expirate date: %s",
                this.getCalories(),
                this.getMultiplicity(),
                date2Str(this.getExpirationDate()));
    }

    private static String date2Str(Date date) {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(new Date());
        currentCalendar.add(Calendar.YEAR, 5);

        if (date.after(currentCalendar.getTime())) {
            return "forever away";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(date);
    }
}
