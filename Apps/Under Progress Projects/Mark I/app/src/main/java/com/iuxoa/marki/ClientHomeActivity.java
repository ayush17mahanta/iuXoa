package com.iuxoa.marki;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iuxoa.marki.model.Project;
import com.iuxoa.marki.viewmodel.PostProjectActivity;

import java.util.ArrayList;
import java.util.List;

public class ClientHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProjectAdapter adapter;
    private TextView noProjectsText;
    private List<Project> projectList;
    private DatabaseReference projectsRef; // Firebase reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerViewClientProjects);
        noProjectsText = findViewById(R.id.noClientProjectsText);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        projectList = new ArrayList<>();
        adapter = new ProjectAdapter(projectList, this);
        recyclerView.setAdapter(adapter);

        // Initialize Firebase Database
        projectsRef = FirebaseDatabase.getInstance().getReference("projects");

        // Load projects from Firebase
        loadProjectsFromFirebase();

        // Floating Action Button to Post New Project
        FloatingActionButton fab = findViewById(R.id.fabPostProject);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(ClientHomeActivity.this, PostProjectActivity.class);
            startActivity(intent);
        });
    }

    private void loadProjectsFromFirebase() {
        projectsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("CLIENT_HOME", "Data changed, count: " + snapshot.getChildrenCount());
                projectList.clear();

                for (DataSnapshot projectSnapshot : snapshot.getChildren()) {
                    Project project = projectSnapshot.getValue(Project.class);
                    if (project != null) {
                        project.setId(projectSnapshot.getKey()); // Ensure ID is set
                        projectList.add(project);
                    }
                }

                Log.d("CLIENT_HOME", "Projects loaded: " + projectList.size());
                adapter.updateProjects(projectList);

                // Update UI visibility
                recyclerView.setVisibility(projectList.isEmpty() ? View.GONE : View.VISIBLE);
                noProjectsText.setVisibility(projectList.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CLIENT_HOME", "Database error", error.toException());
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
