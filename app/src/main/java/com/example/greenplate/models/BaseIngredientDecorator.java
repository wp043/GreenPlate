package com.example.greenplate.models;


public abstract class BaseIngredientDecorator extends Ingredient {
    protected Displayable displayedItem;
    public BaseIngredientDecorator(Ingredient ingredient) {
        super(ingredient);
        this.displayedItem = ingredient;
    }
}
