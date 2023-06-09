package com.fadedbytes.beo.server.controller;

import com.fadedbytes.beo.api.level.Level;
import com.fadedbytes.beo.server.BeoServer;
import com.fadedbytes.beo.util.key.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ExternalServerInterface {

    private final BeoServer server;

    public ExternalServerInterface(@NotNull BeoServer server) {
        this.server = server;
    }

    public @NotNull Set<Level> getRunningLevels() {
        return this.server.getRegisteredLevels();
    }

    public @Nullable Level getRunningLevel(@NotNull NamespacedKey key) {
        return this.server.getRegisteredLevels().stream().filter(level -> level.KEY.equals(key)).findFirst().orElse(null);
    }

    //public @NotNull BeoServer

}
