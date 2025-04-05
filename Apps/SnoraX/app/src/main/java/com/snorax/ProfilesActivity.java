package com.snorax;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ProfilesActivity extends AppCompatActivity {

    private ListView listProfiles;
    private Button btnAddProfile;
    private ProfilesManager profilesManager;
    private List<Profile> profiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);

        // Initialize Views
        listProfiles = findViewById(R.id.listProfiles);
        btnAddProfile = findViewById(R.id.btnAddProfile);

        // Initialize ProfilesManager
        profilesManager = new ProfilesManager(this);

        // Load profiles
        profiles = profilesManager.getProfiles();

        // Set up the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getProfileNames());
        listProfiles.setAdapter(adapter);

        // Set up the Add Profile button
        btnAddProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilesActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Set up item click listener for the ListView
        listProfiles.setOnItemClickListener((parent, view, position, id) -> {
            Profile selectedProfile = profiles.get(position);
            Intent intent = new Intent(ProfilesActivity.this, EditProfileActivity.class);
            intent.putExtra("profile_index", position);
            intent.putExtra("profile_name", selectedProfile.getName());
            startActivity(intent);
        });
    }

    private List<String> getProfileNames() {
        List<String> profileNames = new ArrayList<>();
        for (Profile profile : profiles) {
            profileNames.add(profile.getName());
        }
        return profileNames;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the profiles list
        profiles = profilesManager.getProfiles();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getProfileNames());
        listProfiles.setAdapter(adapter);
    }
}