package com.iuxoa.paradoxone;

import android.content.Context;
import android.content.SharedPreferences;

public class ProfileManager {
    private SharedPreferences sharedPreferences;

    public ProfileManager(Context context) {
        sharedPreferences = context.getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
    }

    public void saveHighScore(int score) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("HighScore", score);
        editor.apply();
    }

    public int getHighScore() {
        return sharedPreferences.getInt("HighScore", 0);
    }
}

