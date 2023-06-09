package com.fadedbytes.beo.api.exception.level.register;

import com.fadedbytes.beo.api.exception.level.LevelException;
import com.fadedbytes.beo.api.level.Level;

/**
 * Thrown when a level is called to run, but it is already running.
 */
public class LevelAlreadyRunningException extends LevelException {

    public LevelAlreadyRunningException(Level level) {
        super(level, "Level is already running");
    }

}
