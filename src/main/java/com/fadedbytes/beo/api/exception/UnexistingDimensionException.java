package com.fadedbytes.beo.api.exception;

/**
 * Exception thrown when a dimension is requested that does not exist.
 */
public class UnexistingDimensionException extends BeoException {

    public UnexistingDimensionException(String message) {
        super(message);
    }

}
