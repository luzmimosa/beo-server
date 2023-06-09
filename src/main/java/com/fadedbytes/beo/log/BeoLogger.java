package com.fadedbytes.beo.log;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public interface BeoLogger {
    /**
     * Logs a message with the given level. The message is generated by the given supplier.
     * @param level The level of the message.
     * @param messageSupplier The supplier of the message.
     */
    void log(@NotNull LogLevel level, @NotNull Supplier<@NotNull String> messageSupplier);

    /**
     * Logs a given message with the given level.
     * @param level The level of the message.
     * @param message The message to log.
     */
    void log(@NotNull LogLevel level, @NotNull String message);

    /**
     * Logs a system message.
     * @param message The message to log.
     */
    void system(@NotNull String message);

    /**
     * Logs a message as debug level. The message is generated by the given supplier.
     * @param messageSupplier The supplier of the message.
     */
    void debug(@NotNull Supplier<@NotNull String> messageSupplier);

    /**
     * Logs a given message as debug level.
     * @param message The message to log.
     */
    void debug(@NotNull String message);

    /**
     * Logs a message as info level. The message is generated by the given supplier.
     * @param messageSupplier The supplier of the message.
     */
    void error(@NotNull Supplier<@NotNull String> messageSupplier);

    /**
     * Logs a given message as info level.
     * @param message The message to log.
     */
    void error(@NotNull String message);

    /**
     * Logs an error message and the specified stack trace
     * @param message The message to log.
     * @param elements The stack trace elements.
     */
    default void error(@NotNull String message, @NotNull StackTraceElement[] elements) {
        this.error(message);
        this.trace(elements);
    }

    /**
     * Logs a message as warning level. The message is generated by the given supplier.
     * @param messageSupplier The supplier of the message.
     */
    void warn(@NotNull Supplier<@NotNull String> messageSupplier);

    /**
     * Logs a given message as warning level.
     * @param message The message to log.
     */
    void warn(@NotNull String message);

    /**
     * Logs a message as info level. The message is generated by the given supplier.
     * @param messageSupplier The supplier of the message.
     */
    void info(@NotNull Supplier<@NotNull String> messageSupplier);

    /**
     * Logs a given message as info level.
     * @param message The message to log.
     */
    void info(@NotNull String message);

    /**
     * Logs a message as event level. The message is generated by the given supplier.
     * @param messageSupplier The supplier of the message.
     */
    void event(@NotNull Supplier<@NotNull String> messageSupplier);

    /**
     * Logs a given message as event level.
     * @param message The message to log.
     */
    void event(@NotNull String message);

    /**
     * Logs the given stack trace as error level.
     * @param elements The stack trace elements to log.
     */
    void trace(@NotNull StackTraceElement[] elements);

    /**
     * Logs the given stack trace as debug level.
     * @param elements The stack trace elements to log.
     */
    void debugTrace(@NotNull StackTraceElement[] elements);

    /**
     * Subscribes the given method to this logger. The method will be called when a message with lower or equal level is logged.
     * @param maxLevel The maximum level of the message to subscribe to.
     * @param subscriber The subscriber to call.
     */
    void subscribe(@NotNull LogLevel maxLevel, @NotNull LogSubscriber subscriber);

    /**
     * Unsubscribes the given method from this logger.
     * @param subscriber The subscriber to unsubscribe.
     */
    void unsubscribe(@NotNull LogSubscriber subscriber);

}
