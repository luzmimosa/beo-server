package com.fadedbytes.beo.event.type;

import com.fadedbytes.beo.event.EventManager;
import com.fadedbytes.beo.server.BeoServer;
import org.jetbrains.annotations.NotNull;

/**
 * Parent class for all events.
 */
public abstract class Event {

    private final @NotNull BeoServer server;
    private boolean launched = false;
    private boolean ended = false;

    public Event(@NotNull BeoServer server) {
        this.server = server;
    }

    /**
     * @return The server that this event is being fired from.
     */
    public final @NotNull BeoServer getServer() {
        return this.server;
    }

    /**
     * Launches the event to the {@link EventManager} instance of the assigned {@link #server server} <br />
     * If the event is {@link Blockable}, calling this method will block the thread until the event lifecycle is complete.
     * @throws IllegalStateException if the event has already been launched.
     */
    public final void launch() throws IllegalStateException {
        if (this.launched) {
            throw new IllegalStateException("Event already launched: " + this.getClass().getName());
        }
        this.launched = true;
        this.server.getEventManager().launchEvent(this);

        if (this instanceof Blockable) {
            while (true) {
                if (this.ended) break;
            }
        }
    }

    /**
     * Ends the event lifecycle. This means that the event has passed through all of the listeners and is now finished.
     * Future changes to the event will not take effect. <br />
     * This method should only be called by an {@link EventManager} instance and inside of the {@link EventManager#EVENT_LAUNCHER_THREAD Launcher thread group}.
     * @throws IllegalStateException if the event has already been ended.
     * @throws WrongThreadException if the method is called from a thread that is not the {@link EventManager#EVENT_LAUNCHER_THREAD Launcher thread group}.
     */
    public final void onLifecycleEnd() throws IllegalStateException {
        if (!Thread.currentThread().getThreadGroup().equals(EventManager.eventThreadGroup)) {
            throw new WrongThreadException("Event lifecycle end must be called from the event thread group.");
        }
        if (this.ended) {
            throw new IllegalStateException("Event lifecycle already ended: " + this.getClass().getName());
        }

        this.ended = true;
    }

    /**
     * Returns true if the event lifecycle has ended. Changes to this value will not be permitted after the event has ended.
     * @return whether the event lifecycle has ended.
     */
    public final boolean hasEndedLifecycle() {
        return this.ended;
    }
}
