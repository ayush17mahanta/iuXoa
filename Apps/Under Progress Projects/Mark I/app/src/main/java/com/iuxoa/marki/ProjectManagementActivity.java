package com.iuxoa.marki;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iuxoa.marki.viewmodel.ProjectViewModel;
import com.iuxoa.marki.model.Project;

import java.util.List;

public class ProjectManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProjectAdapter adapter;
    private FloatingActionButton fabAddProject;
    private ProjectViewModel projectViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_management);

        recyclerView = findViewById(R.id.recyclerViewProjects);
        fabAddProject = findViewById(R.id.fabAddProject);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize ViewModel
        projectViewModel = new ProjectViewModel(getApplication());

        // Observe LiveData from ViewModel
        projectViewModel.getAllProjects().observe(this, new Observer<List<Project>>() {
            @Override
            public void onChanged(List<Project> projects) {
                if (adapter == null) {
                    adapter = new ProjectAdapter(projects, ProjectManagementActivity.this);
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.updateProjects(projects); // Update the data in the adapter
                }
            }
        });

        fabAddProject.setOnClickListener(v -> {
            Intent intent = new Intent(ProjectManagementActivity.this, AddProjectActivity.class);
            startActivity(intent);
        });
    }
}
