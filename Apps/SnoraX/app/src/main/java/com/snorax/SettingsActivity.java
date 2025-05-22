package com.snorax;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Locale;

import soup.neumorphism.NeumorphButton;

public class SettingsActivity extends AppCompatActivity {

    private Spinner spinnerTheme, chooseLanguage;
    private NeumorphButton btnLogout;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "UserSettings";
    private static final String KEY_LANGUAGE = "app_language";

    private final String[] languages = {"English", "Hindi"};
    private final String[] langCodes = {"en", "hi"};

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String langCode = prefs.getString(KEY_LANGUAGE, "en");
        Context context = updateBaseContextLocale(newBase, langCode);
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize theme before super.onCreate to prevent flash
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupLanguageSpinner();
        setupLogoutButton();
    }




    private void setupLanguageSpinner() {
        chooseLanguage = findViewById(R.id.chooseLanguage);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                languages
        );
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseLanguage.setAdapter(languageAdapter);

        String currentLangCode = sharedPreferences.getString(KEY_LANGUAGE, "en");
        int langIndex = Arrays.asList(langCodes).indexOf(currentLangCode);
        if (langIndex >= 0) {
            chooseLanguage.setSelection(langIndex);
        }

        chooseLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLangCode = langCodes[position];
                if (!selectedLangCode.equals(sharedPreferences.getString(KEY_LANGUAGE, "en"))) {
                    sharedPreferences.edit().putString(KEY_LANGUAGE, selectedLangCode).apply();
                    applyLocale(selectedLangCode);
                    recreate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupLogoutButton() {
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            SharedPrefManager.logout(getApplicationContext());
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private Context updateBaseContextLocale(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);
        return context.createConfigurationContext(config);
    }

    private void applyLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}