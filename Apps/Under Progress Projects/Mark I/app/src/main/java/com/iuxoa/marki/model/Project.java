package com.iuxoa.marki.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "projects")
public class Project {

    @PrimaryKey(autoGenerate = true) // Auto-generating the ID
    private int id;

    private String title;
    private String description;
    private String budget;
    private String deadline;
    private String skills;

    // Default constructor
    public Project(String title, String description, String budget, String deadline, String skills) {
        this.title = title;
        this.description = description;
        this.budget = budget;
        this.deadline = deadline;
        this.skills = skills;
    }

    // Getter and Setter methods for the id field
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter and Setter methods for other fields
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }
}
