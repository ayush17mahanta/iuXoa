package com.snorax;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.Locale;

import soup.neumorphism.NeumorphButton;

public class SettingsFragment extends Fragment {

    private Spinner spinnerTheme, chooseLanguage;
    private NeumorphButton btnLogout;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "UserSettings";
    private static final String KEY_LANGUAGE = "app_language";

    private final String[] languages = {"English", "Hindi"};
    private final String[] langCodes = {"en", "hi"};

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Apply language settings when fragment attaches
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String langCode = prefs.getString(KEY_LANGUAGE, "en");
        updateBaseContextLocale(context, langCode);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        setupLanguageSpinner(view);
        setupLogoutButton(view);

        return view;
    }

    private void setupLanguageSpinner(View view) {
        chooseLanguage = view.findViewById(R.id.chooseLanguage);
        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(
                requireContext(),
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
                    requireActivity().recreate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupLogoutButton(View view) {
        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            SharedPrefManager.logout(requireContext());
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
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