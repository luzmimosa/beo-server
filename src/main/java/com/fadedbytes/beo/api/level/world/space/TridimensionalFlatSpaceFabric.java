package com.fadedbytes.beo.api.level.world.space;

public class TridimensionalFlatSpaceFabric extends SpaceFabric {
    public TridimensionalFlatSpaceFabric() {
        super(3);
    }

    @Override
    public int getDimensionCount() {
        return 3;
    }

}
