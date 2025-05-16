package com.iuxoa.datadrop;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.iuxoa.datadrop.R;
import com.iuxoa.datadrop.TrackingVpnService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView txtBalance;
    private SwitchMaterial switchBrowsing, switchLocation, switchDevice, switchSurveys, switchTracking;
    private Button btnViewLogs;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DocumentReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI init
        txtBalance = findViewById(R.id.txtBalance);
        switchBrowsing = findViewById(R.id.switchBrowsing);
        switchLocation = findViewById(R.id.switchLocation);
        switchDevice = findViewById(R.id.switchDevice);
        switchSurveys = findViewById(R.id.switchSurveys);
        switchTracking = findViewById(R.id.switchTracking);
        btnViewLogs = findViewById(R.id.btnViewLogs);

        // Firebase setup
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        switchDevice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            userRef.update("share_device_usage", isChecked);

            if (isChecked) {
                if (!hasUsageStatsPermission()) {
                    requestUsageStatsPermission();
                } else {
                    logTopUsedApps();
                }
            }
        });

        String uid = auth.getCurrentUser().getUid();
        userRef = db.collection("users").document(uid);

        // FCM token
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    Log.d("FCM", "Token: " + token);
                });

        // Load & setup
        loadUserData();
        setupSwitchListeners();

        // View logs
        btnViewLogs.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, com.iuxoa.datadrop.LogsActivity.class));
        });
    }

    private void loadUserData() {
        userRef.get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                double balance = doc.getDouble("balance") != null ? doc.getDouble("balance") : 0.0;
                txtBalance.setText("Balance: $" + String.format("%.2f", balance));

                switchBrowsing.setChecked(Boolean.TRUE.equals(doc.getBoolean("share_browsing")));
                switchLocation.setChecked(Boolean.TRUE.equals(doc.getBoolean("share_location")));
                switchDevice.setChecked(Boolean.TRUE.equals(doc.getBoolean("share_device_usage")));
                switchSurveys.setChecked(Boolean.TRUE.equals(doc.getBoolean("participate_surveys")));
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("email", auth.getCurrentUser().getEmail());
                data.put("balance", 0.0);
                data.put("share_browsing", false);
                data.put("share_location", false);
                data.put("share_device_usage", false);
                data.put("participate_surveys", false);
                data.put("created_at", com.google.firebase.Timestamp.now());

                userRef.set(data);
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
        );
    }

    private void setupSwitchListeners() {
        switchBrowsing.setOnCheckedChangeListener((buttonView, isChecked) ->
                userRef.update("share_browsing", isChecked));

        switchLocation.setOnCheckedChangeListener((buttonView, isChecked) ->
                userRef.update("share_location", isChecked));

        switchDevice.setOnCheckedChangeListener((buttonView, isChecked) ->
                userRef.update("share_device_usage", isChecked));

        switchSurveys.setOnCheckedChangeListener((buttonView, isChecked) ->
                userRef.update("participate_surveys", isChecked));

        switchTracking.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Intent intent = VpnService.prepare(MainActivity.this);
                if (intent != null) {
                    startActivityForResult(intent, 0);
                } else {
                    onActivityResult(0, RESULT_OK, null);
                }
            } else {
                stopService(new Intent(MainActivity.this, TrackingVpnService.class));
                Toast.makeText(this, "Tracking stopped", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (result == RESULT_OK) {
            Intent intent = new Intent(this, TrackingVpnService.class);
            startService(intent);
            Toast.makeText(this, "Tracking started", Toast.LENGTH_SHORT).show();
        } else {
            switchTracking.setChecked(false);
        }
    }

    private boolean hasUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private void requestUsageStatsPermission() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void logTopUsedApps() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

        long time = System.currentTimeMillis();
        List<UsageStats> stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, time - 1000 * 3600 * 24, time);

        if (stats == null || stats.isEmpty()) {
            Log.d("AppUsage", "No usage stats available.");
            return;
        }

        // Sort by last time used
        Collections.sort(stats, (a, b) -> Long.compare(b.getLastTimeUsed(), a.getLastTimeUsed()));

        PackageManager pm = getPackageManager();
        for (int i = 0; i < Math.min(5, stats.size()); i++) {
            UsageStats usageStats = stats.get(i);
            try {
                ApplicationInfo appInfo = pm.getApplicationInfo(usageStats.getPackageName(), 0);
                String appName = pm.getApplicationLabel(appInfo).toString();
                long minutes = usageStats.getTotalTimeInForeground() / 1000 / 60;

                Log.d("AppUsage", appName + " used for " + minutes + " mins today");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
