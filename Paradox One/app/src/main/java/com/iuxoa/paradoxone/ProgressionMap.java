package com.iuxoa.paradoxone;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ProgressionMap {

    private Map<Integer, Level> levels = new HashMap<>();
    private Level currentLevel;
    private int failCount = 0;

    private ProfileManager profileManager;
    private Context context;

    public ProgressionMap(Context context) {
        this.context = context;
        this.profileManager = new ProfileManager(context);
        loadLevelsFromJSON();
        loadProgress();
        updateUnlocks();
    }

    private void loadProgress() {
        int savedLevel = profileManager.getCurrentLevel();
        this.failCount = profileManager.getFailCount();
        this.currentLevel = levels.get(savedLevel);
        if (this.currentLevel == null && !levels.isEmpty()) {
            this.currentLevel = levels.values().iterator().next();
        }
    }

    private void loadLevelsFromJSON() {
        try {
            InputStream is = context.getAssets().open("levels.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String jsonStr = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonLevels = new JSONArray(jsonStr);

            levels.clear();
            for (int i = 0; i < jsonLevels.length(); i++) {
                JSONObject obj = jsonLevels.getJSONObject(i);
                Level level = new Level(
                        obj.getInt("id"),
                        obj.getString("name"),
                        obj.getString("difficulty"),
                        obj.getString("theme"),
                        obj.getInt("rewardPoints")
                );
                levels.put(level.getId(), level);
            }
        } catch (Exception e) {
            Log.e("ProgressionMap", "Error loading levels.json", e);
            createDefaultLevels();
        }
    }

    private void createDefaultLevels() {
        levels.clear();
        Level l1 = new Level(1, "Intro Level", "Easy", "Time", 100);
        Level l2 = new Level(2, "Fear Challenge", "Medium", "Fear", 200);
        Level l3 = new Level(3, "Memory Maze", "Hard", "Memory", 300);

        l1.setUnlocked(true);

        levels.put(l1.getId(), l1);
        levels.put(l2.getId(), l2);
        levels.put(l3.getId(), l3);

        currentLevel = l1;
    }

    public void saveProgress() {
        if (currentLevel != null) {
            profileManager.setCurrentLevel(currentLevel.getId());
        }
        profileManager.setFailCount(failCount);
    }

    public void updateUnlocks() {
        int currentId = currentLevel != null ? currentLevel.getId() : 0;

        for (Level level : levels.values()) {
            if (level.getId() < currentId) {
                level.setUnlocked(true);
                level.setCompleted(true);
            } else if (level.getId() == currentId) {
                level.setUnlocked(true);
                level.setCompleted(false);
            } else {
                level.setUnlocked(false);
                level.setCompleted(false);
            }
        }

        // Unlock special levels if fail count is high
        for (Level level : levels.values()) {
            if (level.getRequiredFailuresToUnlock() > 0 && failCount >= level.getRequiredFailuresToUnlock()) {
                level.setUnlocked(true);
            }
        }
    }

    public boolean moveToLevel(int levelId) {
        Level next = levels.get(levelId);
        if (next != null && next.isUnlocked() && !next.isCollapsed()) {
            currentLevel = next;
            saveProgress();
            updateUnlocks();
            return true;
        }
        return false;
    }

    public void onFail() {
        failCount++;
        if (currentLevel != null) {
            currentLevel.setCollapsed(true);
        }
        saveProgress();
        updateUnlocks();
    }

    public void resetProgress() {
        failCount = 0;
        for (Level level : levels.values()) {
            level.setUnlocked(false);
            level.setCollapsed(false);
            level.setCompleted(false);
        }
        if (!levels.isEmpty()) {
            Level first = levels.values().iterator().next();
            first.setUnlocked(true);
            currentLevel = first;
        }
        saveProgress();
        updateUnlocks();
    }

    public Map<Integer, Level> getLevels() {
        return levels;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

}
