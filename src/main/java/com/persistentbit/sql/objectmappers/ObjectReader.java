package com.persistentbit.sql.objectmappers;

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
    Object read(Function<Class, ObjectReader> readerSupplier, ReadableRow properties);
}
