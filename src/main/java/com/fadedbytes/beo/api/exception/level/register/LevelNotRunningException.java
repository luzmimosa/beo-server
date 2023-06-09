package com.fadedbytes.beo.api.exception.level.register;

import com.fadedbytes.beo.api.exception.level.LevelException;
import com.fadedbytes.beo.api.level.Level;

/**
 * Thrown when a level is called to stop, but it's not running.
 */
public class LevelNotRunningException extends LevelException {
    public LevelNotRunningException(Level level) {
        super(level, "Level is not running");
    }
}
