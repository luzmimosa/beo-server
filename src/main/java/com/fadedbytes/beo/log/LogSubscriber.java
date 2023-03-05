package com.fadedbytes.beo.log;

import org.jetbrains.annotations.NotNull;

/**
 * Log subscribers can be registered with the {@link BeoLogger} class {@link BeoLogger#subscribe(LogLevel, LogSubscriber)} method to receive logs.
 */
public interface LogSubscriber {

    /**
     * Asks the subscriber to process the log message.
     * @param level the level of the message
     * @param message The message to process.
     */
    void processLog(@NotNull LogLevel level, @NotNull String message);

}
