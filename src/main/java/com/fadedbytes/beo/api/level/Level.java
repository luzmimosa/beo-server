package com.fadedbytes.beo.api.level;

import com.fadedbytes.beo.api.level.world.entity.Orb;
import com.fadedbytes.beo.api.level.world.space.SpaceFabric;
import com.fadedbytes.beo.api.level.world.space.Vector;
import com.fadedbytes.beo.loader.OrbLoader;
import com.fadedbytes.beo.log.BeoLogger;
import com.fadedbytes.beo.server.BeoServer;
import com.fadedbytes.beo.stats.LevelStatResult;
import com.fadedbytes.beo.stats.LocalStatManager;
import com.fadedbytes.beo.stats.OrbStatResult;
import com.fadedbytes.beo.stats.TemporalOrbStats;
import com.fadedbytes.beo.util.key.NamespacedKey;
import com.fadedbytes.beo.util.management.freeze.FrozenObjectException;
import com.fadedbytes.beo.util.timer.TickTrigger;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class Level {
    // Level info
    public final NamespacedKey KEY;

    // Thread logic
    public static final ThreadGroup LEVEL_THREAD_GROUP = new ThreadGroup("beo-levels");
    private final ThreadGroup levelThreadGroup;
    private final Thread levelThread;


    // Level execution control
    private final LevelController controller;
    private final TickTrigger tickTrigger;
    private int turnCount = 0;


    // Parent holders
    private final @NotNull BeoServer server;


    // Level logic
    private final @NotNull SpaceFabric spaceFabric;
    private @NotNull HashMap<Orb, TemporalOrbStats> orbs;

    // Stats
    private final HashMap<Orb, HashMap<String, Integer>> temporalCustomStats = new HashMap<>();

    public Level(
            @NotNull String name,
            @NotNull BeoServer server,
            @NotNull SpaceFabric spaceFabric
    ) {
        this.KEY = new NamespacedKey(server.getServerKey().getNamespace(), name);

        this.server = server;
        this.spaceFabric = spaceFabric;

        this.orbs = new HashMap<>(this.getMaxPlayers());
        this.controller = new LevelController();

        this.levelThreadGroup = new ThreadGroup(LEVEL_THREAD_GROUP, this.getLevelName() + " thread group");
        this.levelThread = new Thread(this.levelThreadGroup, this::runLevel);

        this.tickTrigger = new TickTrigger(
                this.getLevelTickRate(),
                this.getLevelTickRate() * 2L,
                (Long ticksBehind) -> { this.getLogger().warn(
                        String.format(
                                "Can't keep up: level %s is running %s ticks behind. Is the server overloaded?",
                                this.getLevelName(),
                                ticksBehind.toString()
                        )
                ); }
        );

        registerLevel();
    }


    // Register methods
    private void registerLevel() {
        this.server.registerLevel(this);
    }


    // Level info getters
    public final boolean isRunning() {
        return this.controller.isRunning();
    }

    public final boolean didFinish() {
        return this.controller.status == LevelController.RunStatus.FINISHED;
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
        this.stopSequence();
    }
    private void startingSequence() {
        this.server.requestOrbsFor(this);
        joinOrbs();

        this.onLevelStartSequence();
    }
    private void startLoop() {
        this.controller.running();
        this.tickTrigger.start();
        while (this.controller.shouldRun()) {
            if (this.tickTrigger.tickRequest()) {
                if (shouldDoNextTurn(turnCount++)) {
                    getLogger().info("Starting turn " + turnCount + " of level " + this.getLevelName());
                    for (Orb orb : this.getOrbs()) {
                        this.orbs.get(orb).turns++;
                        long startTime = System.nanoTime();
                        this.onTurnOf(orb);
                        long endTime = System.nanoTime();
                        this.orbs.get(orb).time += (endTime - startTime);
                    }
                } else {
                    return;
                }
            }
        }
    }

    public void forceStop() {
        this.stopSequence();
    }


    // Pre-start methods
    public synchronized void addOrb(@NotNull Orb newOrb) {
        if (this.orbs.size() >= this.getMaxPlayers()) {
            return;
        }
        this.orbs.put(newOrb, new TemporalOrbStats());
        this.temporalCustomStats.put(newOrb, new HashMap<>());

//        new Thread(LEVEL_THREAD_GROUP, () -> {
//            try {
//                newOrb.setLevel(this);
//            } catch (IllegalAccessException e) {
//                this.server.getLogger().error("An error occurred while setting the level of an orb: " + e.getMessage(), e.getStackTrace());
//            }
//        }).start();

        try {
            newOrb.setLevel(this, this.initialOrbLocation());
        } catch (IllegalAccessException e) {
            this.server.getLogger().error("An error occurred while setting the level of an orb: " + e.getMessage(), e.getStackTrace());
        }

    }
    private void joinOrbs() {
        for (Orb orb : this.getOrbs()) {
            try {
                this.spaceFabric.joinSpaceObject(orb);
            } catch (FrozenObjectException e) {
                this.server.getLogger().error("An error occurred while joining an orb to the space fabric", e.getStackTrace());
            }
        }
    }


    // Implementation getters
    public final BeoLogger getLogger() {
        return this.server.getLogger();
    }
    public @NotNull SpaceFabric getSpaceFabric() {
        return spaceFabric;
    }


    private void stopSequence() {
        this.server.getLogger().debug("Calling controller.finish");
        this.controller.finish();

        this.server.getLogger().debug("Calling tickTrigger.stop");
        this.tickTrigger.stop();

        this.server.getLogger().debug("Calling onLevelFinishSequence");
        this.onLevelFinishSequence();

        this.registerOrbStats();
    }

    private void registerOrbStats() {
        List<OrbStatResult> orbStatResults = new ArrayList<>();
        for (Orb orb : this.getOrbs()) {
            HashMap<String, Integer> customStats = orb.getStatMap();
            for (String key : customStats.keySet()) {
                this.temporalCustomStats.get(orb).put(key, customStats.get(key));
            }

            HashMap<String, Integer> levelCustomizedStats = getLevelCustomStatsFor(orb);
            for (String key : levelCustomizedStats.keySet()) {
                this.temporalCustomStats.get(orb).put(key, levelCustomizedStats.get(key));
            }

            customStats.put("turns", this.orbs.get(orb).turns);
            customStats.put("time", (int) (this.orbs.get(orb).time / 1000000));

            orbStatResults.add(new OrbStatResult(orb.getName(), customStats));
        }


        LocalStatManager.getInstance()
                .saveLevelStats(
                        new LevelStatResult(
                                this.KEY,
                                orbStatResults.toArray(new OrbStatResult[0]),
                                System.currentTimeMillis()
                        )
                );
    }

    private Set<Orb> getOrbs() {
        return this.orbs.keySet();
    }

    /**
     * Set a custom stat for an orb.
     * @param orb The orb whose stat should be set
     * @param key The key of the stat
     * @param value The value of the stat
     */
    protected final void setOrbStat(Orb orb, String key, int value) {
        this.temporalCustomStats.get(orb).put(key, value);
    }


    // Abstract methods for level implementation

    /**
     * Define here the maximum number of orbs that can join the level
     * @return The maximum number of orbs that can join the level
     */
    public abstract int getMaxPlayers();

    /**
     * Put here the code that should be executed before the level starts
     */
    protected abstract void onLevelStartSequence();

    /**
     * This method is called each time the turn of an orb is triggered. <b>it should call the method Orb.onTurn()</b>.
     * @param orb The orb whose turn is triggered
     */
    protected abstract void onTurnOf(Orb orb);

    /**
     * Define here the limit of turns per second that the level should do. The default value is 1 turn per second.
     * @return The tick rate of the level
     */
    protected int getLevelTickRate() {
        return 1;
    }

    /**
     * Define here the name of your level
     * @return The name of your level
     */
    protected abstract @NotNull String getLevelName();

    /**
     * Put here the code that should be executed when the level finishes
     */
    protected abstract void onLevelFinishSequence();

    /**
     * This function will be called each time the level should do a new cycle of turns for all the orbs. If you
     * want to stop the level, based on a condition, you should return false. Otherwise, return true.
     * @param turnNumber The number of the turns that have been executed before this one.
     * @return True if the level should do a new cycle of turns, false otherwise
     */
    protected abstract boolean shouldDoNextTurn(int turnNumber);

    /**
     * You can define here the custom stats that should be saved for each orb. They will be merged with the orb defined ones
     * and the default ones (turns and time).
     * @param orb The orb whose stats should be saved
     * @return A map of custom stats for the orb
     */
    protected HashMap<String, Integer> getLevelCustomStatsFor(Orb orb) {
        return new HashMap<>();
    }

    /**
     * You can change the initial location of the orbs here. The default location is (0, 0, 0).
     * @return
     */
    protected Vector initialOrbLocation() {
        return new Vector(0.0, 0.0, 0.0);
    }

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
            if (this.status != RunStatus.RUNNING && this.status != RunStatus.STARTING) {
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

        public boolean isRunning() {
            return this.status != RunStatus.WAITING && this.status != RunStatus.FINISHED;
        }

        public enum RunStatus {
            WAITING,
            STARTING,
            RUNNING,
            FINISHED
        }
    }

}
