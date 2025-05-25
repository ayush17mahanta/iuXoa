package com.snorax;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class ProfilesFragment extends Fragment {

    private ListView listProfiles;
    private Button btnAddProfile;
    private ProfilesManager profilesManager;
    private List<Profile> profiles;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize ProfilesManager
        profilesManager = new ProfilesManager(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profiles, container, false);

        // Initialize Views
        listProfiles = view.findViewById(R.id.listProfiles);
        btnAddProfile = view.findViewById(R.id.btnAddProfile);

        // Load profiles
        profiles = profilesManager.getProfiles();

        // Set up the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, getProfileNames());
        listProfiles.setAdapter(adapter);

        // Set up Add Profile button
        btnAddProfile.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), EditProfileFragment.class);
            startActivity(intent);
        });

        // Handle list item click
        listProfiles.setOnItemClickListener((parent, view1, position, id) -> {
            Profile selectedProfile = profiles.get(position);
            Intent intent = new Intent(requireActivity(), EditProfileFragment.class);
            intent.putExtra("profile_index", position);
            intent.putExtra("profile_name", selectedProfile.getName());
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listProfiles != null) {
            // Reload and refresh profiles
            profiles = profilesManager.getProfiles();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_list_item_1, getProfileNames());
            listProfiles.setAdapter(adapter);
        }
    }

    private List<String> getProfileNames() {
        List<String> profileNames = new ArrayList<>();
        for (Profile profile : profiles) {
            profileNames.add(profile.getName());
        }
        return profileNames;
    }
}
