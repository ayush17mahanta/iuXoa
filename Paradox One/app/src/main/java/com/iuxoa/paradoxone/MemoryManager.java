package com.iuxoa.paradoxone;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MemoryManager {
    private static final String PREFS_NAME = "paradoxone_prefs";
    private static final String KEY_SCORES = "scores";

    private SharedPreferences prefs;

    public MemoryManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Save a new score to the list
    public void saveScore(int score) {
        List<Integer> scores = loadScores();
        scores.add(score);
        saveScores(scores);
    }

    // Load all scores
    public List<Integer> loadScores() {
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
        return scores;
    }
    public int getBestScore() {
        List<Integer> scores = loadScores();
        int best = 0;
        for (int score : scores) {
            if (score > best) best = score;
        }
        return best;
    }


    // Save the entire list as JSON string
    private void saveScores(List<Integer> scores) {
        JSONArray jsonArray = new JSONArray();
        for (Integer score : scores) {
            jsonArray.put(score);
        }
        prefs.edit().putString(KEY_SCORES, jsonArray.toString()).apply();
    }

    // Optional: Clear all saved scores
    public void clearScores() {
        prefs.edit().remove(KEY_SCORES).apply();
    }
}
