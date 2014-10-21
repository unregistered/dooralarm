package com.unregistered;

import com.rapplogic.xbee.api.*;
import com.rapplogic.xbee.api.wpan.RxResponse64;

import java.sql.Timestamp;
import java.util.List;

public class Main {
    private static final int THRESHOLD = 80; // From the arduino sketch: when to set off an alarm
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
                log("Nothing...");
                scanNetwork();
            }
        }
    }

    public static void setup() throws Exception {
        String port = System.getProperty("serialport");
        xbee.open(port, 9600);
        log("Setup complete");
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
            log("Response: " + response.getSerialAsHex());
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
            if (reading == 255) {
                // This is a heartbeat message, we can ignore
            } else {
                log("Reading: " + reading);

                if (reading < THRESHOLD) {
                    alarm.triggerAlarm();
                }
            }

        }
    }

    public static void log(String msg) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        System.out.println("[" + now.toString() + "] " + msg);
    }

}
