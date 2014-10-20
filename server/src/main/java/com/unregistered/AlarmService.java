package com.unregistered;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class AlarmService {
    private static final String ALARM_ENDPOINT = "http://raspberrypi.local:4567/play/walkingin";
    private static long lastAlarmTriggeredAt = System.currentTimeMillis() + 10000; // Nothing for first 10 seconds on boot

    public void triggerAlarm() {
        long now = System.currentTimeMillis();
        if (now < lastAlarmTriggeredAt + 10000) {
            System.out.println("We had an alarm less than 10 seconds ago, skip");
            return;
        }

        lastAlarmTriggeredAt = now;
        pingInBackground();
    }

    private void pingInBackground() {
        Thread t = new Thread(new Runnable() {
            public void run()
            {
                pingEndpoint();
            }
        });
        t.start();
    }

    private void pingEndpoint() {
        try {
            System.out.println("We will trigger an alarm...");
            // We call to the audio server
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(ALARM_ENDPOINT);

            HttpResponse response = client.execute(post);
            System.out.println("Status code: " + response.getStatusLine().getStatusCode());
        } catch(Exception e) {
            System.err.println("Could not ping endpoint: " + e);
        }
    }
}
