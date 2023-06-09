package com.fadedbytes.beo.network.server.handler.command;

import com.fadedbytes.beo.BinaryElementalOrbs;
import com.fadedbytes.beo.api.exception.level.register.LevelNotRunningException;
import com.fadedbytes.beo.api.exception.level.register.UnregisteredLevelException;
import com.fadedbytes.beo.network.server.handler.NetworkCommand;
import com.fadedbytes.beo.network.server.handler.NetworkHandler;
import com.fadedbytes.beo.server.BeoServer;

public class ShutdownCommandHandler implements NetworkHandler {

    private final BeoServer server;

    public ShutdownCommandHandler(BeoServer server) {
        this.server = server;
    }

    @Override
    public void handleRequest(NetworkCommand command) {
        if (!command.command().equals("shutdown")) {
            return;
        }

        if (command.args().length == 0) {
            new Thread(() -> {

                this.server.getRegisteredLevels().forEach(level -> {

                    if (!level.isRunning()) {
                        return;
                    }

                    try {
                        server.stopLevel(level);
                    } catch (LevelNotRunningException | UnregisteredLevelException e) {
                        throw new RuntimeException(e);
                    }
                });

                while (server.getRegisteredLevels().stream().anyMatch(level -> level.isRunning())) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                this.shutdown(command);
            }).start();
            return;
        }

        this.shutdown(command);
    }

    private void shutdown(NetworkCommand command) {
        command.sendSuccess();
        server.getLogger().info("Shutting down server...");
        BinaryElementalOrbs.shutdown();
    }
}
