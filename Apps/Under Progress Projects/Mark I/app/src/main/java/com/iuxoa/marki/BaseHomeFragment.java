package com.iuxoa.marki;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class BaseHomeFragment extends Fragment {
    protected TextView profileName, textUserName, textAccountType;
    protected ImageView profileImageView;
    protected String currentUserId;
    protected FirebaseAuth mAuth;
    protected FirebaseFirestore db;

    protected void initializeProfileViews(View view) {
        profileName = view.findViewById(R.id.profileName);
        textUserName = view.findViewById(R.id.textUserName);
        textAccountType = view.findViewById(R.id.textAccountType);
        profileImageView = view.findViewById(R.id.profile_image);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUserId = mAuth.getCurrentUser() != null ?
                mAuth.getCurrentUser().getUid() : null;
    }

    protected void loadUserProfile() {
        if (currentUserId == null) {
            Log.e("Profile", "No authenticated user");
            return;
        }

        db.collection("users").document(currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        updateProfileUI(document);
                        saveUserPreferences(document);
                    } else {
                        loadFallbackUserData();
                        Log.e("Profile", "Error loading user data", task.getException());
                    }
                });
    }

    protected abstract void updateProfileUI(DocumentSnapshot userDocument);

    protected void saveUserPreferences(DocumentSnapshot userDocument) {
        SharedPreferences prefs = requireActivity().getSharedPreferences(
                "UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("name", userDocument.getString("name"));
        editor.putString("username", userDocument.getString("username"));
        editor.putString("accountType", userDocument.getString("accountType"));
        editor.apply();
    }

    protected void loadFallbackUserData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(
                "UserPrefs", Context.MODE_PRIVATE);

        profileName.setText(prefs.getString("name", "User"));
        textUserName.setText("@" + prefs.getString("username", "username"));
        textAccountType.setText(prefs.getString("accountType", "Account"));
    }
}
