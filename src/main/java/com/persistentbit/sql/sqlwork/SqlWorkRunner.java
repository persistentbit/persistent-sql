package com.persistentbit.sql.sqlwork;

import com.persistentbit.core.OK;
import com.persistentbit.core.logging.Log;
import com.persistentbit.core.result.Result;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * TODOC
 *
 * @author petermuys
 * @since 14/01/17
 */
public class SqlWorkRunner{

	private static class DbWorkTrans implements DbTransManager{

		private final Supplier<Connection> connectionSupplier;
		private       Connection           connection;

		public DbWorkTrans(Supplier<Connection> connectionSupplier) {
			this.connectionSupplier = connectionSupplier;
			this.connection = connectionSupplier.get();
			try {
				connection.setAutoCommit(false);

			} catch(SQLException e) {
				throw new RuntimeException("Exception while setting db connection autocomit to false", e);
			}
		}

		@Override
		public String toString() {
			return "DbWorkTrans[]";
		}

		@Override
		public Connection get() {
			return connection;
		}

		@Override
		public <T> Result<T> runInNewTransaction(SqlWork<T> work) {
			return new DbWorkTrans(connectionSupplier).run(work);
		}

		public <R> Result<R> run(SqlWork<R> work) {
			Result<R> result;
			try {
				result = work.execute(this);
			} catch(Exception e) {
				result = Result.failure(e);
			}
			Result<R> finalResult = result;
			return Result.function().code(l ->
											  finalResult
												  .ifFailure(failure -> {
													  l.warning("Rolling back database transaction.");
													  try {
														  get().rollback();
													  } catch(SQLException se) {
														  l.exception(se);
													  }
													  close();
												  })

												  .ifPresent(success -> {
													  l.warning("Commit database transaction on success");
													  try {
														  get().commit();
													  } catch(SQLException se) {
														  l.exception(se);
													  }
													  close();
												  })
												  .ifEmpty(empty -> {
															   l.warning("Commit database transaction on empty result");
															   try {
																   get().commit();
															   } catch(SQLException se) {
																   l.exception(se);
															   }
															   close();
														   }
												  )
			);
		}

		private void close() {
			Log.function().code(l -> {
				get().close();
				return OK.inst;
			});
		}
	}

	public static <R> Result<R> run(Supplier<Connection> connectionSupplier, SqlWork<R> work) {
		return new DbWorkTrans(connectionSupplier).run(work);
	}
}
