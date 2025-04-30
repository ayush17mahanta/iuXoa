package com.iuxoa.marki;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private String accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Get the user's account type from SharedPreferences
        accountType = getUserAccountType();

        // Update menu items based on account type
        updateMenuItems(accountType);

        // Set listener for bottom navigation item selection
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);

        // Load appropriate default home fragment based on account type
        if ("client".equals(accountType)) {
            loadFragment(new ClientHomeFragment());
        } else {
            loadFragment(new FreelancerHomeFragment());
        }
    }

    private String getUserAccountType() {
        return getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .getString("accountType", "client"); // Default to "client"
    }

    public void updateMenuItems(String accountType) {
        bottomNavigationView.getMenu().clear();
        if ("client".equals(accountType)) {
            bottomNavigationView.inflateMenu(R.menu.bottom_navigation_client);
        } else {
            bottomNavigationView.inflateMenu(R.menu.bottom_navigation_freelancer);
        }

        // Ensure the home menu item is highlighted
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();

        // Handle bottom navigation item selection
        if (itemId == R.id.nav_home) {
            if ("client".equals(accountType)) {
                selectedFragment = new ClientHomeFragment();
            } else {
                selectedFragment = new FreelancerHomeFragment();
            }

        } else if (itemId == R.id.nav_projects) {
            selectedFragment = new ProjectsFragment();

        } else if (itemId == R.id.top_clients) {
            if ("client".equals(accountType)) {
                selectedFragment = new TopClientsFragment();
            } else {
                selectedFragment = new TopFreelancerFragment();
            }

        } else if (itemId == R.id.nav_settings) {
            selectedFragment = new SettingsFragment();

        } else if (itemId == R.id.choose_account_type) {
            selectedFragment = new ChooseAccountTypeFragment();
        }

        return loadFragment(selectedFragment);
    }



    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    // You can call this method from the ChooseAccountTypeFragment to update the account type and navigate to the home screen
    public void onAccountTypeSelected(String selectedAccountType) {
        // Save the selected account type in SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("accountType", selectedAccountType);
        editor.apply();

        // Update the account type and menu items
        accountType = selectedAccountType;
        updateMenuItems(accountType);

        // Navigate to the correct home screen based on the account type
        if ("client".equals(accountType)) {
            loadFragment(new ClientHomeFragment());
        } else {
            loadFragment(new FreelancerHomeFragment());
        }
    }
}
