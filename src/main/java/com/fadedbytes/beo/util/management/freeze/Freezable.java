package com.fadedbytes.beo.util.management.freeze;

/**
 * Represents a class that can load some kind of data for a determined period of time. After the
 * period of time has elapsed, the containers storing the data are frozen and can no longer be
 * modified.
 * <p>
 * Once the object is frozen, it can not be unfrozen. If there is an attempt to modify a frozen
 * object, a {@link FrozenObjectException} will be thrown.
 */
public interface Freezable {

    /**
     * @return whether the object is frozen or not
     */
    boolean isFrozen();

    /**
     * Freezes the object. After this method is called, the object data should not be modified.
     */
    void freeze();

}
