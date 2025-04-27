package com.iuxoa.marki;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.iuxoa.marki.model.AppDatabase;
import com.iuxoa.marki.model.Project;

import java.util.List;

public class FreelancerHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProjectAdapter adapter;
    private TextView noProjectsText;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_freelancer_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize database
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "my-database-name").allowMainThreadQueries().build();

        recyclerView = findViewById(R.id.recyclerViewProjects);
        noProjectsText = findViewById(R.id.noProjectsText);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Project> projects = db.projectDao().getAllProjects();

        if (projects.isEmpty()) {
            noProjectsText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noProjectsText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new ProjectAdapter(projects, this); // Passing Context ('this')
            recyclerView.setAdapter(adapter);
        }
    }
}
