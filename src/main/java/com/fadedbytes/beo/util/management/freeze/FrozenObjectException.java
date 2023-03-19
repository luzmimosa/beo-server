package com.fadedbytes.beo.util.management.freeze;

import com.fadedbytes.beo.api.exception.BeoException;

/**
 * Exception thrown when an attempt is made to modify a frozen object.
 */
public class FrozenObjectException extends BeoException {
    public FrozenObjectException(Freezable object) {
        super("Object " + object + " is frozen and can not be modified.");
    }
}
