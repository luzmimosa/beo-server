package com.fadedbytes.beo.network.server.handler;

public enum CommandErrorCode {

    SERVER_NOT_FOUND("server_not_found"),
    LEVEL_NOT_FOUND("level_not_found"),
    INVALID_ARGUMENTS("invalid_arguments"),
    LEVEL_START_ERROR("level_start_error"),
    LEVEL_STOP_ERROR("level_stop_error");

    public final String code;
    CommandErrorCode(String code) {
        this.code = code;
    }

}
