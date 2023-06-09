package com.fadedbytes.beo.dev;

import com.fadedbytes.beo.api.level.Level;
import com.fadedbytes.beo.api.level.world.entity.Orb;
import com.fadedbytes.beo.api.level.world.space.SpaceFabric;
import com.fadedbytes.beo.server.BeoServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TestLevel extends Level {

    int tickNumber = 0;

    public TestLevel(@NotNull String name, @NotNull BeoServer server, @NotNull SpaceFabric spaceFabric) {
        super(name, server, spaceFabric);
    }

    @Override
    public @Range(from = 1, to = Integer.MAX_VALUE) int getMaxPlayers() {
        return 2;
    }

    @Override
    protected void onLevelStartSequence() {
        this.getLogger().debug("Executing onLevelStartSequence");
    }

    @Override
    protected void onTurnOf(Orb orb) {
        if (++tickNumber % 1000 == 0) {
            this.getLogger().debug("Tick number: " + this.tickNumber);
            this.getLogger().debug(LocalTime.now().format(DateTimeFormatter.ofPattern("ss.SSS")));
        }
    }

    @Override
    protected int getLevelTickRate() {
        return 1000;
    }

    @Override
    protected @NotNull String getLevelName() {
        return "Test Level";
    }

    @Override
    protected void onLevelFinishSequence() {

    }

    @Override
    protected boolean shouldDoNextTurn(int turnNumber) {
        return true;
    }

}
