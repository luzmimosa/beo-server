package com.fadedbytes.beo.console;

import com.fadedbytes.beo.console.message.ConsoleMessage;
import com.fadedbytes.beo.util.key.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;

public final class ServerConsole extends CommonConsole {

    public ServerConsole(@NotNull NamespacedKey consoleKey, int historyLimit, OutputStream... outputStreams) {
        super(consoleKey, historyLimit, outputStreams);
    }

    @Override
    public void printMotd() {
        this.printMessage(null, new ConsoleMessage("Welcome to the Beo Server Console!"));
    }
}
