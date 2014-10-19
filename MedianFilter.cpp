#include "MedianFilter.h"

MedianFilter::MedianFilter() {
	for (int i=0; i < FILTERLENGTH; i++) {
		storage[i] = 0;
		copy[i] = 0;
	}
}

void MedianFilter::addDataPoint(long dp) {
	storage[idx++] = dp;
	if (idx >= FILTERLENGTH) {
		idx = 0;
	}
}

long MedianFilter::getMedian() {
	cloneStorageToCopy();
	sortCopy();
	return copy[FILTERLENGTH/2];
}

void MedianFilter::cloneStorageToCopy() {
	for (int i=0; i < FILTERLENGTH; i++) {
		copy[i] = storage[i];
	}
}

void MedianFilter::sortCopy() {
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