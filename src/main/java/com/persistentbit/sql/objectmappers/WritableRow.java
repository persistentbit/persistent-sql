package com.persistentbit.sql.objectmappers;

import java.time.temporal.Temporal;
import java.util.Date;

/**
 * Represent an abstraction of a writable table row.
 * @see ReadableRow
 */
@FunctionalInterface
public interface WritableRow {
    /**
     * Set a Row Column value
     * @param name  The case insensitive name of the column
     * @param value The new value of the column
     * @return This row
     */
    WritableRow write(String name, Object value);


}
