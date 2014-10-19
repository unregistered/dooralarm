#define FILTERLENGTH 5

class MedianFilter {
private:
	// Circular buf for storing history
	int storage[FILTERLENGTH];
	short idx = 0;

	// Sorted version of history
	int copy[FILTERLENGTH];

public:
	MedianFilter();
	void addDataPoint(long dp);
	long getMedian();

private:
	void cloneStorageToCopy();
	void sortCopy();
};