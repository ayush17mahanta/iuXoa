package com.iuxoa.marki.viewmodel;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iuxoa.marki.R;
import com.iuxoa.marki.model.Project;

public class PostProjectActivity extends AppCompatActivity {

    private EditText editTextProjectTitle, editTextProjectDescription,
            editTextProjectBudget, editTextProjectDeadline, editTextProjectSkills;
    private Button buttonSubmitProject;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_project);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize views
        editTextProjectTitle = findViewById(R.id.projectTitle);
        editTextProjectDescription = findViewById(R.id.projectDescription);
        editTextProjectBudget = findViewById(R.id.projectBudget);
        editTextProjectDeadline = findViewById(R.id.projectDeadline);
        editTextProjectSkills = findViewById(R.id.projectSkills);
        buttonSubmitProject = findViewById(R.id.submitProjectButton);

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

        // Validate inputs first
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

        String userId = auth.getCurrentUser().getUid();

        // Step 1: Get current counter value
        firestore.collection("metadata").document("projectCounter")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long count = documentSnapshot.getLong("count");
                        if (count == null) count = 0L;

                        long newCount = count + 1;
                        String customProjectId = "PROJ" + String.format("%03d", newCount); // PROJ001, PROJ002, etc.

                        // Step 2: Create project with custom ID
                        Project project = new Project(title, description, budget, deadline, skills, userId);
                        project.setId(customProjectId);

                        // Step 3: Save project under custom ID
                        firestore.collection("projects").document(customProjectId)
                                .set(project)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Project posted successfully!", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to post project: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("POST_PROJECT", "Error posting project", e);
                                });

                        // Step 4: Update counter
                        firestore.collection("metadata").document("projectCounter")
                                .update("count", newCount);

                    } else {
                        Toast.makeText(this, "Counter document does not exist!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch counter: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("POST_PROJECT", "Error fetching counter", e);
                });
    }

}
