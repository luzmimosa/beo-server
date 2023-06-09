package com.fadedbytes.beo.network.server.handler.command;

import com.fadedbytes.beo.network.server.handler.NetworkCommand;
import com.fadedbytes.beo.network.server.handler.NetworkHandler;
import com.fadedbytes.beo.stats.JvmStatProvider;

public class JvmCommandHandler implements NetworkHandler {
    @Override
    public void handleRequest(NetworkCommand command) {
        if (command.command().equals("jvm")) {
            command.sendResponse().provide(JvmStatProvider.getInstance().generateCurrentJsonString());
        }
    }
}
