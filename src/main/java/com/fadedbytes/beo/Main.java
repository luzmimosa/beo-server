package com.fadedbytes.beo;

import com.fadedbytes.beo.console.BeoConsole;
import com.fadedbytes.beo.console.ServerConsole;
import com.fadedbytes.beo.log.LogLevel;
import com.fadedbytes.beo.log.LogManager;
import com.fadedbytes.beo.util.key.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class Main {

    private static LogManager logManager;
    private static BeoConsole console;

    public static void main(String[] args) {
        logManager = createLogManager();
        console = configureConsole();

        logManager.getMainLogger().info("Starting Beo Server...");

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
        console.bindLogger(logManager.getMainLogger(), LogLevel.DEBUG);

        console.startConsole();

        return console;
    }
}