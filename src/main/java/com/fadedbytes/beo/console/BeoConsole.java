package com.fadedbytes.beo.console;

import com.fadedbytes.beo.console.message.ConsoleMessage;
import com.fadedbytes.beo.log.BeoLogger;
import com.fadedbytes.beo.log.LogLevel;
import com.fadedbytes.beo.log.LogSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.util.Set;

/**
 * The BeoConsole interface represents a console that can be used by users to interact with the server.
 * The users should be able to, at least, read messages from the console.
 */
public sealed interface BeoConsole
        extends LogSubscriber, AutoCloseable
        permits CommonConsole
{

    /**
     * Starts the console. This method should be called before any other method.
     */
    void startConsole();

    /**
     * Prints a message to the console.
     * @param origin The origin of the message, such as a logger name, or a thread name.
     * @param message The message to print.
     */
    void printMessage(@Nullable String origin, @NotNull ConsoleMessage message);

    /**
     * Prints the message of the day to the console.
     */
    void printMotd();

    /**
     * @return the max amount of messages that can be stored in the console history.
     */
    int historyLimit();

    /**
     * Binds this console to a logger, so that any messages logged by the logger will be printed to the console.
     * @param logger The logger to bind.
     * @param level The minimum level of messages to print.
     */
    void bindLogger(@NotNull BeoLogger logger, LogLevel level);

    /**
     * Unbinds this console from a logger.
     * @param logger The logger to unbind.
     */
    void unbindLogger(@NotNull BeoLogger logger);

    /**
     * Adds an output stream to the console, so that any messages printed to the console will also be printed to the stream.
     * @param outputStream The output stream to add.
     */
    void addOutputStream(@NotNull OutputStream outputStream);

    /**
     * Removes an output stream from the console.
     * @param outputStream The output stream to remove.
     */
    void removeOutputStream(@NotNull OutputStream outputStream);

    /**
     * @return the set of loggers that are bound to this console.
     */
    Set<OutputStream> getOutputStreams();

}
