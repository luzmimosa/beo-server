package com.fadedbytes.beo.network.protocol.upstream;

import com.fadedbytes.beo.network.protocol.ProtocolPacket;
import org.json.JSONObject;

public class UpstreamPing extends ProtocolPacket {
    public UpstreamPing(JSONObject packetJson) {
        super(packetJson);
    }
}
