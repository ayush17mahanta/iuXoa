package com.iuxoa.marki;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.iuxoa.marki.model.AppDatabase;
import com.iuxoa.marki.model.Project;

public class EditProjectActivity extends AppCompatActivity {

    private EditText editTextProjectTitle, editTextProjectDescription;
    private Button buttonSaveChanges;
    private int projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_project);

        // Initialize UI elements
        editTextProjectTitle = findViewById(R.id.editTextProjectTitle);
        editTextProjectDescription = findViewById(R.id.editTextProjectDescription);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);

        // Retrieve project ID from Intent
        Intent intent = getIntent();
        projectId = intent.getIntExtra("PROJECT_ID", -1);

        // If the project ID is valid, load the project details
        if (projectId != -1) {
            loadProjectDetails(projectId);
        } else {
            Toast.makeText(this, "Invalid project", Toast.LENGTH_SHORT).show();
        }

        // Set button click listener to save changes
        buttonSaveChanges.setOnClickListener(v -> {
            // Get input values
            String title = editTextProjectTitle.getText().toString().trim();
            String description = editTextProjectDescription.getText().toString().trim();

            // Validate inputs
            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(EditProjectActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Save the edited project
                AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "my-database-name").allowMainThreadQueries().build();

                Project editedProject = db.projectDao().getProjectById(projectId);
                editedProject.setTitle(title);
                editedProject.setDescription(description);

                // Update project in the database
                db.projectDao().updateProject(editedProject);

                // Show success message
                Toast.makeText(EditProjectActivity.this, "Project updated successfully!", Toast.LENGTH_SHORT).show();

                // Go back to ProjectManagementActivity
                Intent intentBack = new Intent(EditProjectActivity.this, ProjectManagementActivity.class);
                startActivity(intentBack);
                finish();
            }
        });
    }

    private void loadProjectDetails(int projectId) {
        // Load project details from the database
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "my-database-name").allowMainThreadQueries().build();

        Project project = db.projectDao().getProjectById(projectId);

        // Set the project details in the EditText fields
        if (project != null) {
            editTextProjectTitle.setText(project.getTitle());
            editTextProjectDescription.setText(project.getDescription());
        } else {
            Toast.makeText(this, "Project not found", Toast.LENGTH_SHORT).show();
        }
    }
}
