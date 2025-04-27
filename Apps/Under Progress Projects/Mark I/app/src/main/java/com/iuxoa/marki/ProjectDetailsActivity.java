package com.iuxoa.marki;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ProjectDetailsActivity extends AppCompatActivity {

    private TextView titleText, descText, budgetText, deadlineText, skillsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_project_details);

        titleText = findViewById(R.id.titleText);
        descText = findViewById(R.id.descText);
        budgetText = findViewById(R.id.budgetText);
        deadlineText = findViewById(R.id.deadlineText);
        skillsText = findViewById(R.id.skillsText);

        // Get data from Intent
        String title = getIntent().getStringExtra("title");
        String desc = getIntent().getStringExtra("desc");
        String budget = getIntent().getStringExtra("budget");
        String deadline = getIntent().getStringExtra("deadline");
        String skills = getIntent().getStringExtra("skills");

        // Set data to TextViews
        titleText.setText(title);
        descText.setText(desc);
        budgetText.setText("Budget: " + budget);
        deadlineText.setText("Deadline: " + deadline);
        skillsText.setText("Skills: " + skills);
    }
}
