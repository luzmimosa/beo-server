package com.fadedbytes.beo.log;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Supplier;

public class SimpleLogger extends BaseLogger {
    @Override
    public void log(@NotNull LogLevel level, @NotNull Supplier<String> messageSupplier) {
        HashMap<LogSubscriber, LogLevel> currentSubscribers = this.getSubscribers();
        for (LogSubscriber subscriber : currentSubscribers.keySet()) {
            LogLevel subscriberLevel = currentSubscribers.get(subscriber);
            if (subscriberLevel.isAtLeast(level)) {
                subscriber.print(
                        (
                                subscriberLevel.equals(LogLevel.DEBUG) ?
                                        this.precisionTimestamp() :
                                        this.timestamp()
                        )
                                + "> " + messageSupplier.get()
                );
            }
        }
    }
}
