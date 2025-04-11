package com.iuxoa.irshieldx;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

        passwordInput = findViewById(R.id.passwordInput);
        delayInput = findViewById(R.id.delayInput);
        connectButton = findViewById(R.id.connectButton);
        sendPassword = findViewById(R.id.sendPassword);
        turnOn = findViewById(R.id.turnOn);
        turnOff = findViewById(R.id.turnOff);
        setDelay = findViewById(R.id.setDelay);
        logout = findViewById(R.id.logout);
        statusText = findViewById(R.id.statusText);

        connectButton.setOnClickListener(view -> connectToESP32());

        sendPassword.setOnClickListener(view -> {
            String password = passwordInput.getText().toString().trim();
            if (password.isEmpty()) {
                statusText.setText("Enter password!");
                return;
            }
            sendCommand("LOGIN " + password);
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

        logout.setOnClickListener(view -> {
            sendCommand("LOGOUT");
            isLoggedIn = false;
        });
    }

    private void connectToESP32() {
        new Thread(() -> {
            if (bluetoothAdapter == null) {
                runOnUiThread(() -> statusText.setText("Bluetooth not supported!"));
                return;
            }

            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(ESP32_DEVICE_NAME)) {
                    try {
                        bluetoothSocket = device.createRfcommSocketToServiceRecord(SERIAL_UUID);
                        bluetoothSocket.connect();
                        outputStream = bluetoothSocket.getOutputStream();
                        inputStream = bluetoothSocket.getInputStream();
                        runOnUiThread(() -> statusText.setText("Connected to ESP32!"));

                        listenForResponses(); // Start reading feedback from ESP32
                        return;
                    } catch (IOException e) {
                        runOnUiThread(() -> statusText.setText("Connection failed!"));
                        return;
                    }
                }
            }
            runOnUiThread(() -> statusText.setText("ESP32 not found!"));
        }).start();
    }

    private void sendCommand(String command) {
        if (outputStream != null) {
            try {
                outputStream.write((command + "\n").getBytes());
            } catch (IOException e) {
                runOnUiThread(() -> statusText.setText("Failed to send command!"));
            }
        } else {
            runOnUiThread(() -> statusText.setText("Not connected to ESP32!"));
        }
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
                            if (response.equalsIgnoreCase("LOGIN_SUCCESS")) {
                                isLoggedIn = true;
                                statusText.setText("Login Successful");
                            } else if (response.equalsIgnoreCase("LOGIN_FAIL")) {
                                isLoggedIn = false;
                                statusText.setText("Wrong Password");
                            } else if (response.equalsIgnoreCase("LOGOUT_SUCCESS")) {
                                isLoggedIn = false;
                                statusText.setText("Logged Out");
                            } else {
                                statusText.setText("ESP32: " + response);
                            }
                        });
                    }
                } catch (IOException e) {
                    runOnUiThread(() -> statusText.setText("Disconnected"));
                    break;
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (outputStream != null) outputStream.close();
            if (inputStream != null) inputStream.close();
            if (bluetoothSocket != null) bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
