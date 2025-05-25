package com.snorax;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EditProfileFragment extends Fragment {

    private EditText etProfileName;
    private Button btnSaveProfile;
    private ProfilesManager profilesManager;
    private int profileIndex = -1; // -1 means new profile

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Initialize Views
        etProfileName = view.findViewById(R.id.etProfileName);
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);

        // Initialize ProfilesManager
        profilesManager = new ProfilesManager(requireContext());

        // Check if editing an existing profile
        if (getArguments() != null) {
            profileIndex = getArguments().getInt("profile_index", -1);
            String profileName = getArguments().getString("profile_name");
            etProfileName.setText(profileName);
        }

        // Set up the Save Profile button
        btnSaveProfile.setOnClickListener(v -> saveProfile());

        return view;
    }

    private void saveProfile() {
        String profileName = etProfileName.getText().toString().trim();
        if (profileName.isEmpty()) {
            Toast.makeText(requireContext(), "Profile name cannot be empty", Toast.LENGTH_SHORT).show();
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

        Toast.makeText(requireContext(), "Profile saved", Toast.LENGTH_SHORT).show();

        // Navigate back
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    // Factory method for creating new instance with arguments
    public static EditProfileFragment newInstance(int profileIndex, String profileName) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putInt("profile_index", profileIndex);
        args.putString("profile_name", profileName);
        fragment.setArguments(args);
        return fragment;
    }
}