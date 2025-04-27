package com.iuxoa.marki;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity {

    public static List<User> userList = new ArrayList<>();
    public static UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        // Initialize RecyclerView and Adapter
        RecyclerView recyclerView = findViewById(R.id.userRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userAdapter = new UserAdapter(userList);
        recyclerView.setAdapter(userAdapter);
    }

    // Add this method to get the user list
    public static List<User> getUserList() {
        return userList;
    }

    // Add this method to notify adapter data changes
    public static void notifyAdapter() {
        if (userAdapter != null) {
            userAdapter.notifyDataSetChanged();
        }
    }
}
