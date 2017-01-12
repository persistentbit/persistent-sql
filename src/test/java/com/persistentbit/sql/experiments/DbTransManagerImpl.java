package com.persistentbit.sql.experiments;

import com.persistentbit.core.logging.Log;
import com.persistentbit.core.result.Result;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * TODOC
 *
 * @author petermuys
 * @since 12/01/17
 */
public class DbTransManagerImpl implements DbTransManager{

	private final Supplier<Connection> connectionSupplier;
	private final Result<Connection>   connection;
	private final boolean              needClosing;

	private DbTransManagerImpl(Supplier<Connection> connectionSupplier, Result<Connection> connection,
							   boolean needClosing
	) {
		this.connectionSupplier = Objects.requireNonNull(connectionSupplier);
		this.connection = connection;
		this.needClosing = needClosing;
	}

	public DbTransManagerImpl(Supplier<Connection> connectionSupplier) {
		this(connectionSupplier, Result.lazy(() -> {
			Connection connection = connectionSupplier.get();
			try {
				connection.setAutoCommit(false);
				return Result.success(connection);
			} catch(SQLException e) {
				throw new RuntimeException("Exception while setting db connection autocomit to false", e);
			}

		}), true);
	}

	@Override
	public DbTransManager newTrans() {
		return new DbTransManagerImpl(this.connectionSupplier);
	}

	@Override
	public Connection get() {
		return connection.orElseThrow();
	}

	public <R> Result<R> run(DbWork<R> work) {
		Result<R> result;
		try {
			result = work.execute(new DbTransManagerImpl(connectionSupplier, connection, false));
		} catch(Exception e) {
			result = Result.failure(e);
		}
		Result<R> finalResult = result;
		return Result.function().code(l ->
										  finalResult
											  .ifFailure(failure ->
															 connection.ifPresent(c -> {
																 if(needClosing) {
																	 l.warning("Rolling back database transaction.");
																	 try {
																		 c.getValue().rollback();
																	 } catch(SQLException se) {
																		 l.exception(se);
																	 }
																	 close();
																 }
															 })
											  )
											  .ifPresent(success ->
															 connection.ifPresent(c -> {
																 if(needClosing) {
																	 l.warning("Commit database transaction on success");
																	 try {
																		 c.getValue().commit();
																	 } catch(SQLException se) {
																		 l.exception(se);
																	 }
																	 close();
																 }
															 })
											  )
											  .ifEmpty(empty ->
														   connection.ifPresent(c -> {
															   if(needClosing) {
																   l.warning("Commit database transaction on empty result");
																   try {
																	   c.getValue().commit();
																   } catch(SQLException se) {
																	   l.exception(se);
																   }
																   close();
															   }
														   })
											  )
		);
	}


	public void close() {
		connection.ifPresent(s ->
								 Log.function().code(l -> {
									 s.getValue().close();
									 return Result.empty();
								 })
		);

	}
}
