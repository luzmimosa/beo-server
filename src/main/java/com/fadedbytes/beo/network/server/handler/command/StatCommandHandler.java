package com.fadedbytes.beo.network.server.handler.command;

import com.fadedbytes.beo.network.server.handler.NetworkCommand;
import com.fadedbytes.beo.network.server.handler.NetworkHandler;
import com.fadedbytes.beo.stats.LocalStatManager;
import com.google.gson.Gson;

public class StatCommandHandler implements NetworkHandler {

    private Gson gson = new Gson();
    @Override
    public void handleRequest(NetworkCommand command) {
        if (!command.command().equals("stats")) return;

        if (command.args().length == 0) {
            sendSavedStatsLevelsList(command);
            return;
        }

        if (command.args().length == 1) {
            sendLevelStats(command, command.args()[0]);
            return;
        }
    }

    private void sendSavedStatsLevelsList(NetworkCommand command) {
        command.sendResponse().provide(
            gson.toJson(LocalStatManager.getInstance().savedLevelStatsNames())
        );
    }

    private void sendLevelStats(NetworkCommand command, String levelName) {
        String stats = LocalStatManager.getInstance().readStatsToJsonString(levelName);

        if (stats == null) {
            command.sendResponse().provide("{\"error\": \"No stats found for level " + levelName + "\"}");
            return;
        }

        command.sendResponse().provide(
            LocalStatManager.getInstance().readStatsToJsonString(levelName)
        );
    }
}
