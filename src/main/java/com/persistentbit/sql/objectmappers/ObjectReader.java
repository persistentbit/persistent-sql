package com.persistentbit.sql.objectmappers;

import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * Object to read an Object using data from a ReadableRow
 *
 * User: petermuys
 * Date: 16/07/16
 * Time: 09:33
 *
 * @see ObjectRowMapper
 */
@FunctionalInterface
public interface ObjectReader {
    /**
     * Read an object from a readable row
     * @param typeToRead  The expected Type
     * @param name The name of the value
     * @param readerSupplier Supplier of Objectreaders for classes
     * @param properties The row of properties
     * @return The value
     */
    Object read(Type typeToRead, String name, Function<Class,ObjectReader> readerSupplier, ReadableRow properties);
}
