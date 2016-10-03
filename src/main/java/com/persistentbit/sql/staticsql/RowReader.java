package com.persistentbit.sql.staticsql;

/**
 * Created by petermuys on 3/10/16.
 */
public interface RowReader {
    <T> T readNext(Class<T> cls);
}
