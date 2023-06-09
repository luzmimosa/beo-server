package com.fadedbytes.beo;

import com.fadedbytes.beo.console.BeoConsole;
import com.fadedbytes.beo.console.ServerConsole;
import com.fadedbytes.beo.event.EventManager;
import com.fadedbytes.beo.log.LogLevel;
import com.fadedbytes.beo.log.LogManager;
import com.fadedbytes.beo.network.NetworkManager;
import com.fadedbytes.beo.server.BeoServer;
import com.fadedbytes.beo.server.StandaloneServer;
import com.fadedbytes.beo.server.listener.ServerControlListener;
import com.fadedbytes.beo.stats.JvmStatProvider;
import com.fadedbytes.beo.util.key.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;

public class BinaryElementalOrbs {

    private static boolean isDebug;
    private static String serverName;
    private static boolean openGui;
    private static String licenceServerHost;

    private static LogManager logManager;
    private static BeoConsole console;
    private static EventManager eventManager;
    private static BeoServer server;

    public static void main(String[] args) {
        runPrestartOperations(args);

        logManager = createLogManager();
        console = configureConsole();
        eventManager = createEventManager();

        if (openGui) {
            // Execute "./Beo Console.exe" to open the GUI
            try {
                Runtime.getRuntime().exec("cmd /c start \"\" \"Beo Console.exe\"");
            } catch (IOException e) {
                System.err.println("Failed to open GUI: " + e.getMessage());
            }
        }

        if (isDebug) {
            printDebugModeAlert(
                    Arrays.stream(args).anyMatch(arg -> arg.equalsIgnoreCase("--skip-debug-delay"))
            );
        }

        if (!checkServerLicence()) {
            logManager.getMainLogger().error("Server licence is invalid, stopping server. Please contact the BEO team for more information.");
            System.exit(-1);
        }
        logManager.getMainLogger().info("Licence is valid. Starting Beo Server...");

        server = new StandaloneServer.Builder()
                .logger(logManager.getMainLogger())
                .name(serverName)
                .console(console)
                .eventManager(eventManager)
                .version("0.0.1")
                .build();

        try {
            NetworkManager networkManager = new NetworkManager(server, 8080);
        } catch (Exception e) {
            server.getLogger().error("Failed to start network manager, stopping server: " + e.getMessage(), e.getStackTrace());
            System.exit(-1);
        }
        JvmStatProvider.getInstance().startStatThread();

    }

    public static BeoServer getServer() {
        return server;
    }

    private static void runPrestartOperations(String[] args) {
        isDebug = Arrays.stream(args).anyMatch(arg -> arg.equalsIgnoreCase("--debug"));
        serverName = Arrays.stream(args)
                .filter(arg -> arg.startsWith("--server-name="))
                .findFirst()
                .map(arg -> arg.substring("--server-name=".length()))
                .orElse("beo_server");
        openGui = Arrays.stream(args).noneMatch(arg -> arg.equalsIgnoreCase("nogui"));
        licenceServerHost = Arrays.stream(args)
                .filter(arg -> arg.startsWith("--licence-server="))
                .findFirst()
                .map(arg -> arg.substring("--licence-server=".length()))
                .orElse("auth.beo.fadedbytes.com");
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

        LogLevel consoleLogLevel = isDebug ? LogLevel.SYSTEM : LogLevel.INFO;

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

    public static void shutdown() {
        server.getLogger().info("Shutting down server...");

        System.exit(0);
    }

    private static boolean checkServerLicence() {
        logManager.getMainLogger().info("Checking server licence...");

        String key = "-";
        File licenceFile = new File("licence.key");
        if (licenceFile.exists()) {
            try {
                key = Files.readString(licenceFile.toPath());
            } catch (IOException e) {
                System.err.println("Failed to read licence file: " + e.getMessage());
                return false;
            }
        };

        int port = 9987;
        try {
            URL url = new URL("http://" + licenceServerHost + ":" + port + "/" + key);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                return true;
            } else {
                return false;
            }
        } catch (ConnectException e) {
            System.err.println("Failed to connect to licence server: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("Error connecting to licence server: " + e.getMessage());
            return false;
        }
    }
}