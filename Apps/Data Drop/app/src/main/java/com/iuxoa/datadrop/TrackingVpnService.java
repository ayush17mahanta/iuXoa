package com.iuxoa.datadrop;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileInputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.concurrent.Executors;

public class TrackingVpnService extends VpnService {
    private ParcelFileDescriptor vpnInterface;
    private boolean isRunning = false;
    private static final String TAG = "TrackingVpnService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setupVpn();
        startSniffing();
        return START_STICKY;
    }

    private void setupVpn() {
        Builder builder = new Builder();
        builder.addAddress("10.0.0.2", 24);
        builder.addDnsServer("8.8.8.8");
        builder.addRoute("0.0.0.0", 0);
        vpnInterface = builder.setSession("DataDropTracking").establish();
        Log.d(TAG, "VPN Interface established");
    }

    private void startSniffing() {
        isRunning = true;
        Executors.newSingleThreadExecutor().execute(() -> {
            FileInputStream in = new FileInputStream(vpnInterface.getFileDescriptor());
            ByteBuffer buffer = ByteBuffer.allocate(32767);

            while (isRunning) {
                try {
                    int length = in.read(buffer.array());
                    if (length > 0) {
                        String domain = DNSParser.parse(buffer.array(), length);
                        if (domain != null) {
                            Log.d(TAG, "Domain visited: " + domain);
                            saveToFirestore(domain);
                            saveToLocalDb(domain);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error reading VPN stream", e);
                }
            }
        });
    }

    private void saveToFirestore(String domain) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = auth.getCurrentUser().getUid();

        BrowsingEntry entry = new BrowsingEntry(domain, new Date().getTime());
        db.collection("users")
                .document(uid)
                .collection("browsing_logs")
                .add(entry);
    }

    private void saveToLocalDb(String domain) {
        BrowsingEntry entry = new BrowsingEntry(domain, new Date().getTime());
        AppDatabase.getInstance(getApplicationContext()).browsingDao().insert(entry);
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        try {
            vpnInterface.close();
        } catch (Exception ignored) {}
        super.onDestroy();
    }
}
