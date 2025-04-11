#include "BluetoothSerial.h"
BluetoothSerial SerialBT;

#define NUM_LEDS 9
int ledPins[NUM_LEDS] = {13, 12, 14, 27, 26, 25, 33, 32, 15};  // IR LED pins
const int ledIndicator = 2;  // External LED pin
int irDelay = 20;            // Delay between ON/OFF pulses (in microseconds)
bool floodActive = false;
bool countdownDone = false;
bool isLoggedIn = false;
bool previousClientState = false; // NEW


const String correctPassword = "admin123";
String incomingCommand = "";

void setup() {
  Serial.begin(115200);
  SerialBT.begin("ESP32_IR_Blocker");
  Serial.println("Bluetooth started. Waiting for app to connect...");

  // Initialize all IR LED pins
  for (int i = 0; i < NUM_LEDS; i++) {
    pinMode(ledPins[i], OUTPUT);
    digitalWrite(ledPins[i], LOW);
  }

  pinMode(ledIndicator, OUTPUT);
  digitalWrite(ledIndicator, HIGH);  // External LED always ON (indicates power)

  delay(15000);  // 15-second startup delay
  countdownDone = true;
  Serial.println("Startup delay complete. Ready to accept commands.");
}

void loop() {
  // Check if app connected via Bluetooth
 bool currentClientState = SerialBT.hasClient();
if (currentClientState && !previousClientState) {
  Serial.println("App connected via Bluetooth.");
}
previousClientState = currentClientState;
ooo

  // Handle incoming Bluetooth data
  while (SerialBT.available()) {
    char c = SerialBT.read();
    if (c == '\n') {
      processCommand(incomingCommand);
      incomingCommand = "";
    } else {
      incomingCommand += c;
    }
  }

  // Perform IR flooding only if active, logged in, and after delay
  if (countdownDone && floodActive && isLoggedIn) {
    floodIR();
  }
}

void processCommand(String cmd) {
  cmd.trim();
  Serial.print("Raw Command Received: '");
  Serial.print(cmd);
  Serial.println("'");

  String cmdUpper = cmd;
  cmdUpper.toUpperCase();

  if (cmdUpper.startsWith("LOGIN ")) {
    String pass = cmd.substring(6);
    pass.trim();
    Serial.print("Password Received: '");
    Serial.print(pass);
    Serial.println("'");

    if (pass == correctPassword) {
      isLoggedIn = true;
      SerialBT.println("LOGIN_SUCCESS");
      Serial.println("Login successful.");
    } else {
      isLoggedIn = false;
      SerialBT.println("LOGIN_FAIL");
      Serial.println("Login failed. Wrong password.");
    }

  } else if (cmdUpper == "LOGOUT") {
    isLoggedIn = false;
    SerialBT.println("LOGOUT_SUCCESS");
    Serial.println("Logged out.");

  } else if (cmdUpper == "ON" && isLoggedIn) {
    floodActive = true;
    SerialBT.println("IR_FLOOD_ON");
    Serial.println("IR flooding activated.");

  } else if (cmdUpper == "OFF" && isLoggedIn) {
    floodActive = false;
    turnOffIR();
    SerialBT.println("IR_FLOOD_OFF");
    Serial.println("IR flooding stopped.");

  } else if (cmdUpper.startsWith("SET_DELAY ") && isLoggedIn) {
    String delayVal = cmd.substring(10);
    irDelay = delayVal.toInt();
    SerialBT.print("DELAY_SET_TO ");
    SerialBT.println(irDelay);
    Serial.print("IR delay set to: ");
    Serial.println(irDelay);

  } else if (cmdUpper == "IR_ON" && isLoggedIn) {
    turnOnIR();
    SerialBT.println("IR_LED_ON");
    Serial.println("IR LEDs turned ON.");

  } else if (cmdUpper == "IR_OFF" && isLoggedIn) {
    turnOffIR();
    SerialBT.println("IR_LED_OFF");
    Serial.println("IR LEDs turned OFF.");

  } else {
    SerialBT.println("INVALID_COMMAND");
    Serial.println("Invalid command received.");
  }
}

void floodIR() {
  // Emit pulsed 38kHz IR signal on all LEDs
  for (int i = 0; i < NUM_LEDS; i++) {
    digitalWrite(ledPins[i], HIGH);
  }
  delayMicroseconds(irDelay);
  for (int i = 0; i < NUM_LEDS; i++) {
    digitalWrite(ledPins[i], LOW);
  }
  delayMicroseconds(irDelay);
}

void turnOnIR() {
  for (int i = 0; i < NUM_LEDS; i++) {
    digitalWrite(ledPins[i], HIGH);
  }
}

void turnOffIR() {
  for (int i = 0; i < NUM_LEDS; i++) {
    digitalWrite(ledPins[i], LOW);
  }
}
