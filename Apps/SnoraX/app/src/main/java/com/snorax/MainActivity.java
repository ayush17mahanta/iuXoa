package com.snorax;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphFloatingActionButton;

public class MainActivity extends AppCompatActivity {

    // UI Elements
    private TableLayout tblWeeklySchedule;
    private Button btnLectureCount, btnStartTime, btnDuration, btnAnalytics, btnSettings, btnFilters, btnProfiles, btnFeedback;
    private NeumorphButton btnEmergencySettings;
    private NeumorphFloatingActionButton fabBell;
    private TextView tvCurrentStatus, tvNextStatus;

    // Preferences and State
    private int lectureCount = 1;
    private String startTime = "9:00 AM";
    private int duration = 50;
    private List<Button> lectureButtons = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private SharedPreferences analyticsPreferences;
    private static final int NOTIFICATION_LISTENER_REQUEST_CODE = 1001;
    private static final int PERMISSION_REQUEST_CODE = 2001;

    private final String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializeViews();
        checkPermissions();
        checkNotificationPolicyAccess();
        initializePreferences();
        applyTheme();
        loadPreferences();
        updateTable();
        updateUIFromSystemState();
        setupClickListeners();
        String initialState = sharedPreferences.getString("last_mode", "Normal");
        updateStatusDisplay(initialState);

