package com.fadedbytes.beo.network.protocol;

import com.fadedbytes.beo.network.protocol.upstream.UpstreamPing;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum PacketType {

    UPSTREAM_PING(
            "ping",
            UpstreamPing.class,
            Flow.UPSTREAM
    );

    private final @NotNull String name;
    private final @NotNull Class<? extends ProtocolPacket> packetTypeClass;
    private final @NotNull Flow flow;
    PacketType(
            @NotNull String name,
            @NotNull Class<? extends ProtocolPacket> packetTypeClass,
            @NotNull Flow flow
    ) {
        this.name = name;
        this.packetTypeClass = packetTypeClass;
        this.flow = flow;
    }

    public @NotNull String getPacketName() {
        return name;
    }

    public @NotNull Class<? extends ProtocolPacket> getPacketTypeClass() {
        return packetTypeClass;
    }

    public boolean isUpstream() {
        return this.flow == Flow.UPSTREAM;
    }

    public boolean isDownstream() {
        return this.flow == Flow.DOWNSTREAM;
    }

    public static @Nullable PacketType upstreamPacketByName(@NotNull String name) {
        for (PacketType packetType : PacketType.values()) {
            if (packetType.isUpstream() && packetType.getPacketName().equals(name)) {
                return packetType;
            }
        }

        return null;
    }

    public static @Nullable PacketType downstreamPacketByName(@NotNull String name) {
        for (PacketType packetType : PacketType.values()) {
            if (packetType.isDownstream() && packetType.getPacketName().equals(name)) {
                return packetType;
            }
        }

        return null;
    }

    private enum Flow {
        UPSTREAM,
        DOWNSTREAM
    }


}
