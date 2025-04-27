package com.iuxoa.marki;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.iuxoa.marki.model.AppDatabase;
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projectList.get(position);
        holder.title.setText(project.getTitle());
        holder.description.setText(project.getDescription());

        // Handle item click (edit or delete)
        holder.itemView.setOnClickListener(v -> {
            // Handle item click (e.g., open project details or edit)
            Intent intent = new Intent(context, EditProjectActivity.class);
            intent.putExtra("project_id", project.getId()); // Pass project id to edit
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            // Handle long click (e.g., delete project)
            deleteProject(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    private void deleteProject(int position) {
        Project projectToDelete = projectList.get(position);
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "my-database-name").allowMainThreadQueries().build();
        db.projectDao().delete(projectToDelete);

        projectList.remove(position);
        notifyItemRemoved(position);
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;

        public ProjectViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.projectTitle);
            description = itemView.findViewById(R.id.projectDescription);
        }
    }
}
