package com.unregistered;

import com.rapplogic.xbee.api.*;
import com.rapplogic.xbee.api.wpan.RxResponse64;

import java.util.List;

public class Main {
    private static final int THRESHOLD = 90; // From the arduino sketch: when to set off an alarm
    private static XBee xbee = new XBee();
    private static AlarmService alarm = new AlarmService();

    public static void main(String[] args) throws Exception {
        setup();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                System.out.println("Teardown...");
                teardown();
            }
        }));

        scanNetwork();

        while (true) {
            try {
                XBeeResponse response = xbee.getResponse(30000);
                handleResponse(response);
            } catch (XBeeTimeoutException e) {
                System.out.println("Nothing...");
                scanNetwork();
            }
        }
    }

    public static void setup() throws Exception {
        String port = System.getProperty("serialport");
        xbee.open(port, 9600);
        System.out.println("Setup complete");
    }

    public static void teardown() {
        try {
            xbee.close();
        } catch (Exception e) {
            System.err.println("Did not close connection properly");
        }
    }

    public static void scanNetwork() throws Exception {
        NetworkDiscovery nd = new NetworkDiscovery(xbee);
        List<NetworkDiscovery.NodeResponse> responses = nd.scan();
        for (NetworkDiscovery.NodeResponse response : responses) {
            System.out.println("Response: " + response.getSerialAsHex());
        }
    }

    public static void handleResponse(XBeeResponse response) {
        if (response.getApiId() == ApiId.RX_64_RESPONSE) {
            RxResponse64 castedResponse = (RxResponse64)response;

            int[] bytes = castedResponse.getData();
            if (bytes.length != 1) {
                System.out.println("Unexpected length");
                return;
            }

            int reading = bytes[0];
            if (reading < 0) {
                // This is a heartbeat message, we can ignore
            } else {
                if (reading < THRESHOLD) {
                    alarm.triggerAlarm();
                }
            }
            System.out.println("Reading: " + reading);

        }
    }

}
