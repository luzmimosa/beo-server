package com.fadedbytes.beo.api.level.world.space;

import com.fadedbytes.beo.util.data.Filter;
import com.fadedbytes.beo.util.management.freeze.Freezable;
import com.fadedbytes.beo.util.management.freeze.FrozenObjectException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class SpaceFabric implements Freezable {

    private boolean frozen;

    private final ArrayList<SpaceObject> objects = new ArrayList<>();
    private final int dimensionCount;

    public SpaceFabric(
            int dimensionCount
    ) {
        if (dimensionCount < 1) {
            throw new IllegalArgumentException("Dimension count must be greater than 0");
        }

        this.dimensionCount = dimensionCount;
        this.frozen = false;
    }

    public final void joinSpaceObject(@NotNull SpaceObject newObject) throws FrozenObjectException {
        if (this.isFrozen()) {
            throw new FrozenObjectException(this);
        }

        this.objects.add(newObject);
    }

    protected Set<SpaceObject> spaceObjects() {
        return Set.copyOf(objects);
    }

    protected boolean isValidLocation(Vector vector) {
        return vector.dimensionCount() == this.dimensionCount;
    }

    public int getDimensionCount() {
        return dimensionCount;
    }

    /**
     * Creates an energy explosion at the given location
     * @param vector
     * @param filter
     */
    public void energyExplosion(
            Vector vector,
            Filter<SpaceObject> filter,
            double intensity
    ) {
        List<SpaceObject> validObjects = this.spaceObjects().stream()
                .filter(filter::accept)
                .toList();
        for (SpaceObject spaceObject : validObjects) {

        }
    }

    @Override
    public final boolean isFrozen() {
        return this.frozen;
    }

    @Override
    public final void freeze() throws IllegalStateException {
        if (this.frozen) {
            throw new IllegalStateException("Space fabric is already frozen");
        }

        this.frozen = true;
    }
}
