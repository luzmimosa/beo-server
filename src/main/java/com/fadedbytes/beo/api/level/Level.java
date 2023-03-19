package com.fadedbytes.beo.api.level;

import com.fadedbytes.beo.api.level.world.space.SpaceFabric;
import com.fadedbytes.beo.log.BeoLogger;
import com.fadedbytes.beo.server.BeoServer;
import com.fadedbytes.beo.util.management.freeze.FrozenObjectException;
import com.fadedbytes.beo.util.timer.Clock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.HashSet;
import java.util.Set;

public abstract class Level {

    // Thread logic
    private final ThreadGroup levelThreadGroup;
    private final Thread levelThread;


    // Level execution control
    private final LevelController controller;


    // Parent holders
    private final @NotNull BeoServer server;


    // Level logic
    private final @NotNull SpaceFabric spaceFabric;
    private @NotNull Set<LevelPlayer> players;



    public Level(
            @NotNull BeoServer server,
            @NotNull SpaceFabric spaceFabric
    ) {
        this.server = server;
        this.spaceFabric = spaceFabric;

        this.players = new HashSet<>(this.getMaxPlayers());
        this.controller = new LevelController();

        this.levelThreadGroup = new ThreadGroup("");
        this.levelThread = new Thread(this.levelThreadGroup, this::runLevel);
    }


    // Level lifecycle methods
    public synchronized void runLevel() throws IllegalStateException {

        if (!this.controller.canStart()) {
            throw new IllegalStateException("Level is already started");
        }
        if (!Thread.currentThread().equals(this.levelThread)) {
            this.levelThread.start();
            return;
        }

        this.controller.starting();
        startingSequence();
        startLoop();
    }
    private void startingSequence() {
        joinOrbs();

        this.onLevelStartSequence();
    }
    private void startLoop() {
        this.controller.running();
        while (this.controller.shouldRun()) {
            this.levelTick();
        }
    }


    // Pre-start methods
    public synchronized void addPlayer(@NotNull LevelPlayer newPlayer) {
        this.players.add(newPlayer);
    }
    private void joinOrbs() {
        for (LevelPlayer player : this.players) {
            try {
                this.spaceFabric.joinSpaceObject(player.getOrb());
            } catch (FrozenObjectException e) {
                this.server.getLogger().error("An error occurred while joining a player's orb to the space fabric", e.getStackTrace());
            }
        }
    }


    // Implementation getters
    protected final BeoLogger getLogger() {
        return this.server.getLogger();
    }
    protected @NotNull SpaceFabric getSpaceFabric() {
        return spaceFabric;
    }


    // Abstract methods for level implementation
    public abstract int getMaxPlayers();
    protected abstract void onLevelStartSequence();
    protected abstract void levelTick();



    private static class LevelController {

        private @NotNull RunStatus status;

        public LevelController() {
            this.status = RunStatus.WAITING;
        }

        public @NotNull RunStatus getStatus() {
            return status;
        }

        public void starting() {
            if (this.status != RunStatus.WAITING) {
                throw new IllegalStateException("Level is already started");
            }
            this.status = RunStatus.STARTING;
        }

        public void running() {
            if (this.status != RunStatus.STARTING) {
                throw new IllegalStateException("Level is not starting");
            }
            this.status = RunStatus.RUNNING;
        }

        public void finish() {
            if (this.status != RunStatus.RUNNING) {
                throw new IllegalStateException("Level is not running");
            }
            this.status = RunStatus.FINISHED;
        }

        public boolean canStart() {
            return this.status == RunStatus.WAITING;
        }

        public boolean shouldRun() {
            return this.status == RunStatus.RUNNING;
        }


        public enum RunStatus {
            WAITING,
            STARTING,
            RUNNING,
            FINISHED
        }
    }

}
