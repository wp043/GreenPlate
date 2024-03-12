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
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.Navigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class PersonalActivity extends AppCompatActivity {
    /**
     * For displaying personal information
     */
    private UserInfoViewModel userInfoVM;

    private TextView displayHeightField;
    private TextView displayWeightField;
    private TextView displayGenderField;

    private Button editButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info); //connect to XML file (personal_info.xml)

        userInfoVM = new ViewModelProvider(this).get(UserInfoViewModel.class);


        displayHeightField=findViewById(R.id.currentHeight);
        displayWeightField=findViewById(R.id.currentWeight);
        displayGenderField=findViewById(R.id.currentGender);
        editButton=findViewById(R.id.buttonEdit);


        /**
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

        BottomNavigationView btm = findViewById(R.id.bottomNavigationView);
        btm.setOnNavigationItemSelectedListener(item -> {
            Intent intent = new Intent(PersonalActivity.this, NavBarActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            int itemId = item.getItemId();
            intent.putExtra("NAVIGATION_ID", itemId);
            startActivity(intent);
            return true;
        });




        /**
         * display height, weight, gender on the screen by getting the data from the database
         */
         userInfoVM.getUserPersonalInfo().observe(this, personal -> {
             if (personal != null) {
                 displayHeightField.setText(textSetter(personal.getHeight(),"height"));
                 displayWeightField.setText(textSetter(personal.getWeight(),"weight"));
                 displayGenderField.setText(textSetter(personal.getGender(),"gender"));
             }
         });
    }
    /**
     * private method to set the text for the TextView
     * @param txt
     * @param type: height, weight, gender
     * @return String
     */
    private String textSetter(String txt, String type){
        String frontTxt="";
        String endTxt="";
        switch(type){
            case "height":
                frontTxt="Height: ";
                endTxt="cm";
                break;
            case "weight":
                frontTxt="Weight: ";
                endTxt="kg";
                break;
            case "gender":
                frontTxt="Gender: ";
                break;
        }
        if(txt==null){
            return frontTxt+"N/A";
        }else{
            return frontTxt+txt+endTxt;
        }

    }

}
