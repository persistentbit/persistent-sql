package com.persistentbit.sql.staticsql;

/**
 * Created by petermuys on 5/10/16.
 */
public interface ExprRowReaderCache {
    /**
     * If the supplied value is already in the cache,
     * return the cached object.<br>
     * If the supplied value is not already in the cache,
     * put it in the cache and return the value.<br>
     * @param value The object to check in the cache
     * @return The cached object.
     */
    <T> T updatedFromCache(T value);
}
