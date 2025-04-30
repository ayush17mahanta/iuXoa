package com.iuxoa.marki;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iuxoa.marki.model.Project;

public class EditProjectActivity extends AppCompatActivity {

    private EditText editTextProjectTitle, editTextProjectDescription,
            editTextBudget, editTextDeadline, editTextSkills;
    private Button buttonSaveChanges;
    private String projectId;
    private FirebaseFirestore firestore;
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

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
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
        DocumentReference docRef = firestore.collection("Projects").document(projectId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                currentProject = documentSnapshot.toObject(Project.class);
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
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load project", Toast.LENGTH_SHORT).show();
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

        double budget = Double.parseDouble(budgetStr);

        // Update current project object
        currentProject.setTitle(title);
        currentProject.setDescription(description);
        currentProject.setBudget(budget);
        currentProject.setDeadline(deadline);
        currentProject.setSkills(skills);

        // Save to Firestore
        firestore.collection("Projects").document(projectId)
                .set(currentProject)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Project updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update project", Toast.LENGTH_SHORT).show();
                });
    }
}
