package com.example.greenplate.views;

import android.os.Bundle;
import com.example.greenplate.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.greenplate.databinding.NavigationBarBinding;

public class NavBarActivity extends AppCompatActivity {

//    ActivityMainBinding bind;
    NavigationBarBinding navBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navBind = NavigationBarBinding.inflate(getLayoutInflater());
        setContentView(navBind.getRoot());

        // Listeners to switch screens
        navBind.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.inputMeals:
                    break;
                case R.id.recipes:
                    break;
                case R.id.ingredients:
                    replaceFragment(new IngredientList());
                    break;
                case R.id.shoppingList:
                    replaceFragment(new ShoppingList());
                    break;
            }
            return true;
        });
    }


    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

}
