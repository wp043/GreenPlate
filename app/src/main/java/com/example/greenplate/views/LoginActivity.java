package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenplate.R;
import com.example.greenplate.viewmodels.LoginViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel viewModel;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signupButton;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private boolean status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(getApplicationContext(),
                            "User already logged in.",
                            Toast.LENGTH_LONG)
                    .show();
            mAuth.signOut();
        }
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
                                "Please enter a password!",
                                Toast.LENGTH_LONG)
                        .show();
                return;
            }

            viewModel.checkUser(LoginActivity.this, email, password);

            /*
            // Attempt to write sign in logic within view
            // Correct functionality
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(getApplicationContext(),
                                                "Login successful.",
                                                Toast.LENGTH_LONG)
                                        .show();
                                Intent intent = new Intent(LoginActivity.this, NavBarActivity.class);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(getApplicationContext(),
                                                "Email or password incorrect.",
                                                Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });

             */

            /*
            // Attempt to use ViewModel to sign in
            // Incorrect functionality, have to click button twice to register
            // Sign in failed
            if (!viewModel.checkUser(LoginActivity.this, email, password)) {
                Toast.makeText(getApplicationContext(),
                                "Email or Password is invalid.",
                                Toast.LENGTH_LONG)
                        .show();
                return;
            }


            // Sign in successful
            Toast.makeText(getApplicationContext(),
                            "Login successful.",
                            Toast.LENGTH_LONG)
                    .show();
            Intent intent = new Intent(LoginActivity.this, HomeFragment.class);
            startActivity(intent);
            */
        });




            signupButton.setOnClickListener(v -> {
                // Go to register page
                Intent intent = new Intent(LoginActivity.this, AccountCreateActivity.class);
                startActivity(intent);
            });


    }
}
