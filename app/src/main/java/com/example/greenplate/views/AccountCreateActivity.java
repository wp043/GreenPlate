package com.example.greenplate.views;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.greenplate.R;
import com.example.greenplate.viewmodels.AccountCreationViewModel;

import java.util.Objects;

public class AccountCreateActivity extends AppCompatActivity {

    private AccountCreationViewModel accountCreateVM;
    private EditText emailField;
    private EditText passwordField;
    private EditText confirmPasswordField;
    private Button submitFormBtn;
    private ProgressBar registerProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_creation);

        accountCreateVM = new ViewModelProvider(this).get(AccountCreationViewModel.class);
        emailField = findViewById(R.id.account_create_email);
        passwordField = findViewById(R.id.account_create_password);
        submitFormBtn = findViewById(R.id.account_register_btn);
        confirmPasswordField = findViewById(R.id.account_confirm_password);
        registerProgressBar = findViewById(R.id.progressBar);
        registerProgressBar.setVisibility(View.INVISIBLE);

        submitFormBtn.setOnClickListener(l -> {
            //registerProgressBar.setVisibility(View.VISIBLE);
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            String confirmPassword = confirmPasswordField.getText().toString();

            boolean checksPassed = true;

            if (TextUtils.isEmpty(email)) {
                emailField.setError("Please enter email!");
                checksPassed = false;
            }

            if (TextUtils.isEmpty(password)) {
                passwordField.setError("Please enter password!");
                checksPassed = false;
            }

            if (!password.equals(confirmPassword)) {
                confirmPasswordField.setError("Passwords don't match");
            }

            if (!checksPassed) {
                Toast.makeText(getApplicationContext(),
                                "Please correct the errors!",
                                Toast.LENGTH_LONG)
                        .show();
                return;
            }

            hideKeyboard();

            accountCreateVM.setUser(email, password);
            accountCreateVM.createAccount(registerProgressBar, task -> {

                if (task.isSuccessful()) {
                    for (EditText e : new EditText[] {emailField, passwordField, confirmPasswordField}) {
                        e.setText("");
                    }
                    Toast.makeText(this, "Registration succeeded!", Toast.LENGTH_LONG).show();
                }
                else {

                    String error = (Objects.requireNonNull(task.getException())).getLocalizedMessage();
                    Toast toast = new Toast(this);

                    TextView textView = new TextView(this);
                    textView.setText(error);
                    textView.setMaxLines(10); // allow up to 10 lines
                    textView.setTextSize(30);
                    textView.setTextColor(Color.parseColor("#FF0000"));

                    toast.setView(textView);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.show();
                }
            });

        });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}