package com.fadedbytes.beo.log;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Supplier;

public abstract class BaseLogger implements BeoLogger {

    private final @NotNull HashMap<@NotNull LogSubscriber, @NotNull LogLevel> SUBSCRIBERS = new HashMap<>();

    @Override
    public void log(@NotNull LogLevel level, @NotNull String message) {
        log(level, () -> message);
    }

    @Override
    public void debug(@NotNull Supplier<String> messageSupplier) {
        this.log(LogLevel.DEBUG, messageSupplier);
    }

    @Override
    public void debug(@NotNull String message) {
        this.debug(() -> message);
    }

    @Override
    public void error(@NotNull Supplier<String> messageSupplier) {
        this.log(LogLevel.ERROR, messageSupplier);
    }

    @Override
    public void error(@NotNull String message) {
        this.error(() -> message);
    }

    @Override
    public void warn(@NotNull Supplier<String> messageSupplier) {
        this.log(LogLevel.WARN, messageSupplier);
    }

    @Override
    public void warn(@NotNull String message) {
        this.warn(() -> message);
    }

    @Override
    public void info(@NotNull Supplier<String> messageSupplier) {
        this.log(LogLevel.INFO, messageSupplier);
    }

    @Override
    public void info(@NotNull String message) {
        this.info(() -> message);
    }

    @Override
    public void event(@NotNull Supplier<String> messageSupplier) {
        this.log(LogLevel.EVENT, messageSupplier);
    }

    @Override
    public void event(@NotNull String message) {
        this.event(() -> message);
    }

    @Override
    public void trace(StackTraceElement[] elements) {
        for (StackTraceElement element : elements) {
            this.error(element::toString);
        }
    }

    @Override
    public void debugTrace(StackTraceElement[] elements) {
        for (StackTraceElement element : elements) {
            this.debug(element::toString);
        }
    }

    @Override
    public void subscribe(@NotNull LogLevel maxLevel, @NotNull LogSubscriber subscriber) {
        SUBSCRIBERS.put(subscriber, maxLevel);
    }

    protected @NotNull HashMap<@NotNull LogSubscriber, @NotNull LogLevel> getSubscribers() {
        return SUBSCRIBERS;
    }

    /**
     * Generates a string with the current timestamp in the format: "HH:mm:ss"
     * @return a string with the current timestamp
     */
    protected @NotNull String timestamp() {
        return "[" +
                String.format("%02d", System.currentTimeMillis() / 1000 / 60 / 60) + ":" +
                String.format("%02d", System.currentTimeMillis() / 1000 / 60 % 60) + ":" +
                String.format("%02d", System.currentTimeMillis() / 1000 % 60) +
                "]";
    }

    /**
     * Generates a string with the current timestamp in the format: "HH:mm:ss.SSS"
     * @return a string with the current timestamp
     */
    protected @NotNull String precisionTimestamp() {
        return "[" +
                String.format("%02d", System.currentTimeMillis() / 1000 / 60 / 60) + ":" +
                String.format("%02d", System.currentTimeMillis() / 1000 / 60 % 60) + ":" +
                String.format("%02d", System.currentTimeMillis() / 1000 % 60) + "." +
                String.format("%03d", System.currentTimeMillis() % 1000) +
                "]";
    }
}
