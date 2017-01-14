package com.persistentbit.sql.staticsql;

import com.persistentbit.core.result.Result;
import com.persistentbit.sql.sqlwork.SqlWorkRunner;

import java.sql.Connection;
import java.util.function.Supplier;

/**
 * TODOC
 *
 * @author petermuys
 * @since 14/01/17
 */
public class DbWorkRunner{

	public static <R> Result<R> run(Supplier<Connection> connectionSupplier, DbContext context, DbWork<R> work) {
		return SqlWorkRunner.run(connectionSupplier, work.asSqlWork(context));
	}
}
