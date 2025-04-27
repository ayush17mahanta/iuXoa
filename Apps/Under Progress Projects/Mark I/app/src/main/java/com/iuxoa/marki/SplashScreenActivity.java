package com.iuxoa.marki;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

public class SplashScreenActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
            String accountType = prefs.getString("accountType", "");

            if (isLoggedIn) {
                if ("client".equals(accountType)) {
                    startActivity(new Intent(SplashScreenActivity.this, ClientHomeActivity.class));
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, FreelancerHomeActivity.class));
                }
            } else {
                startActivity(new Intent(SplashScreenActivity.this, ChooseAccountTypeActivity.class));
            }
            finish();
        }, SPLASH_DELAY);
    }
}