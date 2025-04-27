package com.iuxoa.marki;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.iuxoa.marki.database.UserDatabase;
import com.iuxoa.marki.model.User;

public class EditUserActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText;
    private Button saveButton;
    private UserDatabase db;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        // Initialize UI components
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        saveButton = findViewById(R.id.saveButton);

        // Get the user data passed through the intent
        Intent intent = getIntent();
        int userId = intent.getIntExtra("USER_ID", -1);
        if (userId != -1) {
            // Fetch the user from the database (assuming you have a database setup)
            db = UserDatabase.getInstance(this);
            currentUser = db.userDao().getUserById(userId);

            // Set current user data to EditText fields
            if (currentUser != null) {
                nameEditText.setText(currentUser.getName());
                emailEditText.setText(currentUser.getEmail());
            }
        }

        // Save button click handler
        saveButton.setOnClickListener(v -> {
            String updatedName = nameEditText.getText().toString();
            String updatedEmail = emailEditText.getText().toString();

            // Update the user in the database
            if (currentUser != null) {
                currentUser.setName(updatedName);
                currentUser.setEmail(updatedEmail);
                db.userDao().update(currentUser);
            }

            // Finish the activity and return to the previous screen
            finish();
        });
    }
}
