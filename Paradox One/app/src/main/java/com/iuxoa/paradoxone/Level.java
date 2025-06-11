package com.iuxoa.paradoxone;

import java.util.ArrayList;
import java.util.List;

public class Level {

    public enum Side {
        LEFT, RIGHT;
    }

    private int id;
    private String name;
    private String difficulty;       // e.g., "Easy", "Medium", "Hard"
    private String theme;            // e.g., "Time", "Fear", "Memory"
    private int rewardPoints;
    private boolean unlocked = false;
    private boolean completed = false;
    private boolean collapsed = false;
    private int requiredFailuresToUnlock = 0;

    // Branching to other levels (can represent progression branches)
    private List<Branch> branches = new ArrayList<>();

    public Level(int id, String name, String difficulty, String theme, int rewardPoints) {
        this.id = id;
        this.name = name;
        this.difficulty = difficulty;
        this.theme = theme;
        this.rewardPoints = rewardPoints;
    }

    // Add a branch to another level with side info
    public void addBranch(Level targetLevel, Side side) {
        if (targetLevel != null) {
            branches.add(new Branch(targetLevel, side));
        }
    }

    // Getters and setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDifficulty() { return difficulty; }
    public String getTheme() { return theme; }
    public int getRewardPoints() { return rewardPoints; }
    public boolean isUnlocked() { return unlocked; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public boolean isCollapsed() { return collapsed; }
    public void setCollapsed(boolean collapsed) { this.collapsed = collapsed; }
    public int getRequiredFailuresToUnlock() { return requiredFailuresToUnlock; }
    public void setRequiredFailuresToUnlock(int requiredFailuresToUnlock) {
        this.requiredFailuresToUnlock = requiredFailuresToUnlock;
    }
    public List<Branch> getBranches() { return branches; }

    @Override
    public String toString() {
        return "Level " + id + ": " + name +
                " [Difficulty: " + difficulty +
                ", Theme: " + theme +
                ", Reward: " + rewardPoints +
                ", Unlocked: " + unlocked +
                ", Completed: " + completed +
                ", Collapsed: " + collapsed +
                ", RequiredFails: " + requiredFailuresToUnlock + "]";
    }

    // Nested Branch class for connecting levels
    public static class Branch {
        private Level target;
        private Side side;

        public Branch(Level target, Side side) {
            this.target = target;
            this.side = side;
        }

        public Level getTarget() { return target; }
        public Side getSide() { return side; }
    }
}
