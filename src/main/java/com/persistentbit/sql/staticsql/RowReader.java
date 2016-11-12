package com.persistentbit.sql.staticsql;

/**
 * Source for reading a row of data.
 * @since 3/10/16
 * @author Peter Muys
 */
public interface RowReader{

	<T> T readNext(Class<T> cls);
}
