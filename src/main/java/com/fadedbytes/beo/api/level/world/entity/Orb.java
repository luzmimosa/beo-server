package com.fadedbytes.beo.api.level.world.entity;

import com.fadedbytes.beo.api.level.Level;
import com.fadedbytes.beo.api.level.world.space.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class Orb implements Entity {

    private Level level;
    private Vector location;

    /**
     * Sets the level that the orb is in.
     * @param level the level that the orb is in.
     * @throws IllegalStateException if the level has already been set.
     * @throws IllegalAccessException if the level is being set from a thread that is not the level thread.
     * @deprecated this method is not part of the API and should not be used.
     */
    public final void setLevel(Level level, Vector initialLocation) throws IllegalStateException, IllegalAccessException {
        if (this.level != null) {
            throw new IllegalStateException("Level already set!");
        }

        if (!Level.LEVEL_THREAD_GROUP.parentOf(Thread.currentThread().getThreadGroup())) {
            throw new IllegalAccessException("Level can only be set from the level thread!");
        }

        this.level = level;
        this.location = initialLocation;
    }

    protected final void move(Vector vector) {
        this.level.getSpaceFabric().moveObject(this, vector);
    }

    /**
     * Called every time the player takes a turn.
     * @param ticks the max number of ticks the turn can take before it is forced to end.
     * @param inputs an array of numbers the player must use in order to decide what to do.
     */
    public abstract void onTurn(int ticks, int[] inputs);

    /**
     * The stat map is an object that represents custom stats that the orb has. They should be defined by the player,
     * and will be shown in the App. You can add as many stats as you want, but they should be limited to integer values.
     * @return the stat map.
     */
    public abstract HashMap<String, Integer> getStatMap();

    /**
     * You should name your orb something cool. This is the name that will be shown in the App.
     * @return the name of the orb.
     */
    public abstract String getName();

    @Override
    public final double getMass() {
        return 1.0d;
    }

    @Override
    public final double getSize() {
        return 1.0d;
    }

    @Override
    public final double getVolume() {
        return 1.0d;
    }

    @Override
    public final double getDensity() {
        return this.getMass() / this.getVolume();
    }

    @Override
    public final @NotNull Vector getLocation() {
        return this.location.copy();
    }

    @Override
    public final @NotNull Vector getVelocity() {
        return new Vector(0.0, 0.0, 0.0);
    }

    @Override
    public final void setLocation(@NotNull Vector location) {
        this.location = location;
    }
}
