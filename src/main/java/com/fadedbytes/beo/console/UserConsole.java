package com.fadedbytes.beo.console;

import com.fadedbytes.beo.console.message.ConsoleMessage;
import com.fadedbytes.beo.util.key.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;

public final class UserConsole extends CommonConsole {

    public UserConsole(@NotNull NamespacedKey consoleKey, int historyLimit, OutputStream... outputStreams) {
        super(consoleKey, historyLimit, outputStreams);
    }

    @Override
    public void printMotd() {
        this.printMessage(null, new ConsoleMessage("Beo User Console"));
    }


}
