package com.iuxoa.irshieldx;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.Manifest;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private boolean isLoggedIn = false;
    private boolean isConnecting = true;

    private Button turnOnLed, turnOffLed;
    private EditText passwordInput, delayInput;
    private Button connectButton, sendPassword, turnOn, turnOff, setDelay, logout;
    private TextView statusText;

    private final String ESP32_DEVICE_NAME = "ESP32_IR_Blocker";
    private final UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        turnOnLed = findViewById(R.id.turnOnLed);
        turnOffLed = findViewById(R.id.turnOffLed);
        passwordInput = findViewById(R.id.passwordInput);
        delayInput = findViewById(R.id.delayInput);
        connectButton = findViewById(R.id.connectButton);
        sendPassword = findViewById(R.id.sendPassword);
        turnOn = findViewById(R.id.turnOn);
        turnOff = findViewById(R.id.turnOff);
        setDelay = findViewById(R.id.setDelay);
        logout = findViewById(R.id.logout);
        statusText = findViewById(R.id.statusText);

        setButtonStates(false);

        connectButton.setOnClickListener(view -> connectToESP32());

        sendPassword.setOnClickListener(view -> {
            String password = passwordInput.getText().toString().trim();
            if (password.isEmpty()) {
                statusText.setText("Enter password!");
                return;
            }
            sendCommand("LOGIN " + password);
            passwordInput.setText(""); // Clear password after sending
        });

        turnOn.setOnClickListener(view -> {
            if (isLoggedIn) sendCommand("ON");
            else statusText.setText("Login required");
        });

        turnOff.setOnClickListener(view -> {
            if (isLoggedIn) sendCommand("OFF");
            else statusText.setText("Login required");
        });

        setDelay.setOnClickListener(view -> {
            if (!isLoggedIn) {
                statusText.setText("Login required");
                return;
            }
            String delayStr = delayInput.getText().toString().trim();
            if (!delayStr.matches("\\d+")) {
                statusText.setText("Invalid delay input!");
                return;
            }
            sendCommand("SET_DELAY " + delayStr);
        });

        turnOnLed.setOnClickListener(view -> {
            if (isLoggedIn) sendCommand("IR_ON");
            else statusText.setText("Login required");
        });

        turnOffLed.setOnClickListener(view -> {
            if (isLoggedIn) sendCommand("IR_OFF");
            else statusText.setText("Login required");
        });

        logout.setOnClickListener(view -> {
            sendCommand("LOGOUT");
            isLoggedIn = false;
            setButtonStates(false);
            passwordInput.setEnabled(true);
            sendPassword.setEnabled(true);
            statusText.setText("Logged Out");
        });

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
        connectButton.setEnabled(false); // Disable during connection
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
                        try {
                            bluetoothSocket = device.createRfcommSocketToServiceRecord(SERIAL_UUID);
                            bluetoothAdapter.cancelDiscovery();
                            bluetoothSocket.connect();

                            outputStream = bluetoothSocket.getOutputStream();
                            inputStream = bluetoothSocket.getInputStream();

                            runOnUiThread(() -> {
                                statusText.setText("Connected to ESP32!");
                                connectButton.setEnabled(false);
                            });
                            listenForResponses();
                            return;

                        } catch (IOException e) {
                            runOnUiThread(() -> statusText.setText("Connection failed, retrying..."));
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException ie) {
                                ie.printStackTrace();
                            }
                        }
                    }
                }

                if (!deviceFound) {
                    runOnUiThread(() -> {
                        statusText.setText("ESP32 not found. Pair it first.");
                        connectButton.setEnabled(true);
                    });
                    break;
                }
            }
        }).start();
    }

    private void sendCommand(String command) {
        if (outputStream != null) {
            try {
                outputStream.write((command + "\n").getBytes());
                outputStream.flush();
            } catch (IOException e) {
                runOnUiThread(() -> statusText.setText("Failed to send command!"));
            }
        } else {
            runOnUiThread(() -> statusText.setText("Not connected to ESP32!"));
        }
    }

    private void setButtonStates(boolean enabled) {
        turnOn.setEnabled(enabled);
        turnOff.setEnabled(enabled);
        setDelay.setEnabled(enabled);
        turnOnLed.setEnabled(enabled);
        turnOffLed.setEnabled(enabled);
        logout.setEnabled(enabled);
    }

    private void listenForResponses() {
        new Thread(() -> {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    if ((bytes = inputStream.read(buffer)) > 0) {
                        String response = new String(buffer, 0, bytes).trim();
                        runOnUiThread(() -> {
                            switch (response.toUpperCase()) {
                                case "LOGIN_SUCCESS":
                                    isLoggedIn = true;
                                    setButtonStates(true);
                                    passwordInput.setEnabled(false);
                                    sendPassword.setEnabled(false);
                                    statusText.setText("Login Successful");
                                    break;
                                case "LOGIN_FAIL":
                                    isLoggedIn = false;
                                    setButtonStates(false);
                                    statusText.setText("Wrong Password");
                                    break;
                                case "LOGOUT_SUCCESS":
                                    isLoggedIn = false;
                                    setButtonStates(false);
                                    statusText.setText("Logged Out");
                                    break;
                                default:
                                    statusText.setText("Response: " + response);
                            }
                        });
                    }
                } catch (IOException e) {
                    runOnUiThread(() -> {
                        statusText.setText("Disconnected");
                        connectButton.setEnabled(true);
                    });
                    break;
                }
            }
        }).start();
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
