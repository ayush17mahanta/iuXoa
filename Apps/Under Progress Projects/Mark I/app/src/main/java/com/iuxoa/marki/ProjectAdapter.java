package com.iuxoa.marki;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.iuxoa.marki.model.Project;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {
    private final List<Project> projectList;
    private final Context context;
    private OnProjectClickListener listener;

    public interface OnProjectClickListener {
        void onProjectClick(Project project);
    }

    public void setOnProjectClickListener(OnProjectClickListener listener) {
        this.listener = listener;
    }

    public ProjectAdapter(List<Project> projectList, Context context) {
        this.projectList = new ArrayList<>(projectList); // Defensive copy
        this.context = context;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projectList.get(position);

        holder.title.setText(project.getTitle() != null ? project.getTitle() : "Untitled Project");
        holder.description.setText(project.getDescription() != null ? project.getDescription() : "No description");
        holder.budget.setText(String.format(Locale.getDefault(), "Budget: $%.2f", project.getBudget()));
        holder.deadline.setText(project.getDeadline() != null ? "Deadline: " + project.getDeadline() : "No deadline");

        // Populate skills as Chips
        holder.skillsChipGroup.removeAllViews();
        if (project.getSkills() != null && !project.getSkills().isEmpty()) {
            String[] skills = project.getSkills().split(",");
            for (String skill : skills) {
                Chip chip = new Chip(context);
                chip.setText(skill.trim());
                chip.setChipBackgroundColorResource(R.color.chip_background); // Replace with actual color or use ColorStateList
                chip.setTextColor(Color.WHITE);
                chip.setClickable(false);
                chip.setChipCornerRadius(16);
                holder.skillsChipGroup.addView(chip);
            }
        }

        // Handle click event
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProjectClick(project);
            }
        });
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public void updateProjects(List<Project> newProjects) {
        projectList.clear();
        projectList.addAll(new ArrayList<>(newProjects)); // Defensive copy
        notifyDataSetChanged();
    }

    static class ProjectViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, budget, deadline;
        ChipGroup skillsChipGroup;

        public ProjectViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.projectTitleText);
            description = itemView.findViewById(R.id.projectDescriptionText);
            budget = itemView.findViewById(R.id.projectBudgetText);
            deadline = itemView.findViewById(R.id.projectDeadlineText);
            skillsChipGroup = itemView.findViewById(R.id.projectSkillsChipGroup);
        }
    }
}
