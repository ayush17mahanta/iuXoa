package com.iuxoa.marki;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iuxoa.marki.model.Project;

public class AddProjectActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription, editTextBudget, editTextDeadline, editTextSkills;
    private Button buttonSubmit;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        // Initialize views
        editTextTitle = findViewById(R.id.projectTitle);
        editTextDescription = findViewById(R.id.projectDescription);
        editTextBudget = findViewById(R.id.projectBudget);
        editTextDeadline = findViewById(R.id.projectDeadline);
        editTextSkills = findViewById(R.id.projectSkills);
        buttonSubmit = findViewById(R.id.submitProjectButton);

        databaseReference = FirebaseDatabase.getInstance().getReference("Projects");

        buttonSubmit.setOnClickListener(v -> addProject());
    }

    private void addProject() {
        String title = editTextTitle.getText().toString().trim();
        String desc = editTextDescription.getText().toString().trim();
        String budgetStr = editTextBudget.getText().toString().trim();
        String deadline = editTextDeadline.getText().toString().trim();
        String skills = editTextSkills.getText().toString().trim();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Validation
        if (title.isEmpty() || desc.isEmpty() || budgetStr.isEmpty() ||
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

        String projectId = databaseReference.push().getKey();
        Project newProject = new Project(title, desc, budget, deadline, skills, userId);
        newProject.setId(projectId);

        databaseReference.child(projectId).setValue(newProject)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Project added successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add project: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}