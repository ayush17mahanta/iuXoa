package com.iuxoa.marki;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.iuxoa.marki.model.Project;

import java.util.ArrayList;
import java.util.List;

public class FreelancerHomeActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private ProjectAdapter adapter;
    private TextView noProjectsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freelancer_home);

        recyclerView = findViewById(R.id.recyclerViewProjects);
        noProjectsText = findViewById(R.id.noProjectsText);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Load projects from Firestore
        loadProjectsFromFirestore();
    }

    private void loadProjectsFromFirestore() {
        db.collection("Projects")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Project> projectList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Project project = document.toObject(Project.class);
                            projectList.add(project);
                        }

                        // Update UI
                        if (projectList.isEmpty()) {
                            noProjectsText.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            noProjectsText.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            adapter = new ProjectAdapter(projectList, FreelancerHomeActivity.this);
                            recyclerView.setAdapter(adapter);
                        }
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }
    // Example sign-out method in your home activities
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, ChooseAccountTypeActivity.class));
        finish();
    }
}