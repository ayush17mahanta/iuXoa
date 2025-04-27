package com.iuxoa.marki;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity {

    private EditText editName, editEmail;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        saveButton = findViewById(R.id.saveButton);


        SharedPreferences prefs = getSharedPreferences("userPrefs", MODE_PRIVATE);
        editName.setText(prefs.getString("name", ""));
        editEmail.setText(prefs.getString("email", ""));

        saveButton.setOnClickListener(v -> {
            // Save changes to database or shared preferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("name", editName.getText().toString());
            editor.putString("email", editEmail.getText().toString());
            editor.apply();

            Toast.makeText(ProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
        });
    }
}