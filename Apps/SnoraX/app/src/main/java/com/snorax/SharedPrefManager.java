package com.snorax;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static final String PREF_NAME = "MyAppPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Save login state (true if logged in, false otherwise)
    public static void saveLoginState(Context context, boolean isLoggedIn) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    // Check if user is logged in
    public static boolean isLoggedIn(Context context) {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Clear all preferences (logout)
    public static void logout(Context context) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.clear(); // Clears all stored keys
        editor.apply();
    }
}
