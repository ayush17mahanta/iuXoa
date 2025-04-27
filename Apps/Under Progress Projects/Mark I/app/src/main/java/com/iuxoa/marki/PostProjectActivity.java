package com.iuxoa.marki;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.iuxoa.marki.model.AppDatabase;

public class PostProjectActivity extends AppCompatActivity {

    private EditText projectTitle, projectDescription, projectBudget, projectDeadline, projectSkills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_project);

        // Initialize database
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "my-database-name").allowMainThreadQueries().build();

        // Initialize the EditText fields
        projectTitle = findViewById(R.id.projectTitle);
        projectDescription = findViewById(R.id.projectDescription);
        projectBudget = findViewById(R.id.projectBudget);
        projectDeadline = findViewById(R.id.projectDeadline);
        projectSkills = findViewById(R.id.projectSkills);

        // Set the click listener for the submit button
        findViewById(R.id.submitProjectButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = projectTitle.getText().toString();
                String desc = projectDescription.getText().toString();
                String budget = projectBudget.getText().toString();
                String deadline = projectDeadline.getText().toString();
                String skills = projectSkills.getText().toString();

                // Validate input fields
                if (title.isEmpty() || desc.isEmpty() || budget.isEmpty() || deadline.isEmpty() || skills.isEmpty()) {
                    Toast.makeText(PostProjectActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Create a new project object
                    com.iuxoa.marki.model.Project project = new com.iuxoa.marki.model.Project(title, desc, budget, deadline, skills);

                    // Insert the project into the database
                    db.projectDao().insert(project);

                    // Show a success message
                    Toast.makeText(PostProjectActivity.this, "Project Posted Successfully", Toast.LENGTH_SHORT).show();

                    // Close the activity and return to the previous one
                    finish();
                }
            }
        });
    }
}
