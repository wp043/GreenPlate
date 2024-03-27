package com.example.greenplate.models;

import androidx.annotation.Nullable;

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
    public Ingredient(String name, double calories, int multiplicity, Date expirationDate) {
        super(name, calories, multiplicity);
        this.expirationDate = expirationDate != null ? expirationDate : new Date(Long.MAX_VALUE);
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
        return that.expirationDate.equals(this.expirationDate);
    }
}
