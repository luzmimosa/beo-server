package com.fadedbytes.beo.event.listener;

/**
 * Defines the priority of an event listener method. The higher the priority, the later the method is called.
 * High priority methods can modify the event later in time.
 * Note that modifying the event, even in a high priority method, does not guarantee that the modification will be
 * the last state of the event. If another high priority method modifies the event, the last modification will be
 * the one that is used.
 */
public enum ListenerPriority {

    /**
     * Lowest priority. The event information is likely to be changed by other listeners.
     * <br/>
     * <b>Do</b> use this priority if your changes are not critical.
     * <br/>
     * <b>Do not</b>use this priority if you need to monitor the final state of the event.
     */
    LOW(300),

    /**
     * Normal priority. The event information is likely to be changed by other listeners.
     * <br/>
     * Use this priority by default.
     */
    NORMAL(500),
    /**
     * Highest priority. The changes made by this listener are likely to be the final state of the event.
     * <br/>
     * <b>Do</b> use this priority if your changes are critical.
     * <br/>
     * <b>Do not</b> use this priority if you want to monitor the final state of the event (use {@link #MONITOR} instead).
     */
    HIGH(700),

    /**
     * Monitor priority. The event flow is completed and the event information will not be changed by other listeners.
     * <br/>
     * <b>Do</b> use this priority if you need to monitor the final state of the event.
     * <br/>
     * <b>Do not</b> use this priority to modify the event. If your changes are critical, use {@link #HIGH}.
     */
    MONITOR(1000);

    private final int priority;

    ListenerPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

}