package com.iuxoa.marki.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

public class Project {
    private String id;
    private String title;
    private String description;
    private double budget;
    private String deadline;
    private String skills;
    private String userId;

    // Empty constructor required for Firebase
    public Project() {}

    public Project(String title, String description, double budget,
                   String deadline, String skills, String userId) {
        this.title = title;
        this.description = description;
        this.budget = budget;
        this.deadline = deadline;
        this.skills = skills;
        this.userId = userId;
    }

    @Exclude
    public String getId() { return id; }

    @Exclude
    public void setId(String id) { this.id = id; }

    @PropertyName("title")
    public String getTitle() { return title; }

    @PropertyName("title")
    public void setTitle(String title) { this.title = title; }

    @PropertyName("description")
    public String getDescription() { return description; }

    @PropertyName("description")
    public void setDescription(String description) { this.description = description; }

    @PropertyName("budget")
    public double getBudget() { return budget; }

    @PropertyName("budget")
    public void setBudget(double budget) { this.budget = budget; }

    @PropertyName("deadline")
    public String getDeadline() { return deadline; }

    @PropertyName("deadline")
    public void setDeadline(String deadline) { this.deadline = deadline; }

    @PropertyName("skills")
    public String getSkills() { return skills; }

    @PropertyName("skills")
    public void setSkills(String skills) { this.skills = skills; }

    @PropertyName("user_id")
    public String getUserId() { return userId; }

    @PropertyName("user_id")
    public void setUserId(String userId) { this.userId = userId; }
}
