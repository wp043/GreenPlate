package com.example.greenplate.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
//import android.os.Handler;
//import androidx.appcompat.app.AppCompatActivity;
//import com.example.greenplate.R;

public class SplashScreenActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
        finish();
    }
}