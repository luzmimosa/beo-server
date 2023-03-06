package com.fadedbytes.beo.server;

import com.fadedbytes.beo.console.BeoConsole;
import com.fadedbytes.beo.log.BeoLogger;
import com.fadedbytes.beo.util.key.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public interface BeoServer {

    /**
     * Gets the namespaced key of this server. This key is used to identify the server.
     * @return The namespaced key of this server.
     */
    @NotNull NamespacedKey getServerKey();

    /**
     * @return the name of the server.
     */
    @NotNull String getName();

    /**
     * @return The current main logger of this server.
     */
    @NotNull BeoLogger getLogger();

    /**
     * @return The current console of this server.
     */
    @NotNull BeoConsole getConsole();

}
