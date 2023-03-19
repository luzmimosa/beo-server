package com.fadedbytes.beo.network.protocol;

import com.fadedbytes.beo.network.packet.RawNetworkPacket;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.net.ProtocolException;


public class NetworkPacketParser {
    public static @NotNull ProtocolPacket parse(RawNetworkPacket rawPacket) throws ProtocolException {
        try {
            JSONObject json = new JSONObject(rawPacket.getRawContent());
            String packetName = json.getString("type");

            PacketType packetType = PacketType.upstreamPacketByName(packetName);

            if (packetType == null) {
                throw new ProtocolException("Unknown packet type: " + packetName);
            }

            ProtocolPacket packet = packetType.getPacketTypeClass().getConstructor(JSONObject.class).newInstance(json);
            return packetType.getPacketTypeClass().getConstructor(JSONObject.class).newInstance(json);


        } catch (JSONException e) {
            throw new ProtocolException("Invalid JSON packet: " + e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Packet class does not have a constructor that takes a JSONObject: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse packet: " + e.getMessage());
        }
    }

}
