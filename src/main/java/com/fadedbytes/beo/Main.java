package com.fadedbytes.beo;

import com.fadedbytes.beo.console.BeoConsole;
import com.fadedbytes.beo.console.ServerConsole;
import com.fadedbytes.beo.event.EventManager;
import com.fadedbytes.beo.log.LogLevel;
import com.fadedbytes.beo.log.LogManager;
import com.fadedbytes.beo.server.BeoServer;
import com.fadedbytes.beo.server.StandaloneServer;
import com.fadedbytes.beo.util.key.NamespacedKey;
import org.jetbrains.annotations.NotNull;

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
                .build();
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

        console.addOutputStream(System.out);
        console.bindLogger(logManager.getMainLogger(), isDebug ? LogLevel.DEBUG : LogLevel.ERROR);

        console.startConsole();

        return console;
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
}