package com.fadedbytes.beo.network;

import com.fadedbytes.beo.network.server.handler.command.*;
import com.fadedbytes.beo.server.BeoServer;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class NetworkManager {

    private final Thread networkThread;

    private final HttpServer httpServer;

    public NetworkManager(BeoServer server, int port) throws IOException {

        this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);

        this.networkThread = new Thread(() -> {
            this.httpServer.createContext("/server", new ServerCommandHandler(server));
            this.httpServer.createContext("/stats", new StatCommandHandler());
            this.httpServer.createContext("/reload", new ReloadCommandHandler(server));
            this.httpServer.createContext("/jvm", new JvmCommandHandler());
            this.httpServer.createContext("/shutdown", new ShutdownCommandHandler(server));
            this.httpServer.createContext("/logs", new LogCommandHandler());

            this.httpServer.createContext("/", exchange -> {
                // Construye el JSON de error
                String errorJson = "{\"error\": \"not_found\"}";

                Headers headers = exchange.getResponseHeaders();
                headers.set("Access-Control-Allow-Origin", "*");

                // Envía la respuesta al cliente
                exchange.sendResponseHeaders(200, errorJson.getBytes().length);

                OutputStream outputStream = exchange.getResponseBody();
                outputStream.write(errorJson.getBytes());
                outputStream.close();
            });

            this.httpServer.start();
        });

        this.networkThread.start();
    }



}
