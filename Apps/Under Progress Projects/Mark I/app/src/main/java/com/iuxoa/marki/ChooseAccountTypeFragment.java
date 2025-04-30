package com.iuxoa.marki;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChooseAccountTypeFragment extends Fragment {

    public ChooseAccountTypeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_account_type, container, false);

        // Edge-to-Edge layout handling
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set click listeners for client and freelancer buttons
        view.findViewById(R.id.clientButton).setOnClickListener(v -> selectRole("client"));
        view.findViewById(R.id.freelancerButton).setOnClickListener(v -> selectRole("freelancer"));

        return view;
    }

    private void selectRole(String role) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", requireContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Update the account type in SharedPreferences
        editor.putString("accountType", role);
        editor.apply();

        // After selecting the account type, load the home screen for the selected role
        Fragment selectedFragment = "client".equals(role) ? new ClientHomeFragment() : new FreelancerHomeFragment();

        // Update the bottom navigation to reflect the correct selection
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            // Update the bottom navigation menu for the selected role
            activity.updateMenuItems(role);
        }

        // Load the selected fragment (home screen)
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commit();

    }
}
