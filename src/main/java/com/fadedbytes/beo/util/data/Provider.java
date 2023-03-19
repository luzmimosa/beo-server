package com.fadedbytes.beo.util.data;

@FunctionalInterface
public interface Provider<T> {

    void provide(T value);

}
