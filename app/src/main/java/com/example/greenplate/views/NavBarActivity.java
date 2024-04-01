package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.greenplate.R;
import com.example.greenplate.databinding.NavigationBarBinding;

public class NavBarActivity extends AppCompatActivity {

    // ActivityMainBinding bind;
    private NavigationBarBinding navBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        navBind = NavigationBarBinding.inflate(getLayoutInflater());
        setContentView(navBind.getRoot());
        if (extras != null && extras.getString("Fragment") != null) {
            String fragment = extras.getString("Fragment");
            if (fragment.equals("Input Meal")) {
                replaceFragment(new InputMealFragment());
            } else if (fragment.equals("Recipes")) {
                replaceFragment(new RecipeFragment());
            } else {
                replaceFragment(new HomeFragment());
            }
        } else {
            replaceFragment(new HomeFragment());
        }

        // Listeners to switch screens
        navBind.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.inputMeals) {
                replaceFragment(new InputMealFragment());
            } else if (itemId == R.id.recipes) {
                replaceFragment(new RecipeFragment());
            } else if (itemId == R.id.ingredients) {
                replaceFragment(new IngredientFragment());
            } else if (itemId == R.id.shoppingList) {
                replaceFragment(new ShoppingFragment());
            }
            return true;
        });
    }


    private void replaceFragment(Fragment fragment)  {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleNavigationIntent(intent);
    }

    private void handleNavigationIntent(Intent intent) {
        if (intent.hasExtra("NAVIGATION_ID")) {
            int navigationId = intent.getIntExtra("NAVIGATION_ID", R.id.home);
            navBind.bottomNavigationView.setSelectedItemId(navigationId);
        }
    }

}
