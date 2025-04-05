package com.snorax;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etProfileName;
    private Button btnSaveProfile;
    private ProfilesManager profilesManager;
    private int profileIndex = -1; // -1 means new profile

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile); // Use the correct layout file

        // Initialize Views
        etProfileName = findViewById(R.id.etProfileName); // Correct ID
        btnSaveProfile = findViewById(R.id.btnSaveProfile); // Correct ID

        // Initialize ProfilesManager
        profilesManager = new ProfilesManager(this);

        // Check if editing an existing profile
        if (getIntent().hasExtra("profile_index")) {
            profileIndex = getIntent().getIntExtra("profile_index", -1);
            String profileName = getIntent().getStringExtra("profile_name");
            etProfileName.setText(profileName);
        }

        // Set up the Save Profile button
        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String profileName = etProfileName.getText().toString().trim();
        if (profileName.isEmpty()) {
            Toast.makeText(this, "Profile name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Profile profile = new Profile(profileName);
        // Add default app settings (example)
        profile.addAppSetting("WhatsApp", NotificationStates.MUTE);
        profile.addAppSetting("Messenger", NotificationStates.VIBRATE);

        if (profileIndex == -1) {
            // Add new profile
            profilesManager.addProfile(profile);
        } else {
            // Update existing profile
            profilesManager.updateProfile(profileIndex, profile);
        }

        Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}