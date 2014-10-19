# Rangefinder + Audio server = Door Alarm

## Building and running

	gradle run

## Adding a new sound

Add mp3 to `src/main/resources/sounds`

Run `gradle generateIndex` to updated the index, otherwise the audio server won't know about it.