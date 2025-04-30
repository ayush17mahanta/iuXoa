package com.iuxoa.marki;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iuxoa.marki.model.Project;

import java.util.HashMap;
import java.util.Map;

public class AddProjectActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription, editTextBudget, editTextDeadline, editTextSkills;
    private Button buttonSubmit;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

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

        // Initialize Firestore and Auth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        buttonSubmit.setOnClickListener(v -> addProject());
    }

    private void addProject() {
        String title = editTextTitle.getText().toString().trim();
        String desc = editTextDescription.getText().toString().trim();
        String budgetStr = editTextBudget.getText().toString().trim();
        String deadline = editTextDeadline.getText().toString().trim();
        String skills = editTextSkills.getText().toString().trim();

        // Check if user is logged in
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        // Validation
        if (title.isEmpty()) {
            editTextTitle.setError("Title is required");
            return;
        }
        if (desc.isEmpty()) {
            editTextDescription.setError("Description is required");
            return;
        }
        if (budgetStr.isEmpty()) {
            editTextBudget.setError("Budget is required");
            return;
        }
        if (deadline.isEmpty()) {
            editTextDeadline.setError("Deadline is required");
            return;
        }

        double budget;
        try {
            budget = Double.parseDouble(budgetStr);
        } catch (NumberFormatException e) {
            editTextBudget.setError("Invalid budget amount");
            return;
        }

        // Create project object
        Project newProject = new Project(title, desc, budget, deadline, skills, userId);

        // Add to Firestore
        db.collection("projects")
                .add(newProject)
                .addOnSuccessListener(documentReference -> {
                    // Set the auto-generated ID
                    newProject.setId(documentReference.getId());

                    // Optionally update the document to include the ID as a field
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("id", documentReference.getId());

                    db.collection("projects").document(documentReference.getId())
                            .update(updates)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(AddProjectActivity.this,
                                        "Project added successfully!",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddProjectActivity.this,
                            "Failed to add project: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}