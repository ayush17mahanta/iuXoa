package com.iuxoa.marki.viewmodel;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iuxoa.marki.R;
import com.iuxoa.marki.model.Project;

public class PostProjectActivity extends AppCompatActivity {

    private EditText editTextProjectTitle, editTextProjectDescription,
            editTextProjectBudget, editTextProjectDeadline, editTextProjectSkills;
    private Button buttonSubmitProject;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_project);

        // Initialize views
        editTextProjectTitle = findViewById(R.id.projectTitle);
        editTextProjectDescription = findViewById(R.id.projectDescription);
        editTextProjectBudget = findViewById(R.id.projectBudget);
        editTextProjectDeadline = findViewById(R.id.projectDeadline);
        editTextProjectSkills = findViewById(R.id.projectSkills);
        buttonSubmitProject = findViewById(R.id.submitProjectButton);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("projects");

        buttonSubmitProject.setOnClickListener(v -> {
            Log.d("POST_PROJECT", "Submit button clicked");
            postProject();
        });
    }

    private void postProject() {
        // Get field values
        String title = editTextProjectTitle.getText().toString().trim();
        String description = editTextProjectDescription.getText().toString().trim();
        String budgetStr = editTextProjectBudget.getText().toString().trim();
        String deadline = editTextProjectDeadline.getText().toString().trim();
        String skills = editTextProjectSkills.getText().toString().trim();
        String userId = auth.getCurrentUser().getUid();

        // Validate inputs
        if (title.isEmpty() || description.isEmpty() || budgetStr.isEmpty() ||
                deadline.isEmpty() || skills.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double budget;
        try {
            budget = Double.parseDouble(budgetStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid budget amount", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create project
        Project project = new Project(title, description, budget, deadline, skills, userId);
        String projectId = databaseReference.push().getKey();
        project.setId(projectId);

        // Save to database
        databaseReference.child(projectId).setValue(project)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Project posted successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to post project: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("POST_PROJECT", "Error posting project", e);
                });
    }
}