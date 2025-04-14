#include "BluetoothSerial.h"
#include <EEPROM.h>

BluetoothSerial SerialBT;

#define NUM_LEDS 8
int ledPins[NUM_LEDS] = {25, 26, 32, 33, 23, 22, 21, 19};
const int ledIndicator = 2;
const int controlLed = 4;
int irDelay = 20;
unsigned long startupDelay = 15000;
#define EEPROM_SIZE 4
#define DEFAULT_DELAY 15000
#define HEALTH_CHECK_INTERVAL 60000

bool floodActive = false;
bool countdownDone = false;
bool isLoggedIn = false;
bool previousClientState = false;
bool controlLedState = false;
unsigned long lastBlinkTime = 0;
bool blinkState = false;
unsigned long lastHealthCheck = 0;

const String correctPassword = "admin123";
String incomingCommand = "";

void setup() {
  Serial.begin(115200);
  EEPROM.begin(EEPROM_SIZE);
  
  startupDelay = EEPROM.readULong(0);
  if (startupDelay < 1000 || startupDelay > 60000) {
    startupDelay = DEFAULT_DELAY;
  }
  
  SerialBT.begin("ESP32_IR_Blocker");
  Serial.println("Bluetooth started. Waiting for app to connect...");

  for (int i = 0; i < NUM_LEDS; i++) {
    pinMode(ledPins[i], OUTPUT);
    digitalWrite(ledPins[i], LOW);
  }

  pinMode(ledIndicator, OUTPUT);
  digitalWrite(ledIndicator, HIGH);

  pinMode(controlLed, OUTPUT);
  digitalWrite(controlLed, LOW);

  Serial.print("Waiting ");
  Serial.print(startupDelay/1000);
  Serial.println(" seconds before IR flooding...");
  delay(startupDelay);
  
  floodActive = true;
  countdownDone = true;
  lastHealthCheck = millis();
  Serial.println("IR flooding started.");
}

void loop() {
  bool currentClientState = SerialBT.hasClient();
  if (currentClientState && !previousClientState) {
    Serial.println("App connected via Bluetooth.");
  }
  previousClientState = currentClientState;

  while (SerialBT.available()) {
    char c = SerialBT.read();
    if (c == '\n') {
      processCommand(incomingCommand);
      incomingCommand = "";
    } else {
      incomingCommand += c;
    }
  }

  if (floodActive) {
    if (millis() - lastBlinkTime > 500) {
      blinkState = !blinkState;
      digitalWrite(ledIndicator, blinkState ? HIGH : LOW);
      lastBlinkTime = millis();
    }
  } else {
    digitalWrite(ledIndicator, HIGH);
  }

  if (millis() - lastHealthCheck > HEALTH_CHECK_INTERVAL) {
    String healthStatus = checkLEDHealth();
    Serial.println("Auto Health Check: " + healthStatus);
    if (SerialBT.hasClient()) {
      SerialBT.println(healthStatus);
    }
    lastHealthCheck = millis();
  }

  if (countdownDone && floodActive) {
    floodIR();
  }
}

String checkLEDHealth() {
  String damagedLEDs = "";
  int damagedCount = 0;
  bool wasFlooding = floodActive;
  floodActive = false;
  
  for (int i = 0; i < NUM_LEDS; i++) {
    digitalWrite(ledPins[i], HIGH);
    delay(50);
    
    // Simple verification - replace with actual current sensing
    bool isWorking = (digitalRead(ledPins[i]) == HIGH);
    
    digitalWrite(ledPins[i], LOW);
    delay(50);
    
    if (!isWorking) {
      damagedCount++;
      if (!damagedLEDs.isEmpty()) damagedLEDs += ",";
      damagedLEDs += String(i+1);
    }
  }
  
  floodActive = wasFlooding;
  
  if (damagedCount == 0) return "ALL_IR_GOOD";
  else if (damagedCount == NUM_LEDS) return "ALL_IR_DAMAGED";
  else return String(damagedCount) + "_IR_DAMAGED:" + damagedLEDs;
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
  } 
  else if (cmdUpper == "LOGOUT") {
    isLoggedIn = false;
    SerialBT.println("LOGOUT_SUCCESS");
    Serial.println("Logged out.");
  } 
  else if (cmdUpper == "CHECK_IR_HEALTH" && isLoggedIn) {
    String healthStatus = checkLEDHealth();
    SerialBT.println(healthStatus);
    Serial.println("IR Health: " + healthStatus);
  }
  else if (cmdUpper == "ON" && isLoggedIn && !floodActive) {
    digitalWrite(controlLed, HIGH);
    controlLedState = true;
    SerialBT.println("LED_ON");
    Serial.println("Control LED turned ON.");
  } 
  else if (cmdUpper == "OFF" && isLoggedIn && !floodActive) {
    digitalWrite(controlLed, LOW);
    controlLedState = false;
    SerialBT.println("LED_OFF");
    Serial.println("Control LED turned OFF.");
  } 
  else if ((cmdUpper == "ON" || cmdUpper == "OFF") && floodActive) {
    SerialBT.println("BLOCKED_DUE_TO_IR");
    Serial.println("LED control blocked: IR flooding is active.");
  } 
  else if (cmdUpper.startsWith("SET_DELAY ") && isLoggedIn) {
    String delayVal = cmd.substring(10);
    unsigned long newDelay = delayVal.toInt() * 1000UL;
    
    if (newDelay >= 1000 && newDelay <= 60000) {
      startupDelay = newDelay;
      EEPROM.writeULong(0, startupDelay);
      EEPROM.commit();
      
      SerialBT.print("DELAY_SET_TO ");
      SerialBT.println(startupDelay/1000);
      Serial.print("Startup delay set to: ");
      Serial.print(startupDelay/1000);
      Serial.println(" seconds");
    } else {
      SerialBT.println("INVALID_DELAY (1-60s)");
    }
  } 
  else if (cmdUpper == "IR_ON" && isLoggedIn) {
    floodActive = true;
    SerialBT.println("IR_LED_ON");
    Serial.println("IR flooding activated.");
  } 
  else if (cmdUpper == "IR_OFF" && isLoggedIn) {
    floodActive = false;
    turnOffIR();
    digitalWrite(ledIndicator, HIGH);
    SerialBT.println("IR_FLOOD_OFF");
    Serial.println("IR flooding deactivated.");
  } 
  else if (cmdUpper == "GET_DELAY" && isLoggedIn) {
    SerialBT.print("CURRENT_DELAY ");
    SerialBT.println(startupDelay/1000);
  } 
  else {
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
