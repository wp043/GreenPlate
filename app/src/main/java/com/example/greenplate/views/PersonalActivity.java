//package com.example.greenplate.views;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.lifecycle.ViewModelProvider;
//
//import com.example.greenplate.R;
//import com.example.greenplate.models.GreenPlateStatus;
//import com.example.greenplate.models.Personal;
//import com.example.greenplate.viewmodels.UserInfoViewModel;
//
//public class PersonalActivity extends AppCompatActivity {
//    private UserInfoViewModel userInfoVM;
//    private EditText heightField;
//    private EditText weightField;
//    private RadioGroup genderGroup;
//    private Button submitButton;
//    private Button backButton;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.personal_info);
//
//        userInfoVM = new ViewModelProvider(this).get(UserInfoViewModel.class);
//
//        TextView currheight = findViewById(R.id.currentHeight);
//        TextView currweight = findViewById(R.id.currentWeight);
//        TextView currgender = findViewById(R.id.currentGender);
//        //display current info
//        userInfoVM.getUserHeight(currheight).observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(String height) {
//                currheight.setText(height);
//            }
//        });
//        userInfoVM.getUserWeight(currweight);
//        userInfoVM.getUserGender(currgender);
//
//        //update information as needed
//        heightField = findViewById(R.id.editTextHeight);
//        weightField = findViewById(R.id.editTextWeight);
//        genderGroup = findViewById(R.id.radioGroupGender);
//        submitButton = findViewById(R.id.buttonSubmit);
//        backButton = findViewById(R.id.buttonBackToHome);
//        submitButton.setOnClickListener(v -> {
//            String height = heightField.getText().toString().trim();
//            String weight = weightField.getText().toString().trim();
//            int selectedGenderId = genderGroup.getCheckedRadioButtonId();
//            RadioButton selectedGenderButton = findViewById(selectedGenderId);
//            String gender = selectedGenderButton == null ? "" : selectedGenderButton.getText().toString();
//
//            if (!userInfoVM.isInputDataValid(height, weight, gender, heightField, weightField, selectedGenderButton)) {
//                return;
//            }
//
//            // Update the user info in ViewModel
//            Personal person = new Personal(height, weight, gender);
//            userInfoVM.editPersonalInformation(person);
//
//            Intent intent = new Intent(PersonalActivity.this, HomeFragment.class);
//            startActivity(intent);
//        });
//
//        backButton.setOnClickListener(v -> {
//            // Go to create account page
//            Intent intent = new Intent(PersonalActivity.this, HomeFragment.class);
//            startActivity(intent);
//        });
//    }
//
//    private void setSelectedGenderRadioButton(String gender) {
//        switch (gender) {
//            case "Male":
//                ((RadioButton)findViewById(R.id.radioButtonMale)).setChecked(true);
//                break;
//            case "Female":
//                ((RadioButton)findViewById(R.id.radioButtonFemale)).setChecked(true);
//                break;
//            default:
//                break;
//        }
//    }
//
//
//    // Method for closing the keyboard in fragment.
//    private void hideKeyboardFrom(Context context, View view) {
//        InputMethodManager imm = (InputMethodManager)
//                context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//    }
//}
package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.greenplate.R;
import com.example.greenplate.models.Personal;
import com.example.greenplate.viewmodels.UserInfoViewModel;

public class PersonalActivity extends AppCompatActivity {
    private UserInfoViewModel userInfoVM;
    private EditText heightField;
    private EditText weightField;
    private RadioGroup genderGroup;
    private Button submitButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info);

        userInfoVM = new ViewModelProvider(this).get(UserInfoViewModel.class);
        heightField = findViewById(R.id.editTextHeight);
        weightField = findViewById(R.id.editTextWeight);
        genderGroup = findViewById(R.id.radioGroupGender);
        submitButton = findViewById(R.id.buttonSubmit);
        backButton = findViewById(R.id.buttonBackToHome);

        // Observe the LiveData from the ViewModel
        userInfoVM.getUserPersonalInfo().observe(this, personal -> {
            if (personal != null) {
                heightField.setText(personal.getHeight());
                weightField.setText(personal.getWeight());
                setSelectedGenderRadioButton(personal.getGender());
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
                Toast.makeText(PersonalActivity.this, "Information updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PersonalActivity.this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(v -> finish());
    }

    private void setSelectedGenderRadioButton(String gender) {
        if (gender.equals("Male")) {
            ((RadioButton)findViewById(R.id.radioButtonMale)).setChecked(true);
        } else if (gender.equals("Female")) {
            ((RadioButton)findViewById(R.id.radioButtonFemale)).setChecked(true);
        }
        // Add more conditions if there are more genders
    }
}
