package com.snorax;

public class AnalyticsData {
    private int muteCount;
    private int unmuteCount;
    private int vibrateCount;
    private long timeSaved; // in minutes

    public AnalyticsData(int muteCount, int unmuteCount, int vibrateCount, long timeSaved) {
        this.muteCount = muteCount;
        this.unmuteCount = unmuteCount;
        this.vibrateCount = vibrateCount;
        this.timeSaved = timeSaved;
    }

    public int getMuteCount() {
        return muteCount;
    }

    public int getUnmuteCount() {
        return unmuteCount;
    }

    public int getVibrateCount() {
        return vibrateCount;
    }

    public long getTimeSaved() {
        return timeSaved;
    }
}