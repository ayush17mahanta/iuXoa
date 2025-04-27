package com.iuxoa.marki;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iuxoa.marki.model.AppDatabase;
import com.iuxoa.marki.model.Project;

import java.util.List;

public class ProjectManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProjectAdapter adapter;
    private FloatingActionButton fabAddProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_management);

        // Initialize the RecyclerView and FloatingActionButton
        recyclerView = findViewById(R.id.recyclerViewProjects);
        fabAddProject = findViewById(R.id.fabAddProject);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the database and get the list of projects
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "my-database-name").allowMainThreadQueries().build();

        List<Project> projects = db.projectDao().getAllProjects();

        // Set the adapter for the RecyclerView
        adapter = new ProjectAdapter(projects, this);
        recyclerView.setAdapter(adapter);

        // Set up the FloatingActionButton to add new projects
        fabAddProject.setOnClickListener(v -> {
            Intent intent = new Intent(ProjectManagementActivity.this, AddProjectActivity.class);
            startActivity(intent);
        });
    }
}
