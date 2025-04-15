package com.iuxoa.matridna;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    private List<CheckBox> checkBoxList = new ArrayList<>();
    private Button nextButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome); // ✅ Ensure this is first

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ✅ Initialize SharedPreferences once
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isWelcomeCompleted", false)) {
            navigateToMain();
            return;
        }

        // ✅ Add all CheckBoxes to the list
        checkBoxList.add(findViewById(R.id.generalHealthCheckup));
        checkBoxList.add(findViewById(R.id.basicHealthScreening));
        checkBoxList.add(findViewById(R.id.advancedHealthTest));
        checkBoxList.add(findViewById(R.id.fullBodyWellnessCheck));
        checkBoxList.add(findViewById(R.id.energyStrengthLevels));
        checkBoxList.add(findViewById(R.id.womensHealthPanel));
        checkBoxList.add(findViewById(R.id.hormonalChangesImbalance));
        checkBoxList.add(findViewById(R.id.pcosPcodScreening));
        checkBoxList.add(findViewById(R.id.pregnancyDetection));
        checkBoxList.add(findViewById(R.id.prenatalPostnatalHealth));
        checkBoxList.add(findViewById(R.id.ovulationFertilityHealth));
        checkBoxList.add(findViewById(R.id.ovulationPregnancySupport));
        checkBoxList.add(findViewById(R.id.menstrualCycleTracking));
        checkBoxList.add(findViewById(R.id.reproductiveHormoneHealth));
        checkBoxList.add(findViewById(R.id.preIVFHealthCheck));
        checkBoxList.add(findViewById(R.id.diabetesBloodSugarTest));
        checkBoxList.add(findViewById(R.id.thyroidHealthMonitoring));
        checkBoxList.add(findViewById(R.id.anemiaIronLevelsCheck));
        checkBoxList.add(findViewById(R.id.immunityVitaminDeficiency));

        // ✅ Initialize Next Button
        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> handleNextButtonClick());
    }

    private void handleNextButtonClick() {
        List<String> selectedPreferences = new ArrayList<>();

        for (CheckBox checkBox : checkBoxList) {
            if (checkBox.isChecked()) {
                selectedPreferences.add(checkBox.getText().toString());
            }
        }

        if (selectedPreferences.isEmpty()) {
            Toast.makeText(this, "Please select at least one preference.", Toast.LENGTH_SHORT).show();
        } else {
            // ✅ Save completion status
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isWelcomeCompleted", true);
            editor.apply();

            // ✅ Navigate to Login
            navigateToLogin();
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
