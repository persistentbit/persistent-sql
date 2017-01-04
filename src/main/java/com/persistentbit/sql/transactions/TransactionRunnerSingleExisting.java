package com.persistentbit.sql.transactions;

import com.persistentbit.core.logging.Log;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * Transaction runner keeping track of just 1 {@link Connection} even across multiple threads
 *
 * @author Peter Muys
 * @since 11/10/2016
 */
public class TransactionRunnerSingleExisting implements TransactionRunner{


	private Supplier<Connection> connectionSupplier;
	private Connection           current;

	public TransactionRunnerSingleExisting(Supplier<Connection> connectionSupplier) {
		this.connectionSupplier = connectionSupplier;
	}

	@Override
	public Supplier<Connection> getConnectionSupplier() {
		return connectionSupplier;
	}

	@Override
	public void trans(SqlCode code) {
		trans((c) -> {
			code.run(c);
			return null;
		});
	}

	@Override
	public <T> T trans(SqlCodeWithResult<T> code) {
		return Log.function("<code>").code(log -> {
			Connection c;
			boolean    isNew = false;
			synchronized(this) {
				if(current == null) {
					current = connectionSupplier.get();
					isNew = true;
				}
			}
			try {
				T result = code.run(current);
				if(isNew) {
					current.commit();

				}
				return result;
			} catch(Exception e) {
				try {
					current.rollback();
				} catch(SQLException e1) {
					log.error("Error while performing rollback", e1);
				}
				throw new RuntimeException("Rolledback", e);
			} finally {

				if(isNew) {
					try { current.close(); } catch(Exception e) {log.error("Error while closing the db connection", e); }
					current = null;
				}
			}
		});

	}

	@Override
	public void transNew(SqlCode code) {
		transNew((c) -> {
			code.run(c);
			return null;
		});
	}

	@Override
	public <T> T transNew(SqlCodeWithResult<T> code) {

		throw new UnsupportedOperationException("transNew Not yet supported on " + getClass().getName());
	}
}
