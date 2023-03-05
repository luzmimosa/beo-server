package com.fadedbytes.beo.log;

import com.fadedbytes.beo.util.key.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class LogManager {

    public static final NamespacedKey MAIN_LOGGER_KEY = new NamespacedKey(NamespacedKey.BEO_NAMESPACE, "main_logger");

    private HashMap<NamespacedKey, BeoLogger> loggers = new HashMap<>();

    public LogManager() {
        createMainLogger();
    }

    private void createMainLogger() {
        if (!this.loggers.containsKey(MAIN_LOGGER_KEY)) {
            this.loggers.put(
                MAIN_LOGGER_KEY,
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

}
