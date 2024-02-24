package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenplate.R;
import com.example.greenplate.viewmodels.LoginViewModel;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel viewModel;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        emailEditText = findViewById(R.id.loginEmail);
        passwordEditText = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);
        viewModel = new LoginViewModel();

        loginButton.setOnClickListener(v -> {
            // Check credentials
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (email.isEmpty()) {
                Toast.makeText(getApplicationContext(),
                                "Please enter an email!",
                                Toast.LENGTH_LONG)
                        .show();
                return;
            }

            if (password.isEmpty()) {
                Toast.makeText(getApplicationContext(),
                                "Please enter an email!",
                                Toast.LENGTH_LONG)
                        .show();
                return;
            }

            viewModel.setUser(email, password);
            if (viewModel.checkPassword()) {
                // Switch activity screen to main page
            }

            Toast.makeText(getApplicationContext(),
                            "Incorrect email or password.",
                            Toast.LENGTH_LONG)
                    .show();
        });

        signupButton.setOnClickListener(v -> {
            // Go to register page
            Intent intent = new Intent(LoginActivity.this, AccountCreateActivity.class);
            startActivity(intent);
        });

    }
}
