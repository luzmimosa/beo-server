package com.fadedbytes.beo.api.exception.level;

import com.fadedbytes.beo.api.exception.BeoException;
import com.fadedbytes.beo.api.level.Level;

public class LevelException extends BeoException {
    public LevelException(Level level, String message) {
        super(message + " Level: " + level.KEY.getKey());
    }
}
