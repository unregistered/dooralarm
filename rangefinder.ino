#define echoPin 6 // Echo Pin
#define trigPin 5 // Trigger Pin
#define LEDPin 13 // Onboard LED

#define LEDTHRESHOLD 30
#include "MedianFilter.h"

MedianFilter filter;

void setup() {
	Serial.begin (9600);
	pinMode(trigPin, OUTPUT);
	pinMode(echoPin, INPUT);
	pinMode(LEDPin, OUTPUT); // Use LED indicator (if required)
	filter = MedianFilter();
}

void loop() {
	for (int i=0; i < FILTERLENGTH; i++) {
		long distance = getDistance();
		filter.addDataPoint(distance);
		delayMicroseconds(100);
	}

	long median = filter.getMedian();
	Serial.println(median);

	if (median < LEDTHRESHOLD) {
		digitalWrite(LEDPin, HIGH);
	} else {
		digitalWrite(LEDPin, LOW);
	}

	delay(1000);
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
