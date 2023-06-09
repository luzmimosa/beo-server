package com.fadedbytes.beo.server;

import com.fadedbytes.beo.api.exception.level.register.LevelAlreadyRunningException;
import com.fadedbytes.beo.api.exception.level.register.LevelNotRunningException;
import com.fadedbytes.beo.api.exception.level.register.UnregisteredLevelException;
import com.fadedbytes.beo.api.level.Level;
import com.fadedbytes.beo.console.BeoConsole;
import com.fadedbytes.beo.event.EventManager;
import com.fadedbytes.beo.event.type.control.ServerStartupEvent;
import com.fadedbytes.beo.loader.LevelLoader;
import com.fadedbytes.beo.loader.OrbLoader;
import com.fadedbytes.beo.log.BeoLogger;
import com.fadedbytes.beo.util.key.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StandaloneServer implements BeoServer {

    private static final String levelFolderPath = "./levels";
    private static final String orbFolderPath = "./orbs";

    private final @NotNull NamespacedKey key;
    private final @NotNull String version;
    private final @NotNull BeoLogger logger;
    private final @NotNull BeoConsole console;
    private final @NotNull EventManager eventManager;
    private @NotNull ArrayList<Level> levels;
    private final @NotNull LevelLoader levelLoader;
    private final @NotNull OrbLoader orbLoader;

    private StandaloneServer(
            @NotNull NamespacedKey serverName,
            @NotNull String displayVersion,
            @NotNull BeoLogger logger,
            @NotNull BeoConsole console,
            @NotNull EventManager eventManager
    ) {
        this.key = serverName;
        this.version = displayVersion;
        this.logger = logger;
        this.console = console;
        this.eventManager = eventManager;
        this.levels = new ArrayList<>();

        this.levelLoader = new LevelLoader(levelFolderPath, this);
        this.orbLoader = new OrbLoader(orbFolderPath, this);

        new ServerStartupEvent(this).launch();

        this.loadLevelsFromDisk();
        this.loadOrbsFromDisk();
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
    public @NotNull String getServerVersion() {
        return this.version;
    }

    @Override
    public @NotNull BeoLogger getLogger() {
        return this.logger;
    }

    @Override
    public @NotNull BeoConsole getConsole() {
        return this.console;
    }

    @Override
    public @NotNull EventManager getEventManager() {
        return this.eventManager;
    }

    @Override
    public void registerLevel(@NotNull Level level) throws IllegalArgumentException {
        this.levels.add(level);
        this.getLogger().info("Registered level " + level.KEY.getKey());
    }

    @Override
    public void removeLevel(@NotNull Level level) {
        this.levels.remove(level);
        this.getLogger().info("Removed level " + level.KEY.getKey());
    }

    @Override
    public @NotNull Set<Level> getRegisteredLevels() {
        return Set.copyOf(this.levels);
    }

    @Override
    public void startLevel(@NotNull Level level) throws LevelAlreadyRunningException, UnregisteredLevelException {
        this.getLogger().info("Requesting to start level " + level.KEY.getKey() + "...");
        if (!this.levels.contains(level)) {
            this.getLogger().error("Level " + level.KEY.getKey() + " is not registered!");
            throw new UnregisteredLevelException(level);
        }
        if (level.isRunning()) {
            this.getLogger().error("Level " + level.KEY.getKey() + " is already running!");
            throw new LevelAlreadyRunningException(level);
        }
        level.runLevel();
        this.getLogger().info("Level " + level.KEY.getKey() + " started!");
    }

    @Override
    public void stopLevel(@NotNull Level level) throws LevelNotRunningException, UnregisteredLevelException {
        this.getLogger().info("Requesting to stop level " + level.KEY.getKey() + "...");

        if (!this.levels.contains(level)) {
            this.getLogger().error("Level " + level.KEY.getKey() + " is not registered!");
            throw new UnregisteredLevelException(level);
        }
        if (!level.isRunning()) {
            this.getLogger().error("Level " + level.KEY.getKey() + " is not running!");
            throw new LevelNotRunningException(level);
        }

        this.getLogger().debug("calling level.forceStop()...");
        level.forceStop();
        this.getLogger().info("Level " + level.KEY.getKey() + " stopped!");
    }

    @Override
    public void loadLevelsFromDisk() {
        this.getLogger().info("Loading levels from " + levelLoader.getFolderPath() + "...");

        this.levels.clear();

        List<Level> levels = levelLoader.loadLevels();

        this.getLogger().info("Loaded " + levels.size() + " levels from " + levelLoader.getFolderPath());

        for (Level level : levels) {
            this.registerLevel(level);
        }

        this.getLogger().info("Registered " + levels.size() + " levels");
    }

    @Override
    public void loadOrbsFromDisk() {
        this.getLogger().info("Loading orbs from " + orbLoader.getFolderPath() + "...");
        this.orbLoader.loadOrbs();
        this.getLogger().info("Orbs loaded from " + orbLoader.getFolderPath());
    }

    @Override
    public void requestOrbsFor(Level level) {
        this.orbLoader.joinOrbsForLevel(level);
    }

    public static class Builder {

        private @Nullable String serverName;
        private @Nullable String displayVersion;
        private @Nullable BeoLogger logger;
        private @Nullable BeoConsole console;
        private @Nullable EventManager eventManager;

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
         * Sets the event manager of the server.
         * @param eventManager The event manager of the server.
         * @return the builder, for chaining.
         */
        public Builder eventManager(@NotNull EventManager eventManager) {
            this.eventManager = eventManager;
            return this;
        }

        /**
         * @return the current event manager of the server of the builder.
         */
        public @Nullable EventManager eventManager() {
            return this.eventManager;
        }

        /**
         * Sets the version of the server.
         * @param version The version of the server.
         * @return the builder, for chaining.
         */
        public Builder version(@NotNull String version) {
            this.displayVersion = version;
            return this;
        }

        /**
         * @return the current version of the server of the builder.
         */
        public @Nullable String version() {
            return null;
        }

        /**
         * Builds the server with the current configuration of the builder. <br/>
         * If the server name is not set, it will rise an {@link IllegalStateException}. <br/>
         * If the logger is not set, it will rise an {@link IllegalStateException}. <br/>
         * If the console is not set, it will rise an {@link IllegalStateException}.
         * If the event manager is not set, it will rise an {@link IllegalStateException}.
         * @return the server with the current configuration of the builder.
         */
        public @NotNull StandaloneServer build() {

            assert this.serverName != null;
            assert this.displayVersion != null;
            assert this.logger != null;
            assert this.console != null;
            assert this.eventManager != null;

            return new StandaloneServer(
                NamespacedKey.of(this.serverName),
                this.displayVersion,
                this.logger,
                this.console,
                this.eventManager
            );
        }
    }

}
