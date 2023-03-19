package com.fadedbytes.beo.util.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Clock {
    private final ScheduledExecutorService scheduler;

    public Clock(int tps, Runnable onTick) {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        long periodMs = 1000L / tps;

        scheduler.scheduleAtFixedRate(onTick, 0L, periodMs, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        scheduler.shutdown();
    }
}
