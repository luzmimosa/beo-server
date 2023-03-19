package com.fadedbytes.beo.util.timer;

import com.fadedbytes.beo.util.data.Provider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TickTrigger {

    private long totalTicks;
    private long performedTicks;
    private final @NotNull Clock clock;

    private final @NotNull Provider<Long> onTicksBehind;
    private final long acceptableTicksBehind;
    private final int tps;

    public TickTrigger(int tps, long acceptableTicksBehind, @Nullable Provider<Long> onTicksBehind) {
        this.clock = new Clock(tps, this::onTick);

        this.onTicksBehind = onTicksBehind == null ? (Long uwu) -> {} : onTicksBehind;
        this.acceptableTicksBehind = acceptableTicksBehind;
        this.tps = tps;
    }

    /**
     * Requests the trigger to register a tick. If the tick should not be performed yet, the method returns false. If the tick should be performed, the method returns true and the tick is registered.
     * @return true if the tick should be performed, false otherwise
     */
    public synchronized boolean tickRequest() {
        if (this.totalTicks > this.performedTicks) {
            this.performedTicks++;
            return true;
        } else {
            return false;
        }
    }

    public long ticksBehind() {
        return this.totalTicks - this.performedTicks;
    }

    private synchronized void onTick() {
        this.totalTicks++;

        if (this.ticksBehind() > this.acceptableTicksBehind) {
            if (this.totalTicks % (tps * 20L) == 0) {
                this.onTicksBehind.provide(this.ticksBehind());
            }
        }
    }

    public void stop() {
        this.clock.stop();
    }

}
