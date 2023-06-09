package com.fadedbytes.beo.network.server.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public interface NetworkHandler extends HttpHandler {

    @Override
    default void handle(@NotNull HttpExchange exchange) {
        // Obtiene la ruta solicitada
        String path = exchange.getRequestURI().getPath();

        // Obtiene los argumentos después de "/server/"
        String[] parts = path.split("/");
        String[] arguments = Arrays.copyOfRange(parts, 2, parts.length);

        handleRequest(new NetworkCommand(parts[1], arguments, response -> {
            try {
                Headers headers = exchange.getResponseHeaders();
                headers.set("Access-Control-Allow-Origin", "*");

                exchange.sendResponseHeaders(200, response.getBytes().length);

                OutputStream outputStream = exchange.getResponseBody();
                outputStream.write(response.getBytes());
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    void handleRequest(NetworkCommand command);

}
