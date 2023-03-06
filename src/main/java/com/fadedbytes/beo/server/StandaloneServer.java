package com.fadedbytes.beo.server;

import com.fadedbytes.beo.console.BeoConsole;
import com.fadedbytes.beo.log.BeoLogger;
import com.fadedbytes.beo.util.key.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StandaloneServer implements BeoServer {

    private final @NotNull NamespacedKey key;
    private final @NotNull BeoLogger logger;
    private final @NotNull BeoConsole console;

    private StandaloneServer(
            @NotNull NamespacedKey serverName,
            @NotNull BeoLogger logger,
            @NotNull BeoConsole console
    ) {
        this.key = serverName;
        this.logger = logger;
        this.console = console;

        this.logger.event("Server instance deployed successfully: " + this.key);
    }

    @Override
    public @NotNull NamespacedKey getServerKey() {
        return this.key;
    }

    @Override
    public @NotNull String getName() {
        return this.key.getKey();
    }

    @Override
    public @NotNull BeoLogger getLogger() {
        return this.logger;
    }

    @Override
    public @NotNull BeoConsole getConsole() {
        return this.console;
    }

    public static class Builder {

        private @Nullable String serverName;
        private @Nullable BeoLogger logger;
        private @Nullable BeoConsole console;

        public Builder() {}

        /**
         * Sets the name of the server.
         * @param serverName The name of the server.
         * @return the builder, for chaining.
         * @throws IllegalArgumentException if the name does not match the {@link NamespacedKey#REGEX NamespacedKey's regex} format.
         */
        public Builder name(@NotNull String serverName) throws IllegalArgumentException {

            if (!serverName.matches(NamespacedKey.REGEX)) {
                throw new IllegalArgumentException("Server name must match the following regex: " + NamespacedKey.REGEX);
            }

            this.serverName = serverName;
            return this;
        }

        /**
         * @return the current name of the server of the builder.
         */
        public @Nullable String name() {
            return this.serverName;
        }

        /**
         * Sets the logger of the server.
         * @param logger The logger of the server.
         * @return the builder, for chaining.
         */
        public Builder logger(@NotNull BeoLogger logger) {
            this.logger = logger;
            return this;
        }

        /**
         * @return the current logger of the server of the builder.
         */
        public @Nullable BeoLogger logger() {
            return this.logger;
        }

        /**
         * Sets the console of the server.
         * @param console The console of the server.
         * @return the builder, for chaining.
         */
        public Builder console(@NotNull BeoConsole console) {
            this.console = console;
            return this;
        }

        /**
         * @return the current console of the server of the builder.
         */
        public @Nullable BeoConsole console() {
            return this.console;
        }

        /**
         * Builds the server with the current configuration of the builder. <br/>
         * If the server name is not set, it will rise an {@link IllegalStateException}. <br/>
         * If the logger is not set, it will rise an {@link IllegalStateException}. <br/>
         * If the console is not set, it will rise an {@link IllegalStateException}.
         * @return the server with the current configuration of the builder.
         */
        public @NotNull StandaloneServer build() {

            assert this.serverName != null;
            assert this.logger != null;
            assert this.console != null;

            return new StandaloneServer(
                    NamespacedKey.of(this.serverName),
                    this.logger,
                    this.console
            );
        }
    }

}
