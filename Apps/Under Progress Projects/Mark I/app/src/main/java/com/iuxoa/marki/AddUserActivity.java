package com.iuxoa.marki;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddUserActivity extends AppCompatActivity {

    private EditText userNameInput, userEmailInput;
    private Button addUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        userNameInput = findViewById(R.id.userNameInput);
        userEmailInput = findViewById(R.id.userEmailInput);
        addUserButton = findViewById(R.id.addUserButton);

        addUserButton.setOnClickListener(v -> {
            String name = userNameInput.getText().toString().trim();
            String email = userEmailInput.getText().toString().trim();

            // Validate input
            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a new user
            User newUser = new User(name, email);

            // Add the new user to the user list
            UserManagementActivity.getUserList().add(newUser);

            // Notify the adapter to update the RecyclerView
            UserManagementActivity.notifyAdapter();

            // Finish the activity and return to the User Management screen
            finish();
        });
    }
}
