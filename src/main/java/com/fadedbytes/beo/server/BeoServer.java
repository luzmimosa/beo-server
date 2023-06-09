package com.fadedbytes.beo.server;

import com.fadedbytes.beo.api.exception.level.register.LevelAlreadyRunningException;
import com.fadedbytes.beo.api.exception.level.register.LevelNotRunningException;
import com.fadedbytes.beo.api.exception.level.register.UnregisteredLevelException;
import com.fadedbytes.beo.api.level.Level;
import com.fadedbytes.beo.console.BeoConsole;
import com.fadedbytes.beo.event.EventManager;
import com.fadedbytes.beo.log.BeoLogger;
import com.fadedbytes.beo.util.key.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface BeoServer {

    /**
     * Gets the namespaced key of this server. This key is used to identify the server.
     * @return The namespaced key of this server.
     */
    @NotNull NamespacedKey getServerKey();

    /**
     * @return the name of the server.
     */
    @NotNull String getName();

    /**
     * @return The version of the server.
     */
    @NotNull String getServerVersion();

    /**
     * @return The main logger of this server.
     */
    @NotNull BeoLogger getLogger();

    /**
     * @return The console of this server.
     */
    @NotNull BeoConsole getConsole();

    /**
     * @return The event manager of this server.
     */
    @NotNull EventManager getEventManager();

    /**
     * Links a level to this server. Linking a level to a server means that the level will be
     * registered to the server and will be able to be run by the server.
     * @param level The level to link to this server.
     * @throws IllegalArgumentException If the level is already linked to a server.
     */
    void registerLevel(@NotNull Level level) throws IllegalArgumentException;

    /**
     * Unlinks a level from this server. Unlinking a level from a server means that the level will
     * no longer be registered to the server and will not be able to be run by the server.
     * @param level The level to unlink from this server.
     */
    void removeLevel(@NotNull Level level);

    /**
     * @return The levels that are linked to this server.
     */
    @NotNull Set<Level> getRegisteredLevels();

    /**
     * Starts a level.
     * @param level The level to start.
     * @throws LevelAlreadyRunningException If the level is already running.
     * @throws UnregisteredLevelException If the level is not registered to this server.
     */
    void startLevel(@NotNull Level level) throws LevelAlreadyRunningException, UnregisteredLevelException;

    /**
     * Stops a level.
     * @param level The level to stop.
     * @throws LevelNotRunningException If the level is not running.
     * @throws UnregisteredLevelException If the level is not registered to this server.
     */
    void stopLevel(@NotNull Level level) throws LevelNotRunningException, UnregisteredLevelException;

    /**
     * Loads or reloads all levels from the disk.
     */
    void loadLevelsFromDisk();

    /**
     * Loads or reloads all orbs from the disk.
     */
    void loadOrbsFromDisk();

    /**
     * Joins the prepared orbs for a level.
     * @param level The level to join the prepared orbs for.
     */
    void requestOrbsFor(Level level);
}
