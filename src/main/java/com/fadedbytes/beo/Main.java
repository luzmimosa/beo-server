package com.fadedbytes.beo;

import com.fadedbytes.beo.api.level.Level;
import com.fadedbytes.beo.api.level.world.space.TridimensionalFlatSpaceFabric;
import com.fadedbytes.beo.console.BeoConsole;
import com.fadedbytes.beo.console.ServerConsole;
import com.fadedbytes.beo.event.EventManager;
import com.fadedbytes.beo.log.BeoLogger;
import com.fadedbytes.beo.log.LogLevel;
import com.fadedbytes.beo.log.LogManager;
import com.fadedbytes.beo.server.BeoServer;
import com.fadedbytes.beo.server.StandaloneServer;
import com.fadedbytes.beo.server.listener.ServerControlListener;
import com.fadedbytes.beo.util.key.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Arrays;

public class Main {

    private static LogManager logManager;
    private static BeoConsole console;
    private static EventManager eventManager;

    private static boolean isDebug;

    public static void main(String[] args) {

        isDebug = Arrays.stream(args).anyMatch(arg -> arg.equalsIgnoreCase("--debug"));

        logManager = createLogManager();
        console = configureConsole();

        if (isDebug) {
            printDebugModeAlert(
                    Arrays.stream(args).anyMatch(arg -> arg.equalsIgnoreCase("--skip-debug-delay"))
            );
        }

        logManager.getMainLogger().info("Starting Beo Server...");

        BeoServer server = new StandaloneServer.Builder()
                .logger(logManager.getMainLogger())
                .name("test_server")
                .console(console)
                .eventManager(createEventManager())
                .build();

        // Debug: create a level and run it

        Level level = createTestLevel(server);
        level.runLevel();

    }

    private static LogManager createLogManager() {
        return new LogManager();
    }

    private static @NotNull BeoConsole configureConsole() {
        BeoConsole console = new ServerConsole(
                NamespacedKey.of("server_console"),
                100,
                System.out
        );

        LogLevel consoleLogLevel = isDebug ? LogLevel.DEBUG : LogLevel.INFO;

        console.addOutputStream(System.out);
        console.bindLogger(logManager.getMainLogger(), consoleLogLevel);
        console.bindLogger(logManager.getEventLogger(), consoleLogLevel);

        console.startConsole();

        return console;
    }

    private static EventManager createEventManager() {
        EventManager eventManager = new EventManager(logManager.getEventLogger());
        eventManager.addEventListener(ServerControlListener.class);

        return eventManager;
    }

    private static void printDebugModeAlert(boolean skipDelay) {
        logManager.getMainLogger().warn("""
                   
                   █▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀█
                   █ WARNING: Debug mode is enabled. This may cause performance issues and security risks. █
                   █ The debug mode is intended for development purposes only.                             █
                   █▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█
                    """);

        if (!skipDelay) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static Level createTestLevel(BeoServer server) {
        return new Level(
                server,
                new TridimensionalFlatSpaceFabric()
        ) {
            @Override
            public @Range(from = 1, to = Integer.MAX_VALUE) int getMaxPlayers() {
                return 2;
            }

            @Override
            protected void onLevelStartSequence() {
                this.getLogger().debug("Executing onLevelStartSequence");
            }

            @Override
            protected void levelTick() {
                this.getLogger().debug("Level tick: " + Math.random());
            }
        };
    }
}