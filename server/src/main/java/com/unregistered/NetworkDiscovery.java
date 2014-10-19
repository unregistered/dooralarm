package com.unregistered;

import com.rapplogic.xbee.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NetworkDiscovery {
    XBee xbee;
    NetworkDiscovery(XBee xbee) {
        this.xbee = xbee;
    }

    public List<NodeResponse> scan() {
        try {
            AtCommand cmd = new AtCommand("ND");
            AtCommandResponse response = (AtCommandResponse) xbee.sendSynchronous(cmd, 5000);
            if (response.isOk()) {
                System.out.println("Got response to ATND");
                int[] stream = response.getValue();
                if (stream.length == 0) {
                    System.out.println("ATND found no nodes!");
                }
                return nodesForIntStream(stream);
            }
        } catch (XBeeTimeoutException e) {
            System.err.println(e);
        } catch (XBeeException e) {
            System.err.println(e);
        }

        return Collections.emptyList();
    }

    private static List<NodeResponse> nodesForIntStream(int[] stream) {
        int idx = 0;
        ArrayList<NodeResponse> list = new ArrayList<NodeResponse>();

        while (idx < stream.length) {
            // 2 bytes for source address
            idx += 2; // which we ignore

            // 4 bytes for serial high
            long serialHigh = 0;
            serialHigh |= stream[idx++] << 24;
            serialHigh |= stream[idx++] << 16;
            serialHigh |= stream[idx++] << 8;
            serialHigh |= stream[idx++];

            // 4 bytes for serial low
            long serialLow = 0;
            serialLow |= stream[idx++] << 24;
            serialLow |= stream[idx++] << 16;
            serialLow |= stream[idx++] << 8;
            serialLow |= stream[idx++];

            long serial = serialHigh << 32 | serialLow;

            // 1 byte for RSSI
            int rssi = stream[idx++];

            // Node Identifier is null terminated string
            while (stream[idx++] != 0) {
                // Ignore
            }

            list.add(new NodeResponse(serial, rssi, ""));
        }

        return list;
    }

    static class NodeResponse {
        private long serial;
        private int rssi;

        NodeResponse(long serial, int rssi, String nodeIdentifier) {
            this.serial = serial;
            this.rssi = rssi;
        }

        public long getSerial() {
            return serial;
        }

        public String getSerialAsHex() {
            return Long.toHexString(serial).toUpperCase();
        }

        public int getRSSI() {
            return this.rssi;
        }
    }
}
