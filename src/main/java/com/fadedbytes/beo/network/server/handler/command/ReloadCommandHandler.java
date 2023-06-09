package com.fadedbytes.beo.network.server.handler.command;

import com.fadedbytes.beo.api.level.Level;
import com.fadedbytes.beo.network.server.handler.CommandErrorCode;
import com.fadedbytes.beo.network.server.handler.NetworkCommand;
import com.fadedbytes.beo.network.server.handler.NetworkHandler;
import com.fadedbytes.beo.server.BeoServer;

public class ReloadCommandHandler implements NetworkHandler {

    private final BeoServer server;

    public ReloadCommandHandler(BeoServer server) {
        this.server = server;
    }

    @Override
    public void handleRequest(NetworkCommand command) {
        if (!command.command().equals("reload")) return;

        if (command.args().length == 0) {
            scheduleReload(command);
            return;
        }

        if (command.args().length == 1) {
            if (command.args()[0].equals("force")) {
                reloadLevelsAndOrbs(command);
                return;
            }
        }

        command.sendError(CommandErrorCode.INVALID_ARGUMENTS);
    }

    private void reloadLevelsAndOrbs(NetworkCommand command) {
        server.loadLevelsFromDisk();
        server.loadOrbsFromDisk();

        command.sendSuccess();
    }

    private void scheduleReload(NetworkCommand command) {
        new Thread(() -> {
            while (this.server.getRegisteredLevels().stream().anyMatch(Level::isRunning)) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            this.reloadLevelsAndOrbs(command);
        }).start();
    }
}
