package com.fadedbytes.beo.network.server.handler;

import com.fadedbytes.beo.util.data.Provider;

public record NetworkCommand(
        String command,
        // Token
        String[] args,
        Provider<String> sendResponse
) {

    public void sendSuccess(String message) {
        sendResponse.provide(String.format("{\"success\": \"%s\"}", message));
    }

    public void sendSuccess() {
        sendResponse.provide("{\"success\": \"success\"}");
    }

    public void sendError(CommandErrorCode code) {
        sendResponse.provide(String.format("{\"error\": \"%s\"}", code.code));
    }

}