        // Show tutorial on first launch
        showTutorialIfNeeded();


    }

    private void showTutorialIfNeeded() {
        boolean tutorialShown = sharedPreferences.getBoolean("tutorial_shown", false);
        if (!tutorialShown) {
            new TutorialHelper(this)
                    .setOnComplete(() -> {
                        sharedPreferences.edit().putBoolean("tutorial_shown", true).apply();
                        Toast.makeText(this, "Tutorial completed!", Toast.LENGTH_SHORT).show();
                    })
                    .startTutorial();
        }
    }

    public void resetTutorial() {
        sharedPreferences.edit().putBoolean("tutorial_shown", false).apply();
        showTutorialIfNeeded();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("reset_tutorial", false)) {
            resetTutorial();
        }
    }

    private void initializeViews() {
        tblWeeklySchedule = findViewById(R.id.tblWeeklySchedule);
        btnLectureCount = findViewById(R.id.btnLectureCount);
        btnStartTime = findViewById(R.id.btnStartTime);
        btnDuration = findViewById(R.id.btnDuration);
        fabBell = findViewById(R.id.fabBell);
        btnEmergencySettings = findViewById(R.id.btnEmergencySettings);
        btnAnalytics = findViewById(R.id.btnAnalytics);
        btnSettings = findViewById(R.id.btnSettings);
        btnFilters = findViewById(R.id.btnFilters);
        btnProfiles = findViewById(R.id.btnProfiles);
        btnFeedback = findViewById(R.id.btnFeedback);
        tvCurrentStatus = findViewById(R.id.tvCurrentStatus);
        tvNextStatus = findViewById(R.id.tvNextStatus);
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Some features may not work without permissions", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateStatusDisplay(String currentState) {
        String nextState;
        int currentColor;

        switch (currentState) {
            case "Normal":
                nextState = "Vibrate";
                currentColor = ContextCompat.getColor(this, R.color.normal_mode);
                break;
            case "Vibrate":
                nextState = "Silent";
                currentColor = ContextCompat.getColor(this, R.color.vibrate_mode);
                break;
            case "Silent":
                nextState = "Normal";
                currentColor = ContextCompat.getColor(this, R.color.silent_mode);
                break;
            default:
                return;
        }

        tvCurrentStatus.setText("Current Mode: " + currentState);
        tvCurrentStatus.setTextColor(currentColor);
        tvNextStatus.setText("Next Click: " + nextState);
        updateSystemStatus(currentState);
    }

    private void updateSystemStatus(String state) {
        switch (state) {
            case "Normal":
                SystemControl.setRingerMode(this, SystemControl.ACTION_NORMAL);
                break;
            case "Vibrate":
                SystemControl.setRingerMode(this, SystemControl.ACTION_VIBRATE);
                break;
            case "Silent":
                SystemControl.setRingerMode(this, SystemControl.ACTION_SILENT);
                break;
        }
    }

    private void initializePreferences() {
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        analyticsPreferences = getSharedPreferences("analytics_prefs", MODE_PRIVATE);
    }

    private void applyTheme() {
        String theme = sharedPreferences.getString("theme", "light");
        switch (theme) {
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "system":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    private void setupClickListeners() {
        btnSettings.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));
        btnFilters.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, FiltersActivity.class)));
        btnFeedback.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, FeedbackActivity.class)));
        btnProfiles.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfilesActivity.class)));
        btnAnalytics.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AnalyticsActivity.class)));

        fabBell.setOnClickListener(v -> {
            if (!sharedPreferences.getBoolean("fab_tip_shown", false)) {
                TapTargetView.showFor(this,
                        TapTarget.forView(fabBell, "Tip", "You can mute, unmute, or set vibrate mode for all lectures here.")
                                .outerCircleAlpha(0.7f)
                                .titleTextColor(android.R.color.white)
                                .descriptionTextColor(android.R.color.white)
                                .textTypeface(Typeface.SANS_SERIF)
                                .drawShadow(true)
                                .cancelable(true)
                                .tintTarget(true)
                                .transparentTarget(true),
                        new TapTargetView.Listener() {
                            @Override
                            public void onTargetClick(TapTargetView view) {
                                super.onTargetClick(view);
                                sharedPreferences.edit().putBoolean("fab_tip_shown", true).apply();
                                onFabBellClick();
                            }
                        });
            } else {
                onFabBellClick();
            }
        });

        btnEmergencySettings.setOnClickListener(v -> openEmergencySettings());
        btnLectureCount.setOnClickListener(v -> openLecturePicker());
        btnStartTime.setOnClickListener(v -> openTimePicker());
        btnDuration.setOnClickListener(v -> openDurationPicker());
    }

    private void openEmergencySettings() {
        if (!isNotificationListenerEnabled()) {
            new AlertDialog.Builder(this)
                    .setTitle("Enable Notification Access")
                    .setMessage("Please enable notification access for SnoraX to allow emergency bypass.")
                    .setPositiveButton("Enable", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                        startActivityForResult(intent, NOTIFICATION_LISTENER_REQUEST_CODE);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            showEmergencySettingsDialog();
        }
    }

    private boolean isNotificationListenerEnabled() {
        String enabledListeners = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        return enabledListeners != null && enabledListeners.contains(getPackageName());
    }

    private void showEmergencySettingsDialog() {
        List<String> appList = getInstalledApps();
        String[] apps = appList.toArray(new String[0]);

        new AlertDialog.Builder(this)
                .setTitle("Select Emergency App")
                .setItems(apps, (dialog, which) -> {
                    String selectedApp = apps[which];
                    saveEmergencyApp(selectedApp);
                    Toast.makeText(this, "Emergency app set to: " + selectedApp, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private List<String> getInstalledApps() {
        List<String> appList = new ArrayList<>();
        appList.add("WhatsApp");
        appList.add("Messenger");
        appList.add("Phone");
        return appList;
    }

    private void saveEmergencyApp(String appName) {
        SharedPreferences.Editor editor = analyticsPreferences.edit();
        editor.putString("emergency_app", appName);
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NOTIFICATION_LISTENER_REQUEST_CODE) {
            if (isNotificationListenerEnabled()) {
                showEmergencySettingsDialog();
            } else {
                Toast.makeText(this, "Notification access not enabled.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void toggleLectureState() {
        if (hasNotificationPolicyAccess()) {
            String currentMode = SystemControl.getCurrentRingerModeString(this);
            String nextMode = getNextMode(currentMode);

            switch (nextMode) {
                case "Normal":
                    SystemControl.setRingerMode(this, SystemControl.ACTION_NORMAL);
                    break;
                case "Vibrate":
                    SystemControl.setRingerMode(this, SystemControl.ACTION_VIBRATE);
                    break;
                case "Silent":
                    SystemControl.setRingerMode(this, SystemControl.ACTION_SILENT);
                    break;
            }
        } else {
            showPolicyAccessDialog();
        }
    }

    private boolean hasNotificationPolicyAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            return nm.isNotificationPolicyAccessGranted();
        }
        return true;
    }

    private void showPolicyAccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("This app needs notification policy access to change ringer modes.")
                .setPositiveButton("Open Settings", (d, w) -> {
                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateUIFromSystemState() {
        String currentMode = SystemControl.getCurrentRingerModeString(this);

        runOnUiThread(() -> {
            tvCurrentStatus.setText("Current Mode: " + currentMode);
            tvNextStatus.setText("Next Click: " + getNextMode(currentMode));

            for (Button button : lectureButtons) {
                updateButtonAppearance(button, currentMode);
            }
        });
    }

    private void checkNotificationPolicyAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (!notificationManager.isNotificationPolicyAccessGranted()) {
                Toast.makeText(this,
                        "Please grant notification policy access in settings",
                        Toast.LENGTH_LONG).show();

                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            }
        }
    }

    private String getNextMode(String currentMode) {
        switch (currentMode) {
            case "Normal": return "Vibrate";
            case "Vibrate": return "Silent";
            case "Silent": return "Normal";
            default: return "Normal";
        }
    }

    private final BroadcastReceiver ringerModeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.RINGER_MODE_CHANGED_ACTION.equals(intent.getAction())) {
                updateUIFromSystemState();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(ringerModeReceiver, new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION));
        updateUIFromSystemState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(ringerModeReceiver);
    }

    private void openTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            startTime = String.format(Locale.getDefault(), "%02d:%02d %s",
                    hourOfDay % 12 == 0 ? 12 : hourOfDay % 12,
                    minute1,
                    hourOfDay < 12 ? "AM" : "PM");
            btnStartTime.setText("Start Time: " + startTime);
            savePreferences();
        }, hour, minute, false);

        timePickerDialog.setTitle("Select Start Time");
        timePickerDialog.show();
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String currentMode = tvCurrentStatus.getText().toString().replace("Current Mode: ", "");
        editor.putString("last_mode", currentMode);
        editor.putInt("lecture_count", lectureCount);
        editor.putString("start_time", startTime);
        editor.putInt("duration", duration);

        for (int i = 0; i < daysOfWeek.length; i++) {
            for (int j = 0; j < lectureCount; j++) {
                String key = daysOfWeek[i] + "_lecture_" + j;
                editor.putString(key, lectureButtons.get(i * lectureCount + j).getText().toString());
            }
        }

        editor.apply();
    }

    private void openDurationPicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMinValue(30);
        numberPicker.setMaxValue(180);
        numberPicker.setValue(duration);
        builder.setTitle("Select Duration (minutes)");
        builder.setView(numberPicker);
        builder.setPositiveButton("OK", (dialog, which) -> {
            duration = numberPicker.getValue();
            btnDuration.setText("Duration: " + duration + " mins");
            savePreferences();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void onFabBellClick() {
        PopupMenu popupMenu = new PopupMenu(this, fabBell);
        popupMenu.getMenuInflater().inflate(R.menu.menu_fab_bell, popupMenu.getMenu());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_normal_all) {
                updateAllLectures("Normal");
                return true;
            } else if (itemId == R.id.menu_vibrate_all) {
                updateAllLectures("Vibrate");
                return true;
            } else if (itemId == R.id.menu_silent_all) {
                updateAllLectures("Silent");
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void updateAllLectures(String targetMode) {
        switch (targetMode) {
            case "Normal":
                SystemControl.setRingerMode(this, SystemControl.ACTION_NORMAL);
                break;
            case "Vibrate":
                SystemControl.setRingerMode(this, SystemControl.ACTION_VIBRATE);
                break;
            case "Silent":
                SystemControl.setRingerMode(this, SystemControl.ACTION_SILENT);
                break;
        }

        Toast.makeText(this, "All set to " + targetMode, Toast.LENGTH_SHORT).show();
        savePreferences();
    }

    private void openLecturePicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(8);
        numberPicker.setValue(lectureCount);
        builder.setView(numberPicker);
        builder.setPositiveButton("OK", (dialog, which) -> {
            lectureCount = numberPicker.getValue();
            updateTable();
            savePreferences();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void loadPreferences() {
        lectureCount = sharedPreferences.getInt("lecture_count", 1);
        startTime = sharedPreferences.getString("start_time", "9:00 AM");
        duration = sharedPreferences.getInt("duration", 50);

        btnLectureCount.setText("Lecture Count: " + lectureCount);
        btnStartTime.setText("Start Time: " + startTime);
        btnDuration.setText("Duration: " + duration + " mins");

        if (!lectureButtons.isEmpty()) {
            for (int i = 0; i < daysOfWeek.length; i++) {
                for (int j = 0; j < lectureCount; j++) {
                    String key = daysOfWeek[i] + "_lecture_" + j;
                    String state = sharedPreferences.getString(key, "Vibrate");
                    lectureButtons.get(i * lectureCount + j).setText(state);
                }
            }
        }
    }

    private void updateTable() {
        tblWeeklySchedule.removeAllViews();
        lectureButtons.clear();

        // Create header row
        TableRow headerRow = new TableRow(this);
        headerRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));

        TextView emptyHeader = new TextView(this);
        emptyHeader.setText("");
        emptyHeader.setPadding(16, 16, 16, 16);
        emptyHeader.setGravity(Gravity.CENTER);
        headerRow.addView(emptyHeader);

        for (int i = 0; i < lectureCount; i++) {
            TextView lectureHeader = new TextView(this);
            lectureHeader.setText("Lec " + (i + 1));
            lectureHeader.setPadding(16, 16, 16, 16);
            lectureHeader.setGravity(Gravity.CENTER);
            headerRow.addView(lectureHeader);
        }
        tblWeeklySchedule.addView(headerRow);

        String currentSystemMode = SystemControl.getCurrentRingerModeString(this);
        String nextSystemMode = getNextMode(currentSystemMode);

        for (String day : daysOfWeek) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            ));

            TextView dayTextView = new TextView(this);
            dayTextView.setText(day);
            dayTextView.setPadding(16, 16, 16, 16);
            dayTextView.setGravity(Gravity.CENTER);
            row.addView(dayTextView);

            for (int i = 0; i < lectureCount; i++) {
                Button lectureButton = new Button(this);

                String key = day + "_lecture_" + i;
                String initialState = sharedPreferences.getString(key, nextSystemMode);
                lectureButton.setText(initialState);

                updateButtonAppearance(lectureButton, currentSystemMode);

                lectureButton.setPadding(16, 16, 16, 16);
                lectureButton.setOnClickListener(v -> toggleLectureState());

                lectureButtons.add(lectureButton);
                row.addView(lectureButton);
            }
            tblWeeklySchedule.addView(row);
        }
    }

    private void updateButtonAppearance(Button button, String currentMode) {
        int color;
        switch (currentMode) {
            case "Normal":
                color = ContextCompat.getColor(this, R.color.normal_mode);
                break;
            case "Vibrate":
                color = ContextCompat.getColor(this, R.color.vibrate_mode);
                break;
            case "Silent":
                color = ContextCompat.getColor(this, R.color.silent_mode);
                break;
            default:
                color = ContextCompat.getColor(this, R.color.normal_mode);
        }

        button.setBackgroundTintList(ColorStateList.valueOf(color));
        button.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        button.setText(getNextMode(currentMode));
    }
}