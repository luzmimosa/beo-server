package com.fadedbytes.beo.event.type.control;

import com.fadedbytes.beo.event.type.Event;
import com.fadedbytes.beo.server.BeoServer;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class ServerStartupEvent extends Event {
    public ServerStartupEvent(@NotNull BeoServer server) {
        super(server);
    }

}
