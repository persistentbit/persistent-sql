package com.persistentbit.sql.staticsql;

import com.persistentbit.core.logging.FunctionLogging;
import com.persistentbit.core.logging.Log;
import com.persistentbit.core.result.Result;
import com.persistentbit.sql.sqlwork.SqlWorkRunner;

import java.sql.Connection;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * TODOC
 *
 * @author petermuys
 * @since 14/01/17
 */
@FunctionalInterface
public interface DbWorkRunner{

	<T> Result<T> run(DbWork<T> work);


	static DbWorkRunner create(Supplier<Connection> connectionSupplier, DbContext context) {
		return Log.function(connectionSupplier, context).code((FunctionLogging l) -> {
			Objects.requireNonNull(connectionSupplier, "connectionSupplier");
			Objects.requireNonNull(context, "context");
			return new DbWorkRunner(){
				@Override
				public <T> Result<T> run(DbWork<T> work) {
					return SqlWorkRunner.run(connectionSupplier, work.asSqlWork(context));
				}
			};
		});
	}


}
