package com.iuxoa.marki;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.iuxoa.marki.model.Project;

import java.util.ArrayList;
import java.util.List;

public class FreelancerHomeFragment extends BaseHomeFragment {
    private static final String TAG = "FreelancerHomeFragment";
    private RecyclerView recyclerView;
    private TextView noProjectsText;
    private SwipeRefreshLayout swipeRefresh;
    private ProjectAdapter adapter;
    private List<Project> availableProjects;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_freelancer_home, container, false);
        initializeComponents(view);
        setupUI();
        loadData();
        return view;
    }

    private void initializeComponents(View view) {
        initializeProfileViews(view);
        recyclerView = view.findViewById(R.id.recyclerViewProjects);
        noProjectsText = view.findViewById(R.id.noProjectsText);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        availableProjects = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void updateProfileUI(DocumentSnapshot userDocument) {
        if (userDocument == null || !userDocument.exists()) {
            Toast.makeText(getActivity(), "User data not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = userDocument.getString("name");
        String username = userDocument.getString("username");
        String accountType = userDocument.getString("accountType");

        if (name != null) profileName.setText(name);
        if (username != null) textUserName.setText("@" + username);
        textAccountType.setText("Freelancer");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getPhotoUrl() != null && profileImageView != null) {
            Glide.with(requireContext())
                    .load(user.getPhotoUrl())
                    .into(profileImageView);
        }

        if (!"freelancer".equalsIgnoreCase(accountType)) {
            Toast.makeText(getActivity(),
                    "This account is not registered as a freelancer",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void setupUI() {
        adapter = new ProjectAdapter(availableProjects, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(() -> {
            availableProjects.clear();
            adapter.notifyDataSetChanged();
            loadData();
        });
    }

    private void loadData() {
        loadUserProfile();
        loadAvailableProjects();
    }

    private void loadAvailableProjects() {
        Log.d(TAG, "Attempting to load projects...");
        swipeRefresh.setRefreshing(true);

        db.collection("projects")
                .whereEqualTo("status", "open")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefresh.setRefreshing(false);

                    if (!task.isSuccessful()) {
                        Log.e(TAG, "Load failed", task.getException());
                        noProjectsText.setText("Error loading projects. Pull to refresh.");
                        Toast.makeText(getContext(), "Network error. Check connection.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (task.getResult().isEmpty()) {
                        Log.d(TAG, "No projects found in query");
                        noProjectsText.setText("No open projects available");
                        availableProjects.clear();
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    availableProjects.clear();
                    for (DocumentSnapshot doc : task.getResult()) {
                        try {
                            Project project = doc.toObject(Project.class);
                            if (project != null) {
                                project.setId(doc.getId());
                                availableProjects.add(project);
                                Log.d(TAG, "Loaded project: " + project.getTitle());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing document " + doc.getId(), e);
                        }
                    }

                    if (availableProjects.isEmpty()) {
                        noProjectsText.setText("No projects match your filters");
                    }

                    adapter.notifyDataSetChanged();
                    updateEmptyState();
                });
    }

    private void updateEmptyState() {
        boolean isEmpty = availableProjects.isEmpty();
        noProjectsText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}