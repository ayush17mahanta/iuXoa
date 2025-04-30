package com.iuxoa.marki;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment {

    private SwitchMaterial notificationsSwitch, darkModeSwitch;
    private FirebaseFirestore db;
    private String userId;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Edge-to-Edge setup for the fragment's root view
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        notificationsSwitch = view.findViewById(R.id.notificationsSwitch);
        darkModeSwitch = view.findViewById(R.id.darkModeSwitch);

        // Load user settings from Firestore
        loadSettings();

        // Set listeners for the switches
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSetting("notifications", isChecked);
        });

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSetting("darkMode", isChecked);
        });

        return view;
    }

    // Load settings from Firestore
    private void loadSettings() {
        DocumentReference userSettingsRef = db.collection("users").document(userId).collection("settings").document("appSettings");
        userSettingsRef.get().addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.DocumentSnapshot>() {
            @Override
            public void onComplete(Task<com.google.firebase.firestore.DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Boolean notificationsEnabled = task.getResult().getBoolean("notifications");
                    Boolean darkModeEnabled = task.getResult().getBoolean("darkMode");

                    notificationsSwitch.setChecked(notificationsEnabled != null && notificationsEnabled);
                    darkModeSwitch.setChecked(darkModeEnabled != null && darkModeEnabled);
                }
            }
        });
    }

    // Save setting to Firestore
    private void saveSetting(String settingKey, boolean value) {
        DocumentReference userSettingsRef = db.collection("users").document(userId).collection("settings").document("appSettings");
        userSettingsRef.update(settingKey, value).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Successfully saved setting
            } else {
                // Handle failure
            }
        });
    }
}
