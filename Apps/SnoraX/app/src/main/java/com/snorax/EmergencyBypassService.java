package com.snorax;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class EmergencyBypassService extends NotificationListenerService {
    private AudioManager audioManager;
    private SharedPreferences sharedPreferences;
    private NotificationManager notificationManager;
    public static final String CHANNEL_ID = "emergency_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        sharedPreferences = getSharedPreferences("emergency_prefs", MODE_PRIVATE);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Emergency Bypass",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for emergency notifications");
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (isEmergencyNotification(sbn)) {
            handleEmergencyNotification(sbn);
        }
    }

    private boolean isEmergencyNotification(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        String emergencyApp = sharedPreferences.getString("emergency_app", "");
        return packageName.equals(emergencyApp);
    }

    private void handleEmergencyNotification(StatusBarNotification sbn) {
        // Unmute notifications temporarily
        if (audioManager != null) {
            audioManager.adjustStreamVolume(
                    AudioManager.STREAM_NOTIFICATION,
                    AudioManager.ADJUST_UNMUTE,
                    0);

            // Show a high priority notification
            Notification notification = sbn.getNotification();

            // For Android O and above, set the channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder builder = Notification.Builder.recoverBuilder(this, notification);
                builder.setChannelId(CHANNEL_ID);
                notification = builder.build();
            }

            notificationManager.notify(sbn.getId(), notification);

            // Schedule re-muting after a delay
            new android.os.Handler().postDelayed(() -> {
                audioManager.adjustStreamVolume(
                        AudioManager.STREAM_NOTIFICATION,
                        AudioManager.ADJUST_MUTE,
                        0);
            }, 30000); // 30 seconds
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // Handle if needed
    }
}