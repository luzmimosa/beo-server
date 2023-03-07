package com.fadedbytes.beo.event.type;

/**
 * Events that implement the Blockable interface will block the calling thread until the event lifecycle has ended.
 * This is useful for events that manage data that is able to be modified by event listeners.
 */
public interface Blockable {
}
