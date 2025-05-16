package com.iuxoa.datadrop;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileSettingsActivity extends AppCompatActivity {

    private Button btnUpdatePayment, btnExportData, btnDeleteData;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profileScroll), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnUpdatePayment = findViewById(R.id.btnUpdatePayment);
        btnExportData = findViewById(R.id.btnExportData);
        btnDeleteData = findViewById(R.id.btnDeleteData);

        btnUpdatePayment.setOnClickListener(v -> {
            // TODO: Launch payment update flow or open Stripe dashboard in webview
            Toast.makeText(this, "Payment update flow coming soon!", Toast.LENGTH_SHORT).show();
        });

        btnExportData.setOnClickListener(v -> exportUserData());

        btnDeleteData.setOnClickListener(v -> confirmDeleteData());
    }

    private void exportUserData() {
        String uid = auth.getCurrentUser().getUid();

        Toast.makeText(this, "Preparing your data...", Toast.LENGTH_SHORT).show();

        // Fetch user profile
        db.collection("users").document(uid).get().addOnSuccessListener(userDoc -> {
            if (!userDoc.exists()) {
                Toast.makeText(this, "No user data found.", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> userData = userDoc.getData();

            // Fetch all survey responses by user
            db.collection("survey_responses")
                    .whereEqualTo("userId", uid)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        List<Map<String, Object>> responses = new ArrayList<>();
                        for (com.google.firebase.firestore.QueryDocumentSnapshot doc : querySnapshot) {
                            Map<String, Object> resp = doc.getData();
                            resp.put("docId", doc.getId());
                            responses.add(resp);
                        }

                        // Combine all data into one JSON-like map
                        Map<String, Object> exportData = new HashMap<>();
                        exportData.put("profile", userData);
                        exportData.put("survey_responses", responses);

                        // Convert map to JSON string
                        String jsonString = new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(exportData);

                        shareJsonData(jsonString);

                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to fetch survey responses: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
    private void shareJsonData(String jsonData) {
        try {
            // Write JSON string to cache file
            File cacheFile = new File(getCacheDir(), "userdata_export.json");
            try (FileWriter writer = new FileWriter(cacheFile)) {
                writer.write(jsonData);
            }

            // Create share intent
            android.net.Uri fileUri = androidx.core.content.FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    cacheFile);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/json");
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share your data"));

        } catch (Exception e) {
            Toast.makeText(this, "Failed to export data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    private void confirmDeleteData() {
        new AlertDialog.Builder(this)
                .setTitle("Delete My Data")
                .setMessage("Are you sure you want to delete your personal data? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteUserData())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteUserData() {
        String uid = auth.getCurrentUser().getUid();
        // Delete user data from Firestore
        db.collection("users").document(uid).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Your data has been deleted.", Toast.LENGTH_LONG).show();
                    auth.signOut();
                    finish(); // Close profile and probably redirect to login
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
