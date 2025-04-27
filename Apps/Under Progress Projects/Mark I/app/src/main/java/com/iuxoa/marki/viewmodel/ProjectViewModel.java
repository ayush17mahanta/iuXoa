package com.iuxoa.marki.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.iuxoa.marki.model.Project;

import java.util.ArrayList;
import java.util.List;

public class ProjectViewModel extends AndroidViewModel {
    private static final String TAG = "ProjectViewModel";
    private final MutableLiveData<List<Project>> allProjects = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ProjectViewModel(@NonNull Application application) {
        super(application);
        setupFirestoreListener();
    }

    public LiveData<List<Project>> getAllProjects() {
        return allProjects;
    }

    private void setupFirestoreListener() {
        db.collection("Projects")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Listen failed.", error);
                        allProjects.setValue(new ArrayList<>()); // Set empty list on error
                        return;
                    }

                    if (value == null || value.isEmpty()) {
                        Log.d(TAG, "No projects found");
                        allProjects.setValue(new ArrayList<>());
                        return;
                    }

                    List<Project> projectList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        try {
                            Project project = doc.toObject(Project.class);
                            project.setId(doc.getId()); // Optional: Store document ID
                            projectList.add(project);
                        } catch (Exception e) {
                            Log.e(TAG, "Error converting document " + doc.getId(), e);
                        }
                    }
                    allProjects.setValue(projectList);
                });
    }

    // Optional: Add method to manually refresh data
    public void refreshProjects() {
        db.collection("Projects")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Manual refresh successful");
                    } else {
                        Log.e(TAG, "Manual refresh failed", task.getException());
                    }
                });
    }
}