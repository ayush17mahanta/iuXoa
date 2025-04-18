package com.iuxoa.aerocommand;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import soup.neumorphism.NeumorphButton;

public class MainActivity extends AppCompatActivity {

    // UI Components
    private TextView statusTextView, batteryText, pressureText, tempText, humText, gpsText, altitudeText;
    private View loon1;
    private NeumorphButton openGraphDialogButton;
    private GraphDialogFragment graphDialog;
    // Bluetooth Components
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean isConnected = false;

    // Reconnection Handler
    private boolean shouldReconnect = true;
    private final Handler reconnectHandler = new Handler();
    private static final long RECONNECT_INTERVAL = 3000;

    // Constants
    private static final int VOICE_REQUEST_CODE = 100;
    private static final String ESP32_MAC = "3C:8A:1F:50:B3:26";
    private static final UUID UUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        checkPermissions();
        connectBluetooth();

        // Set up graph dialog button
        openGraphDialogButton = findViewById(R.id.openGraphDialogButton);
        openGraphDialogButton.setOnClickListener(v -> showGraphDialog());
    }

    private void showGraphDialog() {
        graphDialog = new GraphDialogFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        graphDialog.show(fragmentManager, "GraphDialog");
    }



    private void initializeViews() {
        statusTextView = findViewById(R.id.statusTextView);
        batteryText = findViewById(R.id.batteryLevelText);
        pressureText = findViewById(R.id.pressureText);
        tempText = findViewById(R.id.temperatureText);
        humText = findViewById(R.id.humidityText);
        gpsText = findViewById(R.id.gpsText);
        altitudeText = findViewById(R.id.altitudeText);
        loon1 = findViewById(R.id.loon1);

        findViewById(R.id.openButton).setOnClickListener(v -> {
            sendCommand("O");
            fadeOut(loon1);
        });

        findViewById(R.id.closeButton).setOnClickListener(v -> {
            sendCommand("C");
            fadeIn(loon1);
        });

        findViewById(R.id.voiceCommandButton).setOnClickListener(v -> startVoiceRecognition());
    }

    private void checkPermissions() {
        ArrayList<String> requiredPermissions = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.RECORD_AUDIO);
        }

        if (!requiredPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    requiredPermissions.toArray(new String[0]), 1);
        }
    }

    private void connectBluetooth() {
        new Thread(() -> {
            try {
                BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(ESP32_MAC);
                bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID);
                bluetoothSocket.connect();
                inputStream = bluetoothSocket.getInputStream();
                outputStream = bluetoothSocket.getOutputStream();
                isConnected = true;

                runOnUiThread(() -> {
                    statusTextView.setText("Connected");
                    startListening();
                    startReconnectLoop();
                });
            } catch (Exception e) {
                runOnUiThread(() -> statusTextView.setText("Connection Failed"));
            }
        }).start();
    }

    private void startReconnectLoop() {
        reconnectHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isConnected && shouldReconnect) {
                    connectBluetooth();
                }
                reconnectHandler.postDelayed(this, RECONNECT_INTERVAL);
            }
        }, RECONNECT_INTERVAL);
    }

    private void startListening() {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    parseData(line);
                }
            } catch (Exception e) {
                runOnUiThread(this::disconnect);
            }
        }).start();
    }

    private void parseData(String data) {
        runOnUiThread(() -> {
            if (data.startsWith("BATTERY:")) {
                batteryText.setText("\uD83D\uDD0BBattery: " + data.replace("BATTERY:", "") + "V");
            } else if (data.startsWith("TEMP:")) {
                tempText.setText("\uD83C\uDF21Temperature: " + data.replace("TEMP:", "") + "°C");
            } else if (data.startsWith("HUMIDITY:")) {
                humText.setText("\uD83D\uDCA7Humidity: " + data.replace("HUMIDITY:", "") + "%");
            } else if (data.startsWith("PRESSURE:")) {
                pressureText.setText("\uD83E\uDDEDPressure: " + data.replace("PRESSURE:", "") + "hPa");
            } else if (data.startsWith("GPS:")) {
                gpsText.setText("\uD83D\uDCCDGPS: " + data.replace("GPS:", ""));
            } else if (data.startsWith("ALTITUDE:")) {
                altitudeText.setText("\uD83E\uDDEDAltitude: " + data.replace("ALTITUDE:", "") + "m");
            }


        });
    }


    private void sendCommand(String cmd) {
        if (isConnected) {
            try {
                outputStream.write((cmd + "\n").getBytes());
            } catch (IOException e) {
                disconnect();
            }
        }
    }

    private void startVoiceRecognition() {
        if (!isConnected) {
            Toast.makeText(this, "Connect Bluetooth First", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say 'Open' or 'Close'");

        try {
            startActivityForResult(intent, VOICE_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "Voice recognition not supported", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                String command = results.get(0).toLowerCase();
                if (command.contains("open")) {
                    sendCommand("O");
                    fadeOut(loon1);
                    Toast.makeText(this, "Opening Gripper", Toast.LENGTH_SHORT).show();
                } else if (command.contains("close")) {
                    sendCommand("C");
                    fadeIn(loon1);
                    Toast.makeText(this, "Closing Gripper", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void fadeOut(View view) {
        view.animate().alpha(0f).setDuration(500).start();
    }

    private void fadeIn(View view) {
        view.animate().alpha(1f).setDuration(500).start();
    }

    private void disconnect() {
        try {
            shouldReconnect = false;
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
            }
            isConnected = false;
            runOnUiThread(() -> statusTextView.setText("Disconnected"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
        reconnectHandler.removeCallbacksAndMessages(null);
    }
}