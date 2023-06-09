package com.fadedbytes.beo.util.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Clock {
    private final ScheduledExecutorService scheduler;
    private final Runnable onTick;
    private final long periodMs;
    private boolean isRunning = true;

    public Clock(int tps, Runnable onTick) {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.onTick = onTick;
        this.periodMs = 1000L / tps;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            if (isRunning) {
                onTick.run();
            }
        }, 0L, periodMs, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        this.isRunning = false;
        scheduler.shutdown();
    }
}
