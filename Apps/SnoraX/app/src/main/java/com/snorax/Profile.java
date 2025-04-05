package com.snorax;

import java.util.HashMap;
import java.util.Map;

public class Profile {
    private String name;
    private Map<String, String> appSettings; // Key: App Name, Value: Notification State (Mute/Unmute/Vibrate)

    public Profile(String name) {
        this.name = name;
        this.appSettings = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getAppSettings() {
        return appSettings;
    }

    public void setAppSettings(Map<String, String> appSettings) {
        this.appSettings = appSettings;
    }

    public void addAppSetting(String appName, String state) {
        appSettings.put(appName, state);
    }

    public String getAppSetting(String appName) {
        return appSettings.get(appName);
    }
}