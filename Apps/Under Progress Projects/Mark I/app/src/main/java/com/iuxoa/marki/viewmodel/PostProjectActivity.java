package com.iuxoa.marki.viewmodel;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iuxoa.marki.R;
import com.iuxoa.marki.model.Project;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PostProjectActivity extends AppCompatActivity {

    private EditText editTextProjectTitle, editTextProjectDescription,
            editTextProjectBudget, editTextProjectDeadline, editTextProjectSkills;
    private Button buttonSubmitProject;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_project);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        currentUserId = currentUser != null ? currentUser.getUid() : null;

        // Initialize views
        editTextProjectTitle = findViewById(R.id.projectTitle);
        editTextProjectDescription = findViewById(R.id.projectDescription);
        editTextProjectBudget = findViewById(R.id.projectBudget);
        editTextProjectDeadline = findViewById(R.id.projectDeadline);
        editTextProjectSkills = findViewById(R.id.projectSkills);
        buttonSubmitProject = findViewById(R.id.submitProjectButton);

        buttonSubmitProject.setOnClickListener(v -> {
            Log.d("POST_PROJECT", "Submit button clicked");
            verifyAndPostProject();
        });
    }

    private void verifyAndPostProject() {
        // First verify account type
        db.collection("users").document(currentUserId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && "client".equals(documentSnapshot.getString("accountType"))) {
                        postProject();
                    } else {
                        Toast.makeText(this,
                                "Only client accounts can post projects",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error verifying account type", Toast.LENGTH_SHORT).show();
                    Log.e("POST_PROJECT", "Error verifying account", e);
                });
    }

    private void postProject() {
        // Get field values
        String title = editTextProjectTitle.getText().toString().trim();
        String description = editTextProjectDescription.getText().toString().trim();
        String budgetStr = editTextProjectBudget.getText().toString().trim();
        String deadline = editTextProjectDeadline.getText().toString().trim();
        String skills = editTextProjectSkills.getText().toString().trim();

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

        // Create project data
        Map<String, Object> project = new HashMap<>();
        project.put("title", title);
        project.put("description", description);
        project.put("budget", budget);
        project.put("deadline", deadline);
        project.put("skills", skills.split("\\s*,\\s*")); // Convert comma-separated string to array
        project.put("clientId", currentUserId);
        project.put("status", "open");
        project.put("createdAt", FieldValue.serverTimestamp());

        // Get next project ID
        db.collection("metadata").document("projectCounter")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    long count = documentSnapshot.exists() ?
                            documentSnapshot.getLong("count") : 0L;
                    String projectId = "PROJ" + String.format("%03d", count + 1);

                    // Create the project document
                    db.collection("projects").document(projectId)
                            .set(project)
                            .addOnSuccessListener(aVoid -> {
                                // Update counter
                                db.collection("metadata").document("projectCounter")
                                        .set(Collections.singletonMap("count", count + 1))
                                        .addOnCompleteListener(updateTask -> {
                                            Toast.makeText(this,
                                                    "Project posted successfully!",
                                                    Toast.LENGTH_SHORT).show();
                                            finish();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this,
                                        "Failed to post project: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                Log.e("POST_PROJECT", "Error posting project", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Failed to get project counter: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Log.e("POST_PROJECT", "Error getting counter", e);
                });
    }
}