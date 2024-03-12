package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.greenplate.R;
import com.example.greenplate.models.Personal;
import com.example.greenplate.viewmodels.UserInfoViewModel;

public class PersonalUpdateActivity extends AppCompatActivity {
    /**
     * For editing personal information
     */
    private UserInfoViewModel userInfoVM;
    private EditText heightField;
    private EditText weightField;
    private RadioGroup genderGroup;
    private Button submitButton;
    private Button backButton;

    private TextView displayHeightField;
    private TextView displayWeightField;
    private TextView displayGenderField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info); //connect to XML file (personal_info.xml)

        userInfoVM = new ViewModelProvider(this).get(UserInfoViewModel.class);
        heightField = findViewById(R.id.editTextHeight);
        weightField = findViewById(R.id.editTextWeight);
        genderGroup = findViewById(R.id.radioGroupGender);
        submitButton = findViewById(R.id.buttonSubmit);
        backButton = findViewById(R.id.buttonBackToHome);

        displayHeightField=findViewById(R.id.currentHeight);
        displayWeightField=findViewById(R.id.currentWeight);
        displayGenderField=findViewById(R.id.currentGender);

        // Observe the LiveData from the ViewModel
        //update the personal displayed gender, height, weight if there's any change in the database
        userInfoVM.getUserPersonalInfo().observe(this, personal -> {
            if (personal != null) {
                displayHeightField.setText(personal.getHeight());
                displayWeightField.setText(personal.getWeight());
                displayGenderField.setText(personal.getGender());
            }
        });

        submitButton.setOnClickListener(v -> {
            String height = heightField.getText().toString().trim();
            String weight = weightField.getText().toString().trim();
            RadioButton selectedGenderButton = findViewById(genderGroup.getCheckedRadioButtonId());
            String gender = selectedGenderButton == null ? "" : selectedGenderButton.getText().toString();

            if (userInfoVM.validatePersonalInformation(height, weight, gender)) {
                Personal person = new Personal(height, weight, gender);
                userInfoVM.updatePersonalInformation(person);
                Toast.makeText(PersonalUpdateActivity.this, "Information updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PersonalUpdateActivity.this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(v -> finish());
    }

//    private void setSelectedGenderRadioButton(String gender) {
//        if (gender.equals("Male")) {
//            ((RadioButton)findViewById(R.id.radioButtonMale)).setChecked(true);
//        } else if (gender.equals("Female")) {
//            ((RadioButton)findViewById(R.id.radioButtonFemale)).setChecked(true);
//        }
//        // Add more conditions if there are more genders
//    }
}
