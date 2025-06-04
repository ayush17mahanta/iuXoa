package com.iuxoa.paradoxone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button btnStart, btnProfile, btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.btnStart);
        btnProfile = findViewById(R.id.btnProfile);
        btnExit = findViewById(R.id.btnExit);

        btnStart.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, GameActivity.class);
            startActivity(i);
        });

        btnProfile.setOnClickListener(v -> {
            showProfileDialog();
        });

        btnExit.setOnClickListener(v -> {
            finishAffinity();
        });
    }

    private void showProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile")
                .setMessage("Player name and memory will be shown here in future.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
