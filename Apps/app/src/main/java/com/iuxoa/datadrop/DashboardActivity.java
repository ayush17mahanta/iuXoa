package com.iuxoa.datadrop;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;


public class DashboardActivity extends AppCompatActivity {

    Button goToWallet, goToSurveys, logout;
    TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboardLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        goToWallet = findViewById(R.id.goToWallet);
        goToSurveys = findViewById(R.id.goToSurveys);
        logout = findViewById(R.id.logout);
        welcomeText = findViewById(R.id.welcomeText);

        goToWallet.setOnClickListener(v ->
                startActivity(new Intent(this, WalletActivity.class)));

        goToSurveys.setOnClickListener(v ->
                startActivity(new Intent(this, SurveyListActivity.class))); // This should list surveys

        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}