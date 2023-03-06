package com.fadedbytes.beo.console.message;

import com.fadedbytes.beo.console.ConsoleColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ConsoleMessage {

    private static final String PRESET =
            "\u200E %messageColor%■ " + ConsoleColor.RESET + ConsoleColor.BOLD + "[" + ConsoleColor.CYAN + "%timestamp%" + ConsoleColor.RESET + ConsoleColor.BOLD + "] " + ConsoleColor.RESET +
            ConsoleColor.BRIGHT_BLACK + ConsoleColor.BOLD + "[" + ConsoleColor.RESET + ConsoleColor.BRIGHT_BLACK + "%origin%" + ConsoleColor.BOLD + "]" + ConsoleColor.RESET + ConsoleColor.CYAN + ": " + ConsoleColor.RESET +
            "%messageColor%%message%";
    
    private final @NotNull String message;
    private final @NotNull LocalDateTime timestamp;
    private final @NotNull String color;
    
    public ConsoleMessage(@NotNull String message, @Nullable String messageColor) {
        this.message = message;
        this.timestamp = LocalDateTime.now();

        this.color = Objects.requireNonNullElse(messageColor, ConsoleColor.BRIGHT_BLUE);
    }

    public ConsoleMessage(@NotNull String message) {
        this(message, null);
    }
    
    public @NotNull String getMessage() {
        return message;
    }

    public @NotNull LocalDateTime getTimestamp() {
        return timestamp;
    }

    public @NotNull String format(@Nullable String origin) {
        return PRESET
                .replace("%timestamp%", this.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                .replace("%origin%", origin == null ? "" : origin)
                .replace("%messageColor%", this.color)
                .replace("%message%", this.getMessage());
    }

    public @NotNull String format() {
        return this.format(null);
    }

    public static @NotNull ConsoleMessage of(@NotNull String messageSupplier) {
        return new ConsoleMessage(messageSupplier);
    }

    public static @NotNull ConsoleMessage of(@NotNull String messageSupplier, @Nullable String messageColor) {
        return new ConsoleMessage(messageSupplier, messageColor);
    }


}
