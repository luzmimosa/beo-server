package com.fadedbytes.beo.api.level;

import com.fadedbytes.beo.api.level.world.entity.Entity;
import com.fadedbytes.beo.api.level.world.entity.Orb;
import org.jetbrains.annotations.NotNull;

public interface LevelPlayer extends Entity {

    /**
     * @return the orb that the player submitted to the level.
     */
    @NotNull Orb getOrb();

}
