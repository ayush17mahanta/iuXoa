package com.snorax;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import java.util.Locale;

public class LanguageHelper {

    private static final String LANGUAGE_PREF_KEY = "language_pref";
    private static final String DEFAULT_LANGUAGE = "en"; // Default language is English

    public static void setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }

        resources.updateConfiguration(config, resources.getDisplayMetrics());

        // Save the selected language preference
        SharedPreferences preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LANGUAGE_PREF_KEY, languageCode);
        editor.apply();
    }

    public static String getLanguage(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return preferences.getString(LANGUAGE_PREF_KEY, DEFAULT_LANGUAGE);
    }
}