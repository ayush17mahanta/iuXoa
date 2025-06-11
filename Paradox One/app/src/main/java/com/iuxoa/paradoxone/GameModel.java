package com.iuxoa.paradoxone;

import android.content.Context;
import android.content.SharedPreferences;

public class GameModel {

    private static final String PREFS_NAME = "game_prefs";
    private static final String KEY_SCORE = "key_score";
    private static final String KEY_LEVEL = "key_level";
    private static final String KEY_RUN_COUNT = "key_run_count";

    private int score = 0;
    private int currentLevel = 1;
    private int runCount = 0;

    private SharedPreferences prefs;

    public GameModel(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        load();
    }

    private void load() {
        score = prefs.getInt(KEY_SCORE, 0);
        currentLevel = prefs.getInt(KEY_LEVEL, 1);
        runCount = prefs.getInt(KEY_RUN_COUNT, 0);
    }

    public void save() {
        prefs.edit()
                .putInt(KEY_SCORE, score)
                .putInt(KEY_LEVEL, currentLevel)
                .putInt(KEY_RUN_COUNT, runCount)
                .apply();
    }

    // Getters and setters
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int level) { this.currentLevel = level; }

    public int getRunCount() { return runCount; }
    public void incrementRunCount() { this.runCount++; }

    public void reset() {
        score = 0;
        currentLevel = 1;
        save();
    }
}
