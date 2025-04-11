package com.iuxoa.irshieldx;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Reference to the LinearLayout with ID 'main_layout'
        LinearLayout mainLayout = findViewById(R.id.main_layout);

        // Make the layout visible after a short delay
        new Handler().postDelayed(() -> {
            mainLayout.setVisibility(LinearLayout.VISIBLE);

            // Redirect to MainActivity after displaying the splash screen
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 3000); // 3-second delay
    }
}
