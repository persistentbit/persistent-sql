package com.persistentbit.sql.experiments;

import com.persistentbit.core.result.Result;

import java.sql.Connection;

/**
 * TODOC
 *
 * @author petermuys
 * @since 12/01/17
 */
public interface DbTransManager{

	DbTransManager newTrans();

	Connection get();

	<R> Result<R> run(DbWork<R> work);
}
