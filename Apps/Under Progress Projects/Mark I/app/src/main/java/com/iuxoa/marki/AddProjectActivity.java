package com.iuxoa.marki;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.iuxoa.marki.model.Project;
import com.iuxoa.marki.model.ProjectDatabase;

public class AddProjectActivity extends AppCompatActivity {

    private ProjectDatabase db;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        // Initialize the Save button
        saveButton = findViewById(R.id.saveButton);

        // Assuming you have a method to get the database instance
        db = ProjectDatabase.getInstance(this);

        saveButton.setOnClickListener(v -> {
            // Hardcoded project details
            String title = "Project Title";
            String desc = "Project Description";
            String budget = "10000";  // Example value
            String deadline = "2025-12-31";  // Example value
            String skills = "Java, Android, SQL";  // Example skills

            // Create a new Project object with hardcoded values
            Project newProject = new Project(title, desc, budget, deadline, skills);

            // Insert the new project into the database
            db.projectDao().insert(newProject);

            // Optionally, finish the activity or show a message
            finish();
        });
    }
}
