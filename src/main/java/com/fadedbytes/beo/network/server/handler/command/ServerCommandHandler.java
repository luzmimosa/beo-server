package com.fadedbytes.beo.network.server.handler.command;

import com.fadedbytes.beo.api.exception.level.LevelException;
import com.fadedbytes.beo.api.level.Level;
import com.fadedbytes.beo.network.server.handler.CommandErrorCode;
import com.fadedbytes.beo.network.server.handler.NetworkCommand;
import com.fadedbytes.beo.network.server.handler.NetworkHandler;
import com.fadedbytes.beo.server.BeoServer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public class ServerCommandHandler implements NetworkHandler {
    private final BeoServer server;
    public ServerCommandHandler(BeoServer server) {
        this.server = server;
    }
    @Override
    public void handleRequest(NetworkCommand command) {
        if (!command.command().equals("server")) return;

        if (command.args().length == 0) {
            sendServerInfo(command);
            return;
        }

        String action = command.args()[0];
        switch (action) {
            case "info" -> sendServerInfo(command);
            case "level" -> handleLevelCommand(command);
            default -> command.sendError(CommandErrorCode.INVALID_ARGUMENTS);
        }
    }

    private void sendServerInfo(NetworkCommand command) {

        JsonObject serverInfo = new JsonObject();
        serverInfo.addProperty("name", server.getName());
        serverInfo.addProperty("version", server.getServerVersion());

        JsonArray levels = new JsonArray();
        server.getRegisteredLevels().forEach(level -> levels.add(level.KEY.getKey()));
        serverInfo.add("levels", levels);

        String serverInfoString = serverInfo.toString();

        command.sendResponse().provide(
                serverInfoString
        );
    }

    private void handleLevelCommand(NetworkCommand command) {
        if (command.args().length < 2) {
            command.sendResponse().provide(formatLevels(server));
            return;
        }

        String levelName = command.args()[1];
        Level serverLevel = server.getRegisteredLevels().stream().filter(level -> level.KEY.getKey().equals(levelName)).findFirst().orElse(null);
        if (serverLevel == null) {
            command.sendError(CommandErrorCode.LEVEL_NOT_FOUND);
            return;
        }

        if (command.args().length == 2) {
            command.sendResponse().provide(formatLevel(serverLevel).toString());
            return;
        }

        String action = command.args()[2];
        switch (action) {
            case "start" -> {
                if (requestLevelStart(serverLevel)) {
                    command.sendSuccess();
                } else {
                    command.sendError(CommandErrorCode.LEVEL_START_ERROR);
                }
            }
            case "stop" -> {
                if (requestLevelStop(serverLevel)) {
                    command.sendSuccess();
                } else {
                    command.sendError(CommandErrorCode.LEVEL_STOP_ERROR);
                }
            }
            default -> command.sendError(CommandErrorCode.INVALID_ARGUMENTS);
        }
    }

    private boolean requestLevelStart(Level level) {
        try {
            server.startLevel(level);
            return true;
        } catch (LevelException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean requestLevelStop(Level level) {
        try {
            server.stopLevel(level);
            return true;
        } catch (LevelException e) {
            return false;
        }
    }

    private @NotNull String formatLevels(BeoServer server) {

        JsonArray levels = new JsonArray();
        server.getRegisteredLevels().forEach(level -> levels.add(formatLevel(level)));

        return levels.toString();

    }

    private @NotNull JsonObject formatLevel(Level level) {
        JsonObject levelObject = new JsonObject();
        levelObject.addProperty("name", level.KEY.getKey());
        levelObject.addProperty("max_players", level.getMaxPlayers());
        levelObject.addProperty("state", level.isRunning() ? 1 : level.didFinish() ? 2 : 0);

        return levelObject;
    }
}
