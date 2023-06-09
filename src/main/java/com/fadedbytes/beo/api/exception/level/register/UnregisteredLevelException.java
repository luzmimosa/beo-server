package com.fadedbytes.beo.api.exception.level.register;

import com.fadedbytes.beo.api.exception.level.LevelException;
import com.fadedbytes.beo.api.level.Level;

public class UnregisteredLevelException extends LevelException {
    public UnregisteredLevelException(Level level) {
        super(level, "The level is not registered at the server.");
    }
}
