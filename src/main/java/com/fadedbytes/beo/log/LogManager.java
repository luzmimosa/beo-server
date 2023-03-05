package com.fadedbytes.beo.log;

import com.fadedbytes.beo.util.key.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class LogManager {

    public static final NamespacedKey MAIN_LOGGER_KEY = new NamespacedKey(NamespacedKey.BEO_NAMESPACE, "main_logger");
    public static final NamespacedKey EVENT_LOGGER_KEY = new NamespacedKey(NamespacedKey.BEO_NAMESPACE, "event_logger");

    private final HashMap<NamespacedKey, BeoLogger> loggers = new HashMap<>();

    public LogManager() {
        createMainLogger();
        createEventLogger();
    }

    private void createMainLogger() {
        if (!this.loggers.containsKey(MAIN_LOGGER_KEY)) {
            this.loggers.put(
                MAIN_LOGGER_KEY,
                new SimpleLogger()
            );
        }
    }

    private void createEventLogger() {
        if (!this.loggers.containsKey(EVENT_LOGGER_KEY)) {
            this.loggers.put(
                EVENT_LOGGER_KEY,
                new SimpleLogger()
            );
        }
    }

    public @Nullable BeoLogger getLogger(@NotNull NamespacedKey key) {
        return this.loggers.get(key);
    }

    public @NotNull BeoLogger getMainLogger() {
        if (!this.loggers.containsKey(MAIN_LOGGER_KEY)) {
            createMainLogger();
        }

        return this.loggers.get(MAIN_LOGGER_KEY);
    }

    public @NotNull BeoLogger getEventLogger() {
        if (!this.loggers.containsKey(EVENT_LOGGER_KEY)) {
            createEventLogger();
        }

        return this.loggers.get(EVENT_LOGGER_KEY);
    }

}
