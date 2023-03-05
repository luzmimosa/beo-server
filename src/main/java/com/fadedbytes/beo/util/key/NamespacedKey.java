package com.fadedbytes.beo.util.key;

import org.jetbrains.annotations.NotNull;

public class NamespacedKey {

    public static final String BEO_NAMESPACE = "beo";

    private final String namespace;
    private final String key;

    public NamespacedKey(@NotNull String namespace, @NotNull String key) {

        namespace = namespace.toLowerCase();
        key = key.toLowerCase();

        if (!(isValidString(namespace) && isValidString(key))) {
            throw new IllegalArgumentException("Invalid namespace or key for " + namespace + ":" + key + ". Values must only contain lower case alphanumeric characters and underscores.");
        }

        this.namespace = namespace.toLowerCase();
        this.key = key.toLowerCase();
    }

    private boolean isValidString(@NotNull String value) {
        return value.matches("[a-z\\d_]+");
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

    public static @NotNull NamespacedKey of(@NotNull String namespace, @NotNull String key) {
        return new NamespacedKey(namespace, key);
    }

    public static @NotNull NamespacedKey of(@NotNull String key) {
        return of(BEO_NAMESPACE, key);
    }

}
