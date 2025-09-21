package com.aseds.aithssainesbaiti.domain;

/**
 * Generates unique IDs.
 */
public final class IdGenerator {
    /**
     * Private constructor to prevent instantiation.
     */
    private IdGenerator() {

    }

    /**
     * The current ID value.
     */
    private static int id;

    /**
     * Generates a new unique ID.
     * @return the new ID
     */
    public static int getId() throws IllegalStateException {
        if (id == Integer.MAX_VALUE) {
            throw new IllegalStateException("ID overflow");
        }
        return ++id;
    }
}
