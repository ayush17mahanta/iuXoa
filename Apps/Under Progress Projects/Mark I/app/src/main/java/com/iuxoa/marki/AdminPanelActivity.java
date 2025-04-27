package com.iuxoa.marki;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminPanelActivity extends AppCompatActivity {

    private Button manageUsersButton, manageProjectsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_panel);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        manageUsersButton = findViewById(R.id.manageUsersButton);
        manageProjectsButton = findViewById(R.id.manageProjectsButton);

        manageUsersButton.setOnClickListener(v -> {
            // Handle user management
            Intent intent = new Intent(AdminPanelActivity.this, UserManagementActivity.class);
            startActivity(intent);
        });

        manageProjectsButton.setOnClickListener(v -> {
            // Handle project management
            Intent intent = new Intent(AdminPanelActivity.this, ProjectManagementActivity.class);
            startActivity(intent);
        });
    }
}