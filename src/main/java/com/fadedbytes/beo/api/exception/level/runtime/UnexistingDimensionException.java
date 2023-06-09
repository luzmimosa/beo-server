package com.fadedbytes.beo.api.exception.level.runtime;

import com.fadedbytes.beo.api.exception.BeoException;

/**
 * Exception thrown when a dimension is requested that does not exist.
 */
public class UnexistingDimensionException extends BeoException {

    public UnexistingDimensionException(String message) {
        super(message);
    }

}
