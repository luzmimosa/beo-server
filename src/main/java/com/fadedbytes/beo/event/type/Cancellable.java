package com.fadedbytes.beo.event.type;

/**
 * Events that implement the Cancellable interface will be able to be cancelled by event listeners.
 * If the event is cancelled, the launcher should take the appropriate action, such as not performing the action the event describes.
 */
public interface Cancellable extends Blockable {

    /**
     * Sets the cancelled state of this event.
     * @param cancelled The new cancelled state of this event.
     */
    void setCancelled(boolean cancelled);

    /**
     * @return whether this event is cancelled.
     */
    boolean isCancelled();

}
