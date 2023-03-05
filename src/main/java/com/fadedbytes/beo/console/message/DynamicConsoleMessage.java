package com.fadedbytes.beo.console.message;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DynamicConsoleMessage extends ConsoleMessage {

    private final @NotNull Supplier<@NotNull String> messageSupplier;
    public DynamicConsoleMessage(@NotNull Supplier<@NotNull String> messageSupplier) {
        super(messageSupplier.get());
        this.messageSupplier = messageSupplier;
    }

    @Override
    public @NotNull String getMessage() {
        return this.messageSupplier.get();
    }
}
