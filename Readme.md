# Rangefinder + Audio server = Door Alarm

## Setup
1. Flash firmware to arduino nano wired to XBee (Parallax adapter) and ultrasound rangefinder
2. Run audioserver, which listens to API calls to play audio
3. Run server, which gets packets from XBee and makes API calls to the audioserver

## Building and running

	gradle run

## Adding a new sound

Add mp3 to audioserver. Folders designate groups.