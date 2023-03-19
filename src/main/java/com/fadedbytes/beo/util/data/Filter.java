package com.fadedbytes.beo.util.data;

@FunctionalInterface
public interface Filter<T> {

    boolean accept(T object);

}
