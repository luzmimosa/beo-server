package com.fadedbytes.beo.util.key;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NamespacedKeyTest {

    @Test
    void testToString() {
        NamespacedKey key = new NamespacedKey("namespacetest", "test1");
        assertEquals("namespacetest:test1", key.toString());
    }

    @Test
    void testAvailableCharacters() {

        // space at namespace
        assertThrows(
                IllegalArgumentException.class,
                () -> new NamespacedKey("test namespace", "test1")
        );

        // space at key
        assertThrows(
                IllegalArgumentException.class,
                () -> new NamespacedKey("testnamespace", "test 1")
        );

        // capital letters at namespace
        assertThrows(
                IllegalArgumentException.class,
                () -> new NamespacedKey("testNamespace", "test1")
        );

        // capital letters at key
        assertThrows(
                IllegalArgumentException.class,
                () -> new NamespacedKey("testnamespace", "Test1")
        );

        // dash at namespace
        assertThrows(
                IllegalArgumentException.class,
                () -> new NamespacedKey("test-namespace", "test1")
        );

        // dash at key
        assertThrows(
                IllegalArgumentException.class,
                () -> new NamespacedKey("testnamespace", "test-1")
        );

        // empty namespace
        assertThrows(
                IllegalArgumentException.class,
                () -> new NamespacedKey("", "test1")
        );

        // empty key
        assertThrows(
                IllegalArgumentException.class,
                () -> new NamespacedKey("testnamespace", "")
        );

    }

    @Test
    void testEquals() {
        NamespacedKey key1 = new NamespacedKey("namespacetest", "test1");
        NamespacedKey key2 = new NamespacedKey("namespacetest", "test1");
        NamespacedKey key3 = new NamespacedKey("namespacetest", "test2");

        assertEquals(key1, key2);
        assertNotEquals(key1, key3);
    }

    @Test
    void testNamespace() {
        NamespacedKey key = new NamespacedKey("namespacetest", "test1");
        assertEquals("namespacetest", key.getNamespace());
    }

    @Test
    void testKey() {
        NamespacedKey key = new NamespacedKey("namespacetest", "test1");
        assertEquals("test1", key.getKey());
    }

}