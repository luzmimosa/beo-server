package com.fadedbytes.beo.network;

import com.fadedbytes.beo.network.packet.RawNetworkPacket;
import com.fadedbytes.beo.network.protocol.NetworkPacketParser;
import com.fadedbytes.beo.network.protocol.ProtocolPacket;
import com.fadedbytes.beo.network.socket.SocketController;

import java.util.HashMap;

public class NetworkManager {

    private static final String ERROR_RESPONSE = "{\"error\": \"Invalid packet\"}";

    private final HashMap<Integer, SocketController> socketControllers = new HashMap<>();

    public NetworkManager() {

    }

    public SocketController openPort(int port) {
        try {
            return new SocketController("0.0.0.0", port, this);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to open port " + port + ": " + e.getMessage());
        }
    }

    public void receivePacket(RawNetworkPacket packet) {
        // TODO: this should be done in a separate thread
        try {
            ProtocolPacket parsedPacket = NetworkPacketParser.parse(packet);

        } catch (Exception e) {
            System.out.println("Error on receivePacket: " + e.getMessage());
            packet.respond(ERROR_RESPONSE);
        }
    }

}
