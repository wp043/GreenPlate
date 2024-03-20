package com.example.greenplate.models;

import androidx.annotation.Nullable;

public abstract class RetrievableItem {
    private String name;
    private double calories;
    private int multiplicity;

    /**
     * Constructor for a general RetrievableItem.
     * @param name - name of the item
     * @param calories - calories of the item
     * @param multiplicity - multiplicity of the item
     */
    public RetrievableItem(String name, double calories, int multiplicity) {
        this.name = name;
        this.calories = calories;
        this.multiplicity = multiplicity;
    }

    @Override
    public String toString() {
        return "name='" + name + '\''
                + ", calories=" + calories
                + ", multiplicity=" + multiplicity;
    }

    /**
     * Getter for name.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name.
     * @param name - name to set to
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for calories.
     * @return calories
     */
    public double getCalories() {
        return calories;
    }

    /**
     * Setter for calories.
     * @param calories - calories to set to
     */
    public void setCalories(double calories) {
        this.calories = calories;
    }

    /**
     * Getter for multiplicity.
     * @return multiplicity
     */
    public int getMultiplicity() {
        return multiplicity;
    }

    /**
     * Setter for multiplicity.
     * @param multiplicity - multiplicity to set to
     */
    public void setMultiplicity(int multiplicity) {
        this.multiplicity = multiplicity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime + (name == null ? 0 : name.hashCode());
        long temp = Double.doubleToLongBits(calories);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + multiplicity;
        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RetrievableItem that = (RetrievableItem) obj;
        return this.name.equals(that.name) && this.calories == that.calories;
    }
}
