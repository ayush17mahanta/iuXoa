package com.iuxoa.paradoxone;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileManager {
    private static final String PREFS_NAME = "UserProfilePrefs";
    private static final String KEY_SCORES = "scores";
    private static final String KEY_RUNS = "runs";
    private static final String KEY_CURRENT_LEVEL = "current_level";
    private static final String KEY_FAIL_COUNT = "fail_count";
    private static final String KEY_PHILOSOPHY_CHOICES = "philosophy_choices";
    private static final String KEY_THEME_PREFERENCES = "theme_prefs";

    private SharedPreferences prefs;

    public ProfileManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Score management
    public void saveScore(int score) {
        List<Integer> scores = getAllScores();
        scores.add(score);
        Collections.sort(scores, Collections.reverseOrder());
        if (scores.size() > 10) {
            scores = scores.subList(0, 10);
        }
        saveScores(scores);
    }

    public List<Integer> getAllScores() {
        String jsonString = prefs.getString(KEY_SCORES, "[]");
        List<Integer> scores = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                scores.add(jsonArray.getInt(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Collections.sort(scores, Collections.reverseOrder());
        return scores;
    }

    public int getHighScore() {
        List<Integer> scores = getAllScores();
        return scores.isEmpty() ? 0 : scores.get(0);
    }

    private void saveScores(List<Integer> scores) {
        JSONArray jsonArray = new JSONArray();
        for (int score : scores) {
            jsonArray.put(score);
        }
        prefs.edit().putString(KEY_SCORES, jsonArray.toString()).apply();
    }

    // Run count management
    public void incrementRunCount() {
        prefs.edit().putInt(KEY_RUNS, getRunCount() + 1).apply();
    }

    public int getRunCount() {
        return prefs.getInt(KEY_RUNS, 0);
    }

    // Level progression management
    public int getCurrentLevel() {
        return prefs.getInt(KEY_CURRENT_LEVEL, 1); // Default to level 1
    }

    public void setCurrentLevel(int level) {
        prefs.edit().putInt(KEY_CURRENT_LEVEL, level).apply();
    }

    // Failure tracking
    public int getFailCount() {
        return prefs.getInt(KEY_FAIL_COUNT, 0);
    }

    public void setFailCount(int count) {
        prefs.edit().putInt(KEY_FAIL_COUNT, count).apply();
    }

    // Philosophy choice tracking
    public void recordPhilosophyChoice(String choice) {
        String choices = prefs.getString(KEY_PHILOSOPHY_CHOICES, "");
        prefs.edit().putString(KEY_PHILOSOPHY_CHOICES, choices + choice + ",").apply();
    }

    public String getPhilosophyChoices() {
        return prefs.getString(KEY_PHILOSOPHY_CHOICES, "");
    }

    // Theme preferences
    public void setThemePreference(String theme, boolean preferred) {
        prefs.edit().putBoolean(KEY_THEME_PREFERENCES + "_" + theme, preferred).apply();
    }

    public boolean getThemePreference(String theme) {
        return prefs.getBoolean(KEY_THEME_PREFERENCES + "_" + theme, false);
    }

    // Clear all data
    public void clearAll() {
        prefs.edit().clear().apply();
    }
}