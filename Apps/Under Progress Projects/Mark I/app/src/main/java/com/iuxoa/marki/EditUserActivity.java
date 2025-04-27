package com.iuxoa.marki;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iuxoa.marki.model.User;

public class EditUserActivity extends AppCompatActivity {

    private EditText editTextUserName, editTextUserEmail;
    private Button buttonSaveChanges;
    private String userId;
    private DatabaseReference userDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        // Initialize UI elements
        editTextUserName = findViewById(R.id.editTextUserName);
        editTextUserEmail = findViewById(R.id.editTextUserEmail);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);

        // Firebase database reference
        userDatabaseRef = FirebaseDatabase.getInstance().getReference("users");

        // Retrieve user ID from Intent
        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");

        // If the user ID is valid, load the user details from Firebase
        if (userId != null) {
            loadUserDetails(userId);
        } else {
            Toast.makeText(this, "Invalid user", Toast.LENGTH_SHORT).show();
        }

        // Set button click listener to save changes
        buttonSaveChanges.setOnClickListener(v -> {
            String userName = editTextUserName.getText().toString().trim();
            String userEmail = editTextUserEmail.getText().toString().trim();

            // Validate inputs
            if (userName.isEmpty() || userEmail.isEmpty()) {
                Toast.makeText(EditUserActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Create an updated User object
                User updatedUser = new User(userId, userName, userEmail);

                // Update user in the Firebase database
                userDatabaseRef.child(userId).setValue(updatedUser)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(EditUserActivity.this, "User updated successfully!", Toast.LENGTH_SHORT).show();
                            // Go back to previous activity
                            Intent intentBack = new Intent(EditUserActivity.this, UserManagementActivity.class);
                            startActivity(intentBack);
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(EditUserActivity.this, "Error updating user", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void loadUserDetails(String userId) {
        userDatabaseRef.child(userId).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    editTextUserName.setText(user.getUserName());
                    editTextUserEmail.setText(user.getUserEmail());
                }
            } else {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
