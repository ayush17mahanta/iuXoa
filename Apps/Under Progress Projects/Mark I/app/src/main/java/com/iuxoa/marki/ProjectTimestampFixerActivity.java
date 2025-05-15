package com.iuxoa.marki;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProjectTimestampFixerActivity extends AppCompatActivity {
    private static final String TAG = "ProjectTimestampFixer";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("projects")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int updatedCount = 0;

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        if (!doc.contains("createdAt") || doc.getTimestamp("createdAt") == null) {
                            String docId = doc.getId();
                            Map<String, Object> update = new HashMap<>();
                            update.put("createdAt", FieldValue.serverTimestamp());

                            db.collection("projects").document(docId)
                                    .update(update)
                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Updated project: " + docId))
                                    .addOnFailureListener(e -> Log.e(TAG, "Failed to update: " + docId, e));

                            updatedCount++;
                        }
                    }

                    Log.i(TAG, "Finished. Projects updated: " + updatedCount);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching projects", e));
    }
}
