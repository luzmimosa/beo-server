package com.fadedbytes.beo.auth.exception;

public class UserNotResolvedException extends UserException {
    public UserNotResolvedException(String userId) {
        super("Cannot resolve user with id " + userId + ".");
    }
}
