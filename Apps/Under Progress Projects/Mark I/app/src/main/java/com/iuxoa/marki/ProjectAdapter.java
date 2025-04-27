package com.iuxoa.marki;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iuxoa.marki.model.Project;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    private List<Project> projectList;
    private Context context;

    public ProjectAdapter(List<Project> projectList, Context context) {
        this.projectList = projectList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projectList.get(position);
        holder.title.setText(project.getTitle());
        holder.description.setText(project.getDescription());
        holder.budget.setText(String.format("Budget: $%.2f", project.getBudget()));
        holder.deadline.setText("Deadline: " + project.getDeadline());
        holder.skills.setText("Skills: " + project.getSkills());
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public void updateProjects(List<Project> newProjects) {
        projectList = newProjects;
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