#include <Arduino.h>
#include <WiFi.h>
#include <WiFiMulti.h>
#include <SocketIoClient.h>
#define USE_SERIAL Serial
#define touchPin T0
const int threshold = 20;
int touchValue;
int touch = 0;

WiFiMulti WiFiMulti;
SocketIoClient webSocket;

//Wifi//
const char *ssid = "YourSSID";
const char *pass = "YourPassword";
const char *HOST = "255.255.255.255";

// All Variable //
const int potPin =32;
const int ledPin =18;
const int ledPin2 =19;
int outputValue = 0;
int potValue = 0;
int resolution = 8;
int freq = 5000;
int ledChannel = 0;
int ledChannel2 = 1;
int dutyCycle = 0;
int load =0;
bool ledState = false;
float tegangan;
bool sendMessageLed = true;
bool ledSockets = false;
bool touchMessage = false;

// Smoohting 
const int numReadings = 10;
int readings[numReadings];      
int readIndex = 0;              
int total = 0;                  
int average = 0;

    

void setup() {
  // put your setup code here, to run once:
  USE_SERIAL.begin(9600);
  USE_SERIAL.setDebugOutput(true);
  USE_SERIAL.println();
  for (uint8_t t = 4; t > 0; t--){
    USE_SERIAL.printf("[SETUP] BOOT WAIT %d...\n", t);
    USE_SERIAL.flush();
    delay(1000);
  }

  // Wifi Connected
  WiFiMulti.addAP(ssid, pass);

  while (WiFiMulti.run() != WL_CONNECTED){
    delay(100);
  }
  // END WIFI

  //Smhooting Potentio 
  for (int thisReading = 0; thisReading < numReadings; thisReading++) {
    readings[thisReading] = 0;
  }
  //End Smhooting Potentio

  //LED ESP32
  ledcSetup(ledChannel, freq, resolution);
  ledcAttachPin(ledPin, ledChannel);
  ledcSetup(ledChannel2, freq, resolution);
  ledcAttachPin(ledPin2, ledChannel2);
  ledcWrite(ledChannel, dutyCycle);
  //End LED ESP32

  //Socket.IO initiliaze
  webSocket.on("change_led",ledOfOn);
  webSocket.begin(HOST,3000);
  //End Socket.io
}

void loop() {
  webSocket.loop();
  ledcWrite(ledChannel, dutyCycle);
  potSocket();

  //Serial.print("Digital Read = ");
  //Serial.print(dutyCycle);
 // Serial.print(" Analog read = ");
  //Serial.println(potValue);
  ledCheck();
  touchici();
  
  delay(1);

}
void touchici(){
  touchValue = touchRead(touchPin);
  if(touchValue < threshold&&touchMessage)
  {
    USE_SERIAL.printf("Touched");
   touchMessage = false;
     webSocket.emit("status_touch","\"Touched\"");
   }if(touchValue > threshold&&!touchMessage){
    touchMessage = true;
    webSocket.emit("status_touch","\"Not Touch\"");
    USE_SERIAL.printf("Not Touched");
   }
}
void potSocket(){
  dutyCycle = map(potSmooth(potPin), 0, 4095, 0, 255);
  if(load == 0&&ledState){
    load = dutyCycle;
    ledState = false;
    
  }
  if(dutyCycle != load){
    //char message = ;
    webSocket.emit("status_potentio",("\""+String(dutyCycle)+"\"").c_str());
    load = dutyCycle;
    ledState = true;
  }
}
void ledSocket(const bool newState){
  if(newState){
     ledSockets = false;
     ledcWrite(ledChannel2,255);
  }
  if(!newState){
    ledSockets = true;
    ledcWrite(ledChannel2,0);
  }
}
void ledCheck(){
  char* message = "\"OFF\"";
  if(!ledSockets&&!sendMessageLed){
    message = "\"ON\"";
    webSocket.emit("status_led",message);
    sendMessageLed = true;
    //ledSockets = false;
    USE_SERIAL.printf("ON");
  }
  if(ledSockets&&sendMessageLed){
    ledcWrite(ledChannel, 0);
    webSocket.emit("status_led",message);
    sendMessageLed = false;
    //ledSockets = true;
    USE_SERIAL.printf("OFF");
  }
  
}
void ledOfOn(const char * payload, size_t length){
    String message = String(payload).c_str();
   if(message == "ON"){
    USE_SERIAL.printf("got message: %s\n", payload);
    ledSocket(!ledState);
   }
   if(message == "OFF"){
    USE_SERIAL.printf("got message: %s\n", payload);
      ledSocket(ledState);
   }
}

int potSmooth(int potPin){
  // subtract the last reading:
  total = total - readings[readIndex];
  // read from the sensor:
  readings[readIndex] = analogRead(potPin);
  // add the reading to the total:
  total = total + readings[readIndex];
  // advance to the next position in the array:
  readIndex = readIndex + 1;

  // if we're at the end of the array...
  if (readIndex >= numReadings) {
    // ...wrap around to the beginning:
    readIndex = 0;
  }

  // calculate the average:
  average = total / numReadings;
  // send it to the computer as ASCII digits
  return average;

}
