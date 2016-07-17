package com.persistentbit.sql.objectmappers;

/**
 * Object to map an object to a row.<br>
 * @see ObjectRowMapper
 */
@FunctionalInterface
public interface ObjectWriter {
    void write(String fieldName,Object obj, ObjectWriter masterWriter, WritableRow result);
}
