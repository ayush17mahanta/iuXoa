package com.snorax;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "Device booted, rescheduling alarms");
            scheduleAlarms(context);
        }
    }

    private void scheduleAlarms(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("schedule_prefs", Context.MODE_PRIVATE);
        int lectureCount = prefs.getInt("lecture_count", 1);
        String startTime = prefs.getString("start_time", "9:00 AM");
        int duration = prefs.getInt("duration", 50);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Parse start time
        String[] timeParts = startTime.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1].split(" ")[0]);
        boolean isPM = startTime.contains("PM");

        if (isPM && hour < 12) hour += 12;
        if (!isPM && hour == 12) hour = 0;

        // Schedule alarms for each lecture
        for (int i = 0; i < lectureCount; i++) {
            scheduleLectureAlarm(context, alarmManager, hour, minute, duration, i);
            minute += duration; // Add duration for next lecture
        }
    }

    private void scheduleLectureAlarm(Context context, AlarmManager alarmManager,
                                      int hour, int minute, int duration, int lectureNum) {
        // Silent mode intent (start of lecture)
        Intent silentIntent = new Intent(context, ActionReceiver.class);
        silentIntent.putExtra("action", SystemControl.ACTION_SILENT);
        PendingIntent silentPending = PendingIntent.getBroadcast(
                context,
                lectureNum * 2,
                silentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Normal mode intent (end of lecture)
        Intent normalIntent = new Intent(context, ActionReceiver.class);
        normalIntent.putExtra("action", SystemControl.ACTION_NORMAL);
        PendingIntent normalPending = PendingIntent.getBroadcast(
                context,
                lectureNum * 2 + 1,
                normalIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Set up calendar for silent time (lecture start)
        Calendar silentTime = Calendar.getInstance();
        silentTime.set(Calendar.HOUR_OF_DAY, hour);
        silentTime.set(Calendar.MINUTE, minute);
        silentTime.set(Calendar.SECOND, 0);

        // Set up calendar for normal time (lecture end)
        Calendar normalTime = (Calendar) silentTime.clone();
        normalTime.add(Calendar.MINUTE, duration);

        // Schedule alarms
        if (alarmManager != null) {
            // Set to silent at lecture start
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    silentTime.getTimeInMillis(),
                    silentPending);

            // Set back to normal at lecture end
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    normalTime.getTimeInMillis(),
                    normalPending);

            Log.d(TAG, "Scheduled lecture " + lectureNum +
                    " from " + hour + ":" + minute + " for " + duration + " minutes");
        }
    }
}