#define echoPin 6 // Echo Pin
#define trigPin 5 // Trigger Pin
#define LEDPin 13 // Onboard LED

#define MY_XB_ADDRESS 0x2
#define BASE_XB_ADDRESS 0x1

#define RANGE_THRESHOLD 90
#include "MedianFilter.h"
#include "XBee.h"

MedianFilter filter = MedianFilter();
XBee xbee = XBee();

void setup() {
	Serial.begin (9600);
	pinMode(trigPin, OUTPUT);
	pinMode(echoPin, INPUT);
	pinMode(LEDPin, OUTPUT); // Use LED indicator (if required)

	xbee.begin(Serial);
}

long timeOfLastHeartbeat = 0;
long timeOfLastRead = 0;
long timeOfLastCompute = 0;

typedef enum {NOTHINGINRANGE, SOMETHINGINRANGE} range_state_t;
range_state_t currentState = NOTHINGINRANGE;

void loop() {
	long now = millis();

	xbee.readPacket();
	if (xbee.getResponse().isAvailable()) {
		// Serial.println("Got packet");
	}

	if (now > timeOfLastRead + 50) {
		// Just read and store the latest datapoint
		timeOfLastRead = now;

		long distance = getDistance();
		filter.addDataPoint(distance);
	}

	if (now > timeOfLastCompute + 200) {
		timeOfLastCompute = now;

		// Compute median, update state
		long median = filter.getMedian();

		if (currentState == NOTHINGINRANGE) {
			if (median < RANGE_THRESHOLD) {
				currentState = SOMETHINGINRANGE;
				updateLED();
				sendToBase(median);
			}
		} else if (currentState == SOMETHINGINRANGE) {
			if (median > RANGE_THRESHOLD) {
				currentState = NOTHINGINRANGE;
				updateLED();
			}
		}
	}

	if (now > timeOfLastHeartbeat + 10000) {
		// Send heartbeat
		timeOfLastHeartbeat = now;
		sendToBase(-1);
	}

	delay(1);
}

void updateLED() {
	if (currentState == SOMETHINGINRANGE) {
		digitalWrite(LEDPin, HIGH);
	} else {
		digitalWrite(LEDPin, LOW);
	}
}

void sendToBase(uint8_t distance) {
	uint8_t payload[] = {distance};
	Tx16Request tx = Tx16Request(BASE_XB_ADDRESS, payload, sizeof(payload));
	xbee.send(tx);
}

long getDistance() {
	triggerPing();
	return readDistance();
}

void triggerPing() {
	digitalWrite(trigPin, LOW); 
	delayMicroseconds(2); 

	digitalWrite(trigPin, HIGH);
	delayMicroseconds(10); 

	digitalWrite(trigPin, LOW);
}

long readDistance() {
	long duration = pulseIn(echoPin, HIGH);
	long distance = duration / 58.2;

	return distance;
}
