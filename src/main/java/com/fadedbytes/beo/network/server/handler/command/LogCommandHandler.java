package com.fadedbytes.beo.network.server.handler.command;

import com.fadedbytes.beo.network.server.handler.NetworkCommand;
import com.fadedbytes.beo.network.server.handler.NetworkHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Objects;

public class LogCommandHandler implements NetworkHandler {

    @Override
    public void handle(@NotNull HttpExchange exchange) {
        URI uri = exchange.getRequestURI();
        String fileName = uri.getPath().substring(uri.getPath().lastIndexOf('/') + 1);

        System.out.println("LogCommandHandler: [" + fileName + "]");

        if (fileName.equals("logs")) {
            handleRequest(new NetworkCommand("logs", new String[0], response -> {
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
            return;
        }

        String filePath = "./logs/" + fileName;

        File file = new File(filePath);
        try {
            if (file.exists() && file.isFile()) {
                exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=" + fileName);

                Headers headers = exchange.getResponseHeaders();
                headers.set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, file.length());

                OutputStream outputStream = exchange.getResponseBody();
                FileInputStream fileInputStream = new FileInputStream(file);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                fileInputStream.close();
                outputStream.close();
            } else {
                String errorJson = "{\"error\": \"file_not_found\"}";
                Headers headers = exchange.getResponseHeaders();
                headers.set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, errorJson.getBytes().length);

                OutputStream outputStream = exchange.getResponseBody();
                outputStream.write(errorJson.getBytes());
                outputStream.close();
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void handleRequest(NetworkCommand command) {
        // get all files in the logs folder (./logs)
        File logsFolder = new File("./logs");

        if (!logsFolder.exists()) {
            command.sendResponse().provide("[]");
            return;
        }

        JsonArray files = new JsonArray();
        try {
            for (File file : Objects.requireNonNull(logsFolder.listFiles())) {
                JsonObject fileObject = new JsonObject();
                fileObject.addProperty("name", file.getName());
                fileObject.addProperty("size", file.length());

                BasicFileAttributes attributes = Files.readAttributes(Path.of(file.getAbsolutePath()), BasicFileAttributes.class);
                FileTime creationTime = attributes.lastModifiedTime();
                long millis = creationTime.toMillis();

                fileObject.addProperty("date", millis);

                files.add(fileObject);
            }
        } catch (Exception ignored) {}

        command.sendResponse().provide(files.toString());
    }


}
