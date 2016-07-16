package com.persistentbit.sql.objectmappers;

/**
 * Represent a readable database table row.<br>
 * @see WritableRow
 */
@FunctionalInterface
public interface ReadableRow {
    /**
     * Get value of the Row column with the provided name
     * @param name The case insensitive column name.
     * @return The value of the column or null if not existing
     */
    Object read(String name);
}
