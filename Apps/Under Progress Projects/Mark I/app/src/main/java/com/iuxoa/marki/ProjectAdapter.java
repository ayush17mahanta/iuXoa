package com.iuxoa.marki;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iuxoa.marki.model.Project;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {
    private static final String TAG = "ProjectAdapter";
    private final List<Project> projectList;
    private final Context context;

    public ProjectAdapter(List<Project> projectList, Context context) {
        this.projectList = projectList;
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
        Log.d(TAG, "Binding project: " + project.getTitle());

        holder.title.setText(project.getTitle() != null ? project.getTitle() : "No Title");
        holder.description.setText(project.getDescription() != null ? project.getDescription() : "No Description");
        holder.budget.setText(String.format("Budget: $%.2f", project.getBudget()));
        holder.deadline.setText(project.getDeadline() != null ? "Deadline: " + project.getDeadline() : "No Deadline");
        holder.skills.setText(project.getSkills() != null ? "Skills: " + project.getSkills() : "No Skills Specified");
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public void updateProjects(List<Project> newProjects) {
        projectList.clear();
        projectList.addAll(newProjects);
        notifyDataSetChanged();
    }

    static class ProjectViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, budget, deadline, skills;

        public ProjectViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.projectTitleText);
            description = itemView.findViewById(R.id.projectDescriptionText);
            budget = itemView.findViewById(R.id.projectBudgetText);
            deadline = itemView.findViewById(R.id.projectDeadlineText);
            skills = itemView.findViewById(R.id.projectSkillsText);
        }
    }
}