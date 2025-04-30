package com.iuxoa.marki;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import com.iuxoa.marki.model.Project;
import com.iuxoa.marki.viewmodel.PostProjectActivity;

import java.util.ArrayList;
import java.util.List;

public class ClientHomeFragment extends BaseHomeFragment {

    private static final String TAG = "ClientHomeFragment";
    private RecyclerView recyclerView;
    private FloatingActionButton fabPostProject;
    private Button logoutButton;
    private SwipeRefreshLayout swipeRefresh;
    private TextView noProjectsText;
    private ProjectAdapter adapter;
    private List<Project> projectList;

    private ImageView profileImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_home, container, false);
        initializeComponents(view);
        setupUI();
        loadData();
        return view;
    }

    private void initializeComponents(View view) {
        initializeProfileViews(view); // likely sets profileName, textUserName, textAccountType
        profileImage = view.findViewById(R.id.profile_image); // Ensure this ID exists in layout

        recyclerView = view.findViewById(R.id.recyclerViewClientProjects);
        fabPostProject = view.findViewById(R.id.fabPostProject);
        logoutButton = view.findViewById(R.id.logoutButton);
        noProjectsText = view.findViewById(R.id.noClientProjectsText);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        projectList = new ArrayList<>();
    }

    @Override
    protected void updateProfileUI(DocumentSnapshot userDocument) {
        String name = userDocument.getString("name");
        String username = userDocument.getString("username");
        String accountType = userDocument.getString("accountType");

        if (name != null) profileName.setText(name);
        if (username != null) textUserName.setText("@" + username);
        textAccountType.setText("Client");

        // Gmail Profile Picture
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getPhotoUrl() != null && profileImage != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(profileImage);
        }

        if (!"client".equalsIgnoreCase(accountType)) {
            Toast.makeText(getActivity(),
                    "This account is not registered as a client",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void setupUI() {
        adapter = new ProjectAdapter(projectList, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::refreshData);

        fabPostProject.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), PostProjectActivity.class));
        });

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
        });
    }

    private void loadData() {
        loadUserProfile();
        loadClientProjects();
    }

    private void refreshData() {
        loadUserProfile();
        loadClientProjects();
    }

    private void loadClientProjects() {
        if (currentUserId == null) return;

        db.collection("projects")
                .whereEqualTo("clientId", currentUserId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefresh.setRefreshing(false);

                    if (task.isSuccessful()) {
                        projectList.clear();
                        for (DocumentSnapshot doc : task.getResult()) {
                            Project project = doc.toObject(Project.class);
                            if (project != null) {
                                project.setId(doc.getId());
                                projectList.add(project);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        updateEmptyState();
                    } else {
                        Log.e(TAG, "Error loading projects", task.getException());
                        Toast.makeText(getActivity(),
                                "Failed to load projects: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateEmptyState() {
        boolean isEmpty = projectList.isEmpty();
        noProjectsText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}
