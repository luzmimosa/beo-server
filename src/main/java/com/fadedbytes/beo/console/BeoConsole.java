package com.fadedbytes.beo.console;

import com.fadedbytes.beo.console.message.ConsoleMessage;
import com.fadedbytes.beo.log.BeoLogger;
import com.fadedbytes.beo.log.LogLevel;
import com.fadedbytes.beo.log.LogSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.util.Set;

public sealed interface BeoConsole
        extends LogSubscriber
        permits CommonConsole
{

    void startConsole();

    void closeConsole();

    void printMessage(@Nullable String origin, @NotNull ConsoleMessage message);

    void printMotd();

    int historyLimit();

    void bindLogger(@NotNull BeoLogger logger, LogLevel level);

    void unbindLogger(@NotNull BeoLogger logger);

    void addOutputStream(@NotNull OutputStream outputStream);

    void removeOutputStream(@NotNull OutputStream outputStream);

    Set<OutputStream> getOutputStreams();

}
