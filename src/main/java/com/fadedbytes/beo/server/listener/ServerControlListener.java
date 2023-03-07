package com.fadedbytes.beo.server.listener;

import com.fadedbytes.beo.event.listener.EventListener;
import com.fadedbytes.beo.event.listener.Listener;
import com.fadedbytes.beo.event.type.control.ServerStartupEvent;

/**
 * Listener that listens for control events, and register them into the log.
 */
public class ServerControlListener implements Listener {

    @EventListener
    public static void onServerStartup(ServerStartupEvent event) {
        event.getServer().getLogger().event("Server instance deployed successfully: " + event.getServer().getServerKey());
    }

}
