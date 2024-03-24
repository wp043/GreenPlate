package com.example.greenplate.viewmodels;

import androidx.lifecycle.ViewModel;

public class RecipeViewModel extends ViewModel {
    private CookbookManager cookbookManager;

    public RecipeViewModel() {
        cookbookManager = new CookbookManager();
    }
}
