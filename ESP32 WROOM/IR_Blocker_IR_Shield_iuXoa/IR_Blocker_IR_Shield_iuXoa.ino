#include "BluetoothSerial.h"
BluetoothSerial SerialBT;

#define NUM_LEDS 9
int ledPins[NUM_LEDS] = {25, 26, 32, 33, 23, 22, 21, 18, 19};  // IR LED pins
const int ledIndicator = 2;  // Power indicator LED
const int controlLed = 4;    // LED controlled via ON/OFF
int irDelay = 20;            // Delay in microseconds

bool floodActive = false;
bool countdownDone = false;
bool isLoggedIn = false;
bool previousClientState = false;
bool controlLedState = false;

const String correctPassword = "admin123";
String incomingCommand = "";

void setup() {
  Serial.begin(115200);
  SerialBT.begin("ESP32_IR_Blocker");
  Serial.println("Bluetooth started. Waiting for app to connect...");

  for (int i = 0; i < NUM_LEDS; i++) {
    pinMode(ledPins[i], OUTPUT);
    digitalWrite(ledPins[i], LOW);
  }

  pinMode(ledIndicator, OUTPUT);
  digitalWrite(ledIndicator, HIGH);  // Always ON

  pinMode(controlLed, OUTPUT);
  digitalWrite(controlLed, LOW);     // Start OFF

  delay(15000);  // 15-second startup delay
  floodActive = true;  // Start IR flooding after delay
  countdownDone = true;
  Serial.println("Startup delay complete. IR flooding started.");
}

void loop() {
  bool currentClientState = SerialBT.hasClient();
  if (currentClientState && !previousClientState) {
    Serial.println("App connected via Bluetooth.");
  }
  previousClientState = currentClientState;

  // Handle Bluetooth input
  while (SerialBT.available()) {
    char c = SerialBT.read();
    if (c == '\n') {
      processCommand(incomingCommand);
      incomingCommand = "";
    } else {
      incomingCommand += c;
    }
  }

  // IR Flooding Loop (active after 15s, regardless of login)
  if (countdownDone && floodActive) {
    floodIR();
  }
}

void processCommand(String cmd) {
  cmd.trim();
  Serial.print("Command: '");
  Serial.print(cmd);
  Serial.println("'");

  String cmdUpper = cmd;
  cmdUpper.toUpperCase();

  if (cmdUpper.startsWith("LOGIN ")) {
    String pass = cmd.substring(6);
    pass.trim();
    if (pass == correctPassword) {
      isLoggedIn = true;
      SerialBT.println("LOGIN_SUCCESS");
      Serial.println("Login successful.");
    } else {
      isLoggedIn = false;
      SerialBT.println("LOGIN_FAIL");
      Serial.println("Login failed.");
    }

  } else if (cmdUpper == "LOGOUT") {
    isLoggedIn = false;
    SerialBT.println("LOGOUT_SUCCESS");
    Serial.println("Logged out.");

  } else if (cmdUpper == "ON" && isLoggedIn && !floodActive) {
    digitalWrite(controlLed, HIGH);
    controlLedState = true;
    SerialBT.println("LED_ON");
    Serial.println("Control LED turned ON.");

  } else if (cmdUpper == "OFF" && isLoggedIn && !floodActive) {
    digitalWrite(controlLed, LOW);
    controlLedState = false;
    SerialBT.println("LED_OFF");
    Serial.println("Control LED turned OFF.");

  } else if ((cmdUpper == "ON" || cmdUpper == "OFF") && floodActive) {
    SerialBT.println("BLOCKED_DUE_TO_IR");
    Serial.println("LED control blocked: IR flooding is active.");

  } else if ((cmdUpper == "ON" || cmdUpper == "OFF") && !countdownDone) {
    SerialBT.println("WAIT_STARTUP");
    Serial.println("Please wait: IR flooding startup delay.");

  } else if (cmdUpper.startsWith("SET_DELAY ") && isLoggedIn) {
    String delayVal = cmd.substring(10);
    irDelay = delayVal.toInt();
    SerialBT.print("DELAY_SET_TO ");
    SerialBT.println(irDelay);
    Serial.print("IR delay set to: ");
    Serial.println(irDelay);

  } else if (cmdUpper == "IR_ON" && isLoggedIn) {
    floodActive = true;
    SerialBT.println("IR_LED_ON");
    Serial.println("IR flooding activated.");

  } else if (cmdUpper == "IR_OFF" && isLoggedIn) {
    floodActive = false;
    turnOffIR();
    SerialBT.println("IR_FLOOD_OFF");
    Serial.println("IR flooding deactivated.");

  } else {
    SerialBT.println("INVALID_COMMAND");
    Serial.println("Invalid command received.");
  }
}

void floodIR() {
  for (int i = 0; i < NUM_LEDS; i++) {
    digitalWrite(ledPins[i], HIGH);
  }
  delayMicroseconds(irDelay);
  for (int i = 0; i < NUM_LEDS; i++) {
    digitalWrite(ledPins[i], LOW);
  }
  delayMicroseconds(irDelay);
}

void turnOffIR() {
  for (int i = 0; i < NUM_LEDS; i++) {
    digitalWrite(ledPins[i], LOW);
  }
}
