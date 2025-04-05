package com.snorax;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup rgTheme;
    private Button btnSaveTheme;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize Views
        rgTheme = findViewById(R.id.rgTheme);
        btnSaveTheme = findViewById(R.id.btnSaveTheme);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("theme_prefs", MODE_PRIVATE);

        // Load the saved theme
        String savedTheme = sharedPreferences.getString("theme", "light");
        switch (savedTheme) {
            case "light":
                rgTheme.check(R.id.rbLight);
                break;
            case "dark":
                rgTheme.check(R.id.rbDark);
                break;
            case "system":
                rgTheme.check(R.id.rbSystem);
                break;
        }

        // Set up the Save Theme button
        btnSaveTheme.setOnClickListener(v -> saveTheme());
    }

    private void saveTheme() {
        String selectedTheme = "";
        int selectedId = rgTheme.getCheckedRadioButtonId();

        if (selectedId == R.id.rbLight) {
            selectedTheme = "light";
        } else if (selectedId == R.id.rbDark) {
            selectedTheme = "dark";
        } else if (selectedId == R.id.rbSystem) {
            selectedTheme = "system";
        }

        // Save the selected theme
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("theme", selectedTheme);
        editor.apply();

        // Apply the selected theme
        applyTheme(selectedTheme);

        Toast.makeText(this, "Theme saved", Toast.LENGTH_SHORT).show();
    }

    private void applyTheme(String theme) {
        switch (theme) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "system":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
}