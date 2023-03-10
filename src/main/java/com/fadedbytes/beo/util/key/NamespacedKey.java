package com.fadedbytes.beo.util.key;

import org.jetbrains.annotations.NotNull;

public class NamespacedKey {

    public static final String BEO_NAMESPACE = "beo";
    public static final String REGEX = "^[a-z\\d_]+$";

    private final String namespace;
    private final String key;

    public NamespacedKey(@NotNull String namespace, @NotNull String key) {

        if (!(isValidString(namespace) && isValidString(key))) {
            throw new IllegalArgumentException("Invalid namespace or key for " + namespace + ":" + key + ". Values must only contain lower case alphanumeric characters and underscores.");
        }

        this.namespace = namespace.toLowerCase();
        this.key = key.toLowerCase();
    }

    private boolean isValidString(@NotNull String value) {
        return value.matches(REGEX);
    }

    public @NotNull String getNamespace() {
        return namespace;
    }

    public @NotNull String getKey() {
        return key;
    }

    @Override
    public @NotNull String toString() {
        return String.format("%s:%s", namespace, key);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NamespacedKey otherKey) {
            return otherKey.namespace.equals(this.namespace) && otherKey.key.equals(this.key);
        } else return false;
    }

    public static @NotNull NamespacedKey of(@NotNull String namespace, @NotNull String key) {
        return new NamespacedKey(namespace, key);
    }

    public static @NotNull NamespacedKey of(@NotNull String key) {
        return of(BEO_NAMESPACE, key);
    }

}
