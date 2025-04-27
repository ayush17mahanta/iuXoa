package com.iuxoa.marki;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iuxoa.marki.model.Project;

public class EditProjectActivity extends AppCompatActivity {

    private EditText editTextProjectTitle, editTextProjectDescription,
            editTextBudget, editTextDeadline, editTextSkills;
    private Button buttonSaveChanges;
    private String projectId;
    private DatabaseReference projectDatabaseRef;
    private Project currentProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_project);

        // Initialize views
        editTextProjectTitle = findViewById(R.id.editTextProjectTitle);
        editTextProjectDescription = findViewById(R.id.editTextProjectDescription);
        editTextBudget = findViewById(R.id.editTextBudget);
        editTextDeadline = findViewById(R.id.editTextDeadline);
        editTextSkills = findViewById(R.id.editTextSkills);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);

        projectDatabaseRef = FirebaseDatabase.getInstance().getReference("Projects");
        projectId = getIntent().getStringExtra("PROJECT_ID");

        if (projectId != null) {
            loadProjectDetails(projectId);
        } else {
            Toast.makeText(this, "Invalid project", Toast.LENGTH_SHORT).show();
            finish();
        }

        buttonSaveChanges.setOnClickListener(v -> saveChanges());
    }

    private void loadProjectDetails(String projectId) {
        projectDatabaseRef.child(projectId).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                currentProject = dataSnapshot.getValue(Project.class);
                if (currentProject != null) {
                    editTextProjectTitle.setText(currentProject.getTitle());
                    editTextProjectDescription.setText(currentProject.getDescription());
                    editTextBudget.setText(String.valueOf(currentProject.getBudget()));
                    editTextDeadline.setText(currentProject.getDeadline());
                    editTextSkills.setText(currentProject.getSkills());
                }
            } else {
                Toast.makeText(this, "Project not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveChanges() {
        String title = editTextProjectTitle.getText().toString().trim();
        String description = editTextProjectDescription.getText().toString().trim();
        String budgetStr = editTextBudget.getText().toString().trim();
        String deadline = editTextDeadline.getText().toString().trim();
        String skills = editTextSkills.getText().toString().trim();

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

        // Update the existing project
        currentProject.setTitle(title);
        currentProject.setDescription(description);
        currentProject.setBudget(budget);
        currentProject.setDeadline(deadline);
        currentProject.setSkills(skills);

        projectDatabaseRef.child(projectId).setValue(currentProject)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Project updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error updating project: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}