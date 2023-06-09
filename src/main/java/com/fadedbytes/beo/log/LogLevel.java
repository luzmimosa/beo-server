package com.fadedbytes.beo.log;

public enum LogLevel {

    SYSTEM(1001),
    DEBUG(1000),
    ERROR(900),
    WARN(800),
    INFO(200),
    EVENT(100);

    private final int level;
    LogLevel(int level) {
        this.level = level;
    }

    public boolean isAtLeast(LogLevel otherLevel) {
        return this.level >= otherLevel.level;
    }

}
