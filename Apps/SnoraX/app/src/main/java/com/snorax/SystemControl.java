package com.snorax;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.app.NotificationManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class SystemControl {
    private static final String TAG = "SystemControl";

    public static final int ACTION_NORMAL = 1;  // Ring mode
    public static final int ACTION_VIBRATE = 2; // Vibrate mode
    public static final int ACTION_SILENT = 3;  // Silent mode (mute)

    public static void setRingerMode(Context context, int mode) {
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager == null) return;

            // Check for DND policy access for Android 7.0+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                if (!notificationManager.isNotificationPolicyAccessGranted()) {
                    // Show dialog to guide user to settings
                    showPolicyAccessDialog(context);
                    return;
                }
            }

            switch (mode) {
                case ACTION_NORMAL:
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    break;
                case ACTION_VIBRATE:
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    break;
                case ACTION_SILENT:
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "Ringer mode change failed", e);
            Toast.makeText(context, "Failed to change ringer mode", Toast.LENGTH_SHORT).show();
        }
    }

    private static void showPolicyAccessDialog(final Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Permission Required")
                .setMessage("Please grant notification policy access to change ringer modes")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    context.startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public static int getCurrentRingerMode(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            return audioManager.getRingerMode();
        }
        return -1;
    }

    public static String getCurrentRingerModeString(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null) return "Normal";

        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL: return "Normal";
            case AudioManager.RINGER_MODE_VIBRATE: return "Vibrate";
            case AudioManager.RINGER_MODE_SILENT: return "Silent";
            default: return "Normal";
        }
    }
}