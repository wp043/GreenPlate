package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.greenplate.R;
import com.example.greenplate.viewmodels.UserInfoViewModel;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class PersonalActivity extends AppCompatActivity {
    /**
     * For displaying personal information
     */
    private UserInfoViewModel userInfoVM;
    private TextView displayAgeField;

    private TextView displayHeightField;
    private TextView displayWeightField;
    private TextView displayGenderField;

    private Button editButton;
    private Button backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info);

        userInfoVM = new ViewModelProvider(this).get(UserInfoViewModel.class);


        displayAgeField = findViewById(R.id.currentAge);
        displayHeightField = findViewById(R.id.currentHeight);
        displayWeightField = findViewById(R.id.currentWeight);
        displayGenderField = findViewById(R.id.currentGender);
        editButton = findViewById(R.id.buttonEdit);
        backButton = findViewById(R.id.back);

        /*
        display height, weight, gender on the screen by getting the data from the database
         */
        userInfoVM.getUserAge(displayAgeField);
        userInfoVM.getUserHeight(displayHeightField);
        userInfoVM.getUserWeight(displayWeightField);
        userInfoVM.getUserGender(displayGenderField);


        /*
         * Click editButton to go to PersonalUpdateActivity
         * to edit personal information 
         */
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalActivity.this, PersonalUpdateActivity.class);
                startActivity(intent);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalActivity.this, HomeFragment.class);
                startActivity(intent);
            }
        });

        BottomNavigationView btm = findViewById(R.id.bottomNavigationView);
        btm.setOnNavigationItemSelectedListener(item -> {
            Intent intent = new Intent(PersonalActivity.this, NavBarActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            int itemId = item.getItemId();
            intent.putExtra("NAVIGATION_ID", itemId);
            startActivity(intent);
            return true;
        });
    }
}

