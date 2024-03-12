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

    private Button cancelButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info_edit); //connect to XML file (personal_info_edit.xml)

        userInfoVM = new ViewModelProvider(this).get(UserInfoViewModel.class);
        heightField = findViewById(R.id.editTextHeight);
        weightField = findViewById(R.id.editTextWeight);
        genderGroup = findViewById(R.id.radioGroupGender);
        submitButton = findViewById(R.id.buttonSubmit);
        cancelButton = findViewById(R.id.buttonCancel);

        submitButton.setOnClickListener(v -> {
            String height = heightField.getText().toString().trim();
            String weight = weightField.getText().toString().trim();
            RadioButton selectedGenderButton = findViewById(genderGroup.getCheckedRadioButtonId());
            String gender = selectedGenderButton == null ? "" : selectedGenderButton.getText().toString();

            if (userInfoVM.validatePersonalInformation(height, weight, gender)) {
                Personal person = new Personal(height, weight, gender);
                userInfoVM.updatePersonalInformation(person);
                Toast.makeText(PersonalUpdateActivity.this, "Information updated successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PersonalUpdateActivity.this, PersonalActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(PersonalUpdateActivity.this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * Click cancelButton to go back to PersonalActivity
         */
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalUpdateActivity.this, PersonalActivity.class);
                startActivity(intent);
            }
        });

    }

}
