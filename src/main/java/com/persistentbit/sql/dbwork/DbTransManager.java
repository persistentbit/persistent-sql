package com.persistentbit.sql.dbwork;

import com.persistentbit.core.result.Result;

import java.sql.Connection;

/**
 * TODOC
 *
 * @author petermuys
 * @since 12/01/17
 */
public interface DbTransManager{


	<T> Result<T> runInNewTransaction(DbWork<T> work);

	Connection get();

}
