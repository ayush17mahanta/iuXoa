  #include <ESP32Servo.h>
  #include <BluetoothSerial.h>
  #include <Wire.h>
  #include <TinyGPS++.h>
  #include <DHT.h>
  #include <Adafruit_Sensor.h>
  #include <Adafruit_BMP280.h>
  
  // Components
  Servo gripperServo;
  BluetoothSerial SerialBT;
  TinyGPSPlus gps;
  Adafruit_BMP280 bmp;
  DHT dht(4, DHT11);
  
  // Pins
  #define SERVO_PIN 13
  #define LED_PIN 2
  #define BATTERY_PIN 34
  #define OPEN_PIN 5  
  #define CLOSE_PIN 18  
  #define GPS_RX 16
  #define GPS_TX 17
  
  HardwareSerial gpsSerial(2);
  
  // Connection management
  bool btConnected = false;
  unsigned long lastConnectionTime = 0;
  const unsigned long CONNECTION_TIMEOUT = 10000; // 10 seconds
  
  void setup() {
      Serial.begin(115200);
      initializeBluetooth();
      
      pinMode(LED_PIN, OUTPUT);
      digitalWrite(LED_PIN, LOW);
      pinMode(OPEN_PIN, OUTPUT);
      pinMode(CLOSE_PIN, OUTPUT);
  
      dht.begin();
      if (!bmp.begin(0x76)) {
          Serial.println("BMP280 error!");
      }
      gpsSerial.begin(9600, SERIAL_8N1, GPS_RX, GPS_TX);
  
      gripperServo.attach(SERVO_PIN);
      gripperServo.write(180);
      Serial.println("System Ready");
  }
  
  void initializeBluetooth() {
      if(!SerialBT.begin("ESP32_AeroCommand")) {
          Serial.println("Bluetooth init failed!");
          ESP.restart();
      }
      
      // Corrected setPin() with both parameters
      const char* pin = "1234";
      SerialBT.setPin(pin, 4); // PIN and its length
      Serial.println("Bluetooth Ready - Persistent Connection Enabled");
  }
  
  void loop() {
      checkConnectionState();
      processBluetoothCommands();
      handleStatusLED();
      processGPS();
      autoSendSensorData();
  }
  
  void checkConnectionState() {
      bool currentState = SerialBT.hasClient();
      
      if(currentState != btConnected) {
          btConnected = currentState;
          Serial.println(btConnected ? "BT Connected" : "BT Disconnected");
          
          if(btConnected) {
              lastConnectionTime = millis();
          } else {
              // Safety measures on disconnect
              gripperServo.write(180);
              digitalWrite(OPEN_PIN, LOW);
              digitalWrite(CLOSE_PIN, HIGH);
              // Attempt to reconnect immediately
              if(millis() - lastConnectionTime > 1000) {
                  SerialBT.disconnect();
                  SerialBT.end();
                  initializeBluetooth();
              }
          }
      }
  }
  
  // Rest of the functions remain unchanged
  void processBluetoothCommands() {
      if(SerialBT.available()) {
          String command = SerialBT.readStringUntil('\n');
          command.trim();
          
          if(command == "O") {
              gripperServo.write(150);
              digitalWrite(OPEN_PIN, HIGH);
              digitalWrite(CLOSE_PIN, LOW);
              SerialBT.println("GRIPPER:OPEN");
          }
          else if(command == "C") {
              gripperServo.write(180);
              digitalWrite(OPEN_PIN, LOW);
              digitalWrite(CLOSE_PIN, HIGH);
              SerialBT.println("GRIPPER:CLOSED");
          }
          else if(command == "STATUS") {
              sendSensorData();
          }
      }
  }
  
  void handleStatusLED() {
      static unsigned long lastBlink = 0;
      static bool ledState = false;
  
      if(btConnected) {
          digitalWrite(LED_PIN, HIGH);
      } else {
          if(millis() - lastBlink > 1000) {
              ledState = !ledState;
              digitalWrite(LED_PIN, ledState);
              lastBlink = millis();
          }
      }
  }
  
  void processGPS() {
      while(gpsSerial.available()) {
          gps.encode(gpsSerial.read());
      }
  }
  
  void autoSendSensorData() {
      static unsigned long lastSent = 0;
      if(millis() - lastSent > 2000 && btConnected) {
          sendSensorData();
          lastSent = millis();
      }
  }
  
  void sendSensorData() {
      // Battery
      float battery = analogRead(BATTERY_PIN) / 4095.0 * 3.3 * 2;
      Serial.printf("BATTERY: %.2fV\n", battery);
      SerialBT.printf("BATTERY: %.2fV\n", battery);
  
      // DHT11
      float temp = dht.readTemperature();
      float hum = dht.readHumidity();
      
      if (!isnan(temp)) {
          Serial.printf("TEMP: %.1f°C\n", temp);
          SerialBT.printf("TEMP: %.1f°C\n", temp);
      }
      if (!isnan(hum)) {
          Serial.printf("HUMIDITY: %.1f%%\n", hum);
          SerialBT.printf("HUMIDITY: %.1f%%\n", hum);
      }
  
      // BMP280
      float pressure = bmp.readPressure() / 100.0F;  // Convert to hPa
      float altitude = bmp.readAltitude(1013.25);   // Adjust sea-level pressure if needed
      
      Serial.printf("PRESSURE: %.1f hPa\n", pressure);
      Serial.printf("ALTITUDE: %.1f m\n", altitude);
      
      SerialBT.printf("PRESSURE: %.1f hPa\n", pressure);
      SerialBT.printf("ALTITUDE: %.1f m\n", altitude);
  
      // GPS
      if (gps.location.isValid()) {
          Serial.printf("GPS: Lat: %.6f, Lon: %.6f\n", 
                        gps.location.lat(), 
                        gps.location.lng());
                        
          SerialBT.printf("GPS: Lat: %.6f, Lon: %.6f\n", 
                          gps.location.lat(), 
                          gps.location.lng());
      } else {
          Serial.println("GPS: No valid data");
          SerialBT.println("GPS: No valid data");
      }
  }
  
