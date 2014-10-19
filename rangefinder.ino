#define echoPin 6 // Echo Pin
#define trigPin 5 // Trigger Pin
#define LEDPin 13 // Onboard LED

#define LEDTHRESHOLD 30
#define FILTERLENGTH 5

class MedianFilter {
	// Circular buf for storing history
private:
	int storage[FILTERLENGTH];
	short idx = 0;

	// Sorted version of history
	int copy[FILTERLENGTH];

public:
	MedianFilter() {
		for (int i=0; i < FILTERLENGTH; i++) {
			storage[i] = 0;
			copy[i] = 0;
		}
	}

	void addDataPoint(long dp) {
		storage[idx++] = dp;
		if (idx >= FILTERLENGTH) {
			idx = 0;
		}
	}

	long getMedian() {
		cloneStorageToCopy();
		sortCopy();
		return copy[FILTERLENGTH/2];
	}

	void cloneStorageToCopy() {
		for (int i=0; i < FILTERLENGTH; i++) {
			copy[i] = storage[i];
		}
	}

	void sortCopy() {
		// Selection sort
		for (int i=0; i < FILTERLENGTH; i++) {
			int minimumSoFar = copy[i];
			int idxOfMinimum = i;
			for (int j=i; j < FILTERLENGTH; j++) {
				if (copy[j] < minimumSoFar) {
					minimumSoFar = copy[j];
					idxOfMinimum = j;
				}
			}
			if (idxOfMinimum > i) {
				// Do the swap
				int tmp = copy[i];
				copy[i] = copy[idxOfMinimum];
				copy[idxOfMinimum] = tmp;
			}
		}
	}
};

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
