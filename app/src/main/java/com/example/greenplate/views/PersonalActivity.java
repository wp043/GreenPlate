package com.example.greenplate.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.greenplate.R;
import com.example.greenplate.viewmodels.AccountCreationViewModel;
import com.example.greenplate.viewmodels.UserInfoViewModel;

import java.util.Objects;

public class PersonalActivity extends AppCompatActivity {
    private UserInfoViewModel userInfoVM;
    private EditText heightField;
    private EditText weightField;
    private RadioGroup genderButton;
    private Button submitFormBtn;
    private Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info);

        userInfoVM = new ViewModelProvider(this).get(UserInfoViewModel.class);
        heightField = findViewById(R.id.editTextHeight);
        weightField = findViewById(R.id.editTextWeight);
        genderButton = findViewById(R.id.radioGroupGender);
        submitFormBtn = findViewById(R.id.buttonSubmit);
        buttonBack = findViewById(R.id.buttonBackToHome);

        buttonBack.setOnClickListener(event -> {
            startActivity(new Intent(PersonalActivity.this,
                    HomeFragment.class));
        });

        submitFormBtn.setOnClickListener(l -> {
            // numbers?
            String height = heightField.getText().toString();
            String weight = weightField.getText().toString();
            int selectedGenderId = genderButton.getCheckedRadioButtonId();
            String gender = "";
            if (selectedGenderId != -1) {
                RadioButton selectedGender = findViewById(selectedGenderId);
                gender = selectedGender.getText().toString();
            }

            boolean checksPassed = true;

            if (TextUtils.isEmpty(height.trim())) {
                heightField.setError("Please enter email!");
                checksPassed = false;
            }

            if (TextUtils.isEmpty(weight.trim())) {
                weightField.setError("Please enter password!");
                checksPassed = false;
            }

            if (TextUtils.isEmpty(gender.trim())) {
                Toast.makeText(this, "Please select your gender!", Toast.LENGTH_SHORT).
                        show();
                checksPassed = false;
            }

            if (!checksPassed) {
                Toast.makeText(getApplicationContext(),
                                "Please correct the errors!",
                                Toast.LENGTH_LONG)
                        .show();
                return;
            }

            hideKeyboard();

            userInfoVM.
            userInfoVM.editUserInfo(height, weight, gender);

            // Show confirmation and possibly navigate away
            Toast.makeText(PersonalActivity.this, "Information edited successfully!",
                    Toast.LENGTH_LONG).show();
//            userInfoVM.editUserInfo(task -> {
//
//                if (task.isSuccessful()) {
//                    for (EditText e
//                            : new EditText[] {emailField, passwordField, confirmPasswordField}) {
//                        e.setText("");
//                    }
//                    Toast.makeText(this, "Edit successfully!",
//                            Toast.LENGTH_LONG).show();
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    startActivity(new Intent(AccountCreateActivity.this,
//                            NavBarActivity.class));
//                } else {
//                    String error = (Objects.requireNonNull(task.getException())).
//                            getLocalizedMessage();
//                    Toast toast = new Toast(this);
//
//                    TextView textView = new TextView(this);
//                    textView.setText(error);
//                    textView.setMaxLines(10); // allow up to 10 lines
//                    textView.setTextSize(30);
//                    textView.setTextColor(Color.parseColor("#FF0000"));
//
//                    toast.setView(textView);
//                    toast.setDuration(Toast.LENGTH_LONG);
//                    toast.show();
//                }
//            });
            startActivity(new Intent(PersonalActivity.this,
                    HomeFragment.class));
        });
    }

    /**
     * Method for closing the keyboard.
     */
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
