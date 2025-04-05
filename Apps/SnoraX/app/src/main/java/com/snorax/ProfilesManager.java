package com.snorax;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProfilesManager {
    private static final String PROFILES_KEY = "profiles";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public ProfilesManager(Context context) {
        sharedPreferences = context.getSharedPreferences("profiles_prefs", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveProfiles(List<Profile> profiles) {
        String profilesJson = gson.toJson(profiles);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PROFILES_KEY, profilesJson);
        editor.apply();
    }

    public List<Profile> getProfiles() {
        String profilesJson = sharedPreferences.getString(PROFILES_KEY, null);
        if (profilesJson == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Profile>>() {}.getType();
        return gson.fromJson(profilesJson, type);
    }

    public void addProfile(Profile profile) {
        List<Profile> profiles = getProfiles();
        profiles.add(profile);
        saveProfiles(profiles);
    }

    public void updateProfile(int index, Profile profile) {
        List<Profile> profiles = getProfiles();
        profiles.set(index, profile);
        saveProfiles(profiles);
    }

    public void deleteProfile(int index) {
        List<Profile> profiles = getProfiles();
        profiles.remove(index);
        saveProfiles(profiles);
    }
}