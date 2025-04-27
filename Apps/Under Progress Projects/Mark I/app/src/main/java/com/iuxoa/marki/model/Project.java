package com.iuxoa.marki.model;

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

    // Constructor without ID (for new projects)
    public Project(String title, String description, double budget,
                   String deadline, String skills, String userId) {
        this.title = title;
        this.description = description;
        this.budget = budget;
        this.deadline = deadline;
        this.skills = skills;
        this.userId = userId;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}