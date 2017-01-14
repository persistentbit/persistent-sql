package com.persistentbit.sql.sqlwork;

import com.persistentbit.core.result.Result;

import java.sql.Connection;

/**
 * TODOC
 *
 * @author petermuys
 * @since 12/01/17
 */
public interface DbTransManager{


	<T> Result<T> runInNewTransaction(SqlWork<T> work);

	Connection get();

}
