package com.iuxoa.marki;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class ChooseAccountTypeActivity extends AppCompatActivity {

    private Button clientButton, freelancerButton, signOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_account_type);

        // Initialize views
        initializeViews();

        // Set click listeners
        clientButton.setOnClickListener(v -> selectRole("client"));
        freelancerButton.setOnClickListener(v -> selectRole("freelancer"));
        signOutButton.setOnClickListener(v -> signOut());
    }

    private void initializeViews() {
        clientButton = findViewById(R.id.clientButton);
        freelancerButton = findViewById(R.id.freelancerButton);
        signOutButton = findViewById(R.id.signOutButton);
    }

    private void selectRole(String role) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("accountType", role);
        editor.putBoolean("firstTimeUser", false); // just in case
        editor.apply();

        startActivity(new Intent(this, MainActivity.class));
        finish(); // prevent coming back here
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
