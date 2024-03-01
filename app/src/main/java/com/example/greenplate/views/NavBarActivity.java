package com.example.greenplate.views;

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
        navBind = NavigationBarBinding.inflate(getLayoutInflater());
        setContentView(navBind.getRoot());
        replaceFragment(new HomeFragment());

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

}
