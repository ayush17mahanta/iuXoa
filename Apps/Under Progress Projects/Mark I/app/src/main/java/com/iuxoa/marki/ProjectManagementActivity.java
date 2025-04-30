package com.iuxoa.marki;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.iuxoa.marki.model.Project;
import com.iuxoa.marki.ProjectAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProjectManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProjectAdapter adapter;
    private FloatingActionButton fabAddProject;
    private FirebaseFirestore db;
    private List<Project> projectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_management);

        recyclerView = findViewById(R.id.recyclerViewProjects);
        fabAddProject = findViewById(R.id.fabAddProject);
        db = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProjectAdapter(projectList, this);
        recyclerView.setAdapter(adapter);

        loadProjects();

        fabAddProject.setOnClickListener(v -> {
            Intent intent = new Intent(ProjectManagementActivity.this, AddProjectActivity.class);
            startActivity(intent);
        });
    }

    private void loadProjects() {
        db.collection("projects")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        projectList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Project project = document.toObject(Project.class);
                            project.setId(document.getId()); // Set the document ID
                            projectList.add(project);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error loading projects", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProjects(); // Refresh data when activity resumes
    }
}