package com.iuxoa.irshieldx;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.Manifest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import soup.neumorphism.NeumorphImageButton;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private boolean isLoggedIn = false;
    private boolean isConnecting = true;

    private int currentDelay = 15; // Default 15 seconds
    private static final String DELAY_PREF_KEY = "ir_delay_pref";
    // UI Components
    private TextView connectionStatus, logStatus;
    private NeumorphImageButton toggleIR, toggleLED;
    private SeekBar delaySeekBar;
    private TextView delayLabel;
    private boolean isIROn = false, isLEDOn = false;
    private EditText passwordInput;
    private Button connectButton, loginLogoutButton;
    private TextView statusText;
    private ImageView togglePasswordVisibility;

    // Constants
    private final String ESP32_DEVICE_NAME = "ESP32_IR_Blocker";
    private final UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Bluetooth Adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Initialize all UI components
        initializeViews();

        // Set initial button states
        setButtonStates(false);

        // Set up listeners
        setupListeners();

        // Check and request permissions
        checkPermissions();
    }

    private void initializeViews() {
        toggleIR = findViewById(R.id.toggleIR);
        toggleLED = findViewById(R.id.toggleLED);
        delaySeekBar = findViewById(R.id.delaySeekBar);
        delayLabel = findViewById(R.id.delayLabel);
        connectionStatus = findViewById(R.id.connectionStatus);
        logStatus = findViewById(R.id.logStatus);
        passwordInput = findViewById(R.id.passwordInput);
        connectButton = findViewById(R.id.connectButton);
        loginLogoutButton = findViewById(R.id.sendPassword);
        statusText = findViewById(R.id.statusText);
        togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);

        // Set initial password visibility state
        togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off);
        passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        toggleIR.setColorFilter(getResources().getColor(R.color.black));
        updateLoginButtonText();
    }

    private void setupListeners() {
        // Bluetooth connection button
        connectButton.setOnClickListener(view -> connectToESP32());

        // Delay seekbar
        delaySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                delayLabel.setText("Delay: " + progress + " sec");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isLoggedIn) {
                    int delayMs = seekBar.getProgress() * 1000;
                    sendCommand("SET_DELAY " + delayMs);
                } else {
                    statusText.setText("Login required");
                }
            }
        });

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        currentDelay = prefs.getInt(DELAY_PREF_KEY, 15);
        delaySeekBar.setProgress(currentDelay);
        delayLabel.setText("Delay: " + currentDelay + " sec");
        // IR toggle button
        toggleIR.setOnClickListener(view -> {
            if (!isLoggedIn) {
                statusText.setText("Login required");
                return;
            }
            isIROn = !isIROn;
            sendCommand(isIROn ? "IR_ON" : "IR_OFF");

            // Change image color
            int color = getResources().getColor(isIROn ? R.color.red : R.color.black);
            toggleIR.setColorFilter(color);
        });

        // LED toggle button
        toggleLED.setOnClickListener(view -> {
            if (!isLoggedIn) {
                statusText.setText("Login required");
                return;
            }
            isLEDOn = !isLEDOn;
            sendCommand(isLEDOn ? "ON" : "OFF");
            toggleLED.setImageResource(isLEDOn ? R.drawable.led_on : R.drawable.led_off);
        });

        // Password visibility toggle
        togglePasswordVisibility.setOnClickListener(view -> {
            if (passwordInput.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                // Show password
                passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility);
            } else {
                // Hide password
                passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off);
            }
            // Move cursor to end
            passwordInput.setSelection(passwordInput.getText().length());
        });

        // Login/Logout button
        loginLogoutButton.setOnClickListener(view -> {
            if (isLoggedIn) {
                // Logout action
                sendCommand("LOGOUT");
                isLoggedIn = false;
                setButtonStates(false);
                passwordInput.setEnabled(true);
                togglePasswordVisibility.setEnabled(true);
                statusText.setText("Logged Out");
                logStatus.setText("Logged Out");
                logStatus.setTextColor(getResources().getColor(R.color.red, null));
            } else {
                // Login action
                String password = passwordInput.getText().toString().trim();
                if (password.isEmpty()) {
                    statusText.setText("Enter password!");
                    return;
                }
                sendCommand("LOGIN " + password);
                passwordInput.setText("");
            }
            updateLoginButtonText();
        });

        // Enable/disable password visibility toggle based on connection state
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            togglePasswordVisibility.setEnabled(false);
        }
    }

    private void updateLoginButtonText() {
        if (loginLogoutButton != null) {
            loginLogoutButton.setText(isLoggedIn ? "Logout" : "Login");
        }
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
                return;
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void connectToESP32() {
        runOnUiThread(() -> {
            statusText.setText("Attempting to connect...");
            if (connectButton != null) {
                connectButton.setEnabled(false);
            }
        });

        isConnecting = true;

        new Thread(() -> {
            if (bluetoothAdapter == null) {
                runOnUiThread(() -> statusText.setText("Bluetooth not supported!"));
                return;
            }

            while (isConnecting && (bluetoothSocket == null || !bluetoothSocket.isConnected())) {
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                boolean deviceFound = false;

                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals(ESP32_DEVICE_NAME)) {
                        deviceFound = true;
                        runOnUiThread(() -> statusText.setText("Found device, connecting..."));

                        try {
                            bluetoothSocket = device.createRfcommSocketToServiceRecord(SERIAL_UUID);
                            bluetoothAdapter.cancelDiscovery();
                            bluetoothSocket.connect();

                            outputStream = bluetoothSocket.getOutputStream();
                            inputStream = bluetoothSocket.getInputStream();

                            runOnUiThread(() -> {
                                statusText.setText("Connected to " + ESP32_DEVICE_NAME);
                                if (connectionStatus != null) {
                                    connectionStatus.setText("Connected");
                                    connectionStatus.setTextColor(getResources().getColor(R.color.green, null));
                                }
                                if (connectButton != null) {
                                    connectButton.setEnabled(false);
                                }
                                togglePasswordVisibility.setEnabled(true);
                            });

                            listenForResponses();
                            return;

                        } catch (IOException e) {
                            runOnUiThread(() -> {
                                statusText.setText("Connection failed: " + e.getMessage() + ", retrying...");
                                if (connectionStatus != null) {
                                    connectionStatus.setText("Not Connected");
                                    connectionStatus.setTextColor(getResources().getColor(R.color.red, null));
                                }
                            });

                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException ie) {
                                runOnUiThread(() -> statusText.setText("Connection interrupted"));
                            }
                        }
                    }
                }

                if (!deviceFound) {
                    runOnUiThread(() -> {
                        statusText.setText(ESP32_DEVICE_NAME + " not found in paired devices");
                        if (connectionStatus != null) {
                            connectionStatus.setText("Not Connected");
                            connectionStatus.setTextColor(getResources().getColor(R.color.red, null));
                        }
                        if (connectButton != null) {
                            connectButton.setEnabled(true);
                        }
                    });
                    break;
                }
            }
        }).start();
    }

    private void sendCommand(String command) {
        runOnUiThread(() -> statusText.setText("Sending: " + command));

        if (outputStream != null) {
            try {
                outputStream.write((command + "\n").getBytes());
                outputStream.flush();
            } catch (IOException e) {
                runOnUiThread(() -> statusText.setText("Failed to send '" + command + "': " + e.getMessage()));
            }
        } else {
            runOnUiThread(() -> statusText.setText("Not connected - cannot send '" + command + "'"));
        }
    }

    private void setButtonStates(boolean enabled) {
        if (toggleIR != null) toggleIR.setEnabled(enabled);
        if (toggleLED != null) toggleLED.setEnabled(enabled);
        if (delaySeekBar != null) delaySeekBar.setEnabled(enabled);
        if (togglePasswordVisibility != null) togglePasswordVisibility.setEnabled(enabled && !isLoggedIn);
    }

    private void listenForResponses() {
        new Thread(() -> {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    if ((bytes = inputStream.read(buffer)) > 0) {
                        String response = new String(buffer, 0, bytes).trim();
                        Log.d("BTResponse", "Received: " + response);

                        runOnUiThread(() -> {
                            if (response == null || statusText == null) return;

                            // Always show the raw response first
                            statusText.setText("Response: " + response);

                            // Then handle specific responses
                            if (response.startsWith("DELAY_SET_TO")) {
                                try {
                                    int newDelay = Integer.parseInt(response.substring(12).trim());
                                    currentDelay = newDelay;
                                    delaySeekBar.setProgress(newDelay);
                                    delayLabel.setText("Delay: " + newDelay + " sec");
                                    getSharedPreferences("AppPrefs", MODE_PRIVATE).edit()
                                            .putInt("ir_delay", newDelay)
                                            .apply();
                                } catch (NumberFormatException e) {
                                    statusText.setText("Error parsing delay: " + response);
                                }
                            }
                            else if (response.startsWith("CURRENT_DELAY")) {
                                try {
                                    int currentDelay = Integer.parseInt(response.substring(13).trim());
                                    delaySeekBar.setProgress(currentDelay);
                                    delayLabel.setText("Delay: " + currentDelay + " sec");
                                    this.currentDelay = currentDelay;
                                } catch (NumberFormatException e) {
                                    statusText.setText("Error parsing current delay: " + response);
                                }
                            }
                            else switch (response.toUpperCase()) {
                                    case "LOGIN_SUCCESS":
                                        isLoggedIn = true;
                                        setButtonStates(true);
                                        passwordInput.setEnabled(false);
                                        logStatus.setText("Logged In");
                                        logStatus.setTextColor(getResources().getColor(R.color.green, null));
                                        sendCommand("GET_DELAY");
                                        break;

                                    case "LOGIN_FAIL":
                                        isLoggedIn = false;
                                        setButtonStates(false);
                                        logStatus.setText("Login Failed");
                                        logStatus.setTextColor(getResources().getColor(R.color.red, null));
                                        break;

                                    case "LOGOUT_SUCCESS":
                                        isLoggedIn = false;
                                        setButtonStates(false);
                                        logStatus.setText("Logged Out");
                                        logStatus.setTextColor(getResources().getColor(R.color.red, null));
                                        passwordInput.setEnabled(true);
                                        break;

                                    case "INVALID_DELAY":
                                        delaySeekBar.setProgress(currentDelay);
                                        break;

                                    // Add more cases as needed for other responses
                                }
                        });
                    }
                } catch (IOException e) {
                    runOnUiThread(() -> {
                        statusText.setText("Disconnected: " + e.getMessage());
                        connectionStatus.setText("Not Connected");
                        connectionStatus.setTextColor(getResources().getColor(R.color.red, null));
                        connectButton.setEnabled(true);
                        isLoggedIn = false;
                        setButtonStates(false);
                    });
                    break;
                }
            }
        }).start();
    }

    private void setupDelaySeekBar() {
        // Initialize with saved preference or default (15 seconds)
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        currentDelay = prefs.getInt("ir_delay", 15);
        delaySeekBar.setProgress(currentDelay);
        delayLabel.setText("Delay: " + currentDelay + " sec");

        delaySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentDelay = progress;
                delayLabel.setText("Delay: " + currentDelay + " sec");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed but required to implement
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isLoggedIn) {
                    if (currentDelay >= 1 && currentDelay <= 60) { // Validate range
                        sendCommand("SET_DELAY " + currentDelay);

                        // Save to SharedPreferences
                        getSharedPreferences("AppPrefs", MODE_PRIVATE).edit()
                                .putInt("ir_delay", currentDelay)
                                .apply();

                        // Visual feedback
                        Toast.makeText(MainActivity.this,
                                "Delay set to " + currentDelay + " seconds",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // Reset to last valid value if out of range
                        delaySeekBar.setProgress(15);
                        delayLabel.setText("Delay: 15 sec");
                        Toast.makeText(MainActivity.this,
                                "Delay must be between 1-60 seconds",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    statusText.setText("Login required to change delay");
                    // Revert to last saved value
                    delaySeekBar.setProgress(
                            getSharedPreferences("AppPrefs", MODE_PRIVATE)
                                    .getInt("ir_delay", 15)
                    );
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isConnecting = false;
        try {
            if (outputStream != null) outputStream.close();
            if (inputStream != null) inputStream.close();
            if (bluetoothSocket != null) bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}