package com.persistentbit.sql.transactions;


import com.persistentbit.core.Pair;
import com.persistentbit.sql.PersistSqlException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * A SQL transaction code Runner that keeps track of the current transaction on a thread basis.<br>
 *
 * @author Peter Muys
 * @since 29/02/2016
 */
public class TransactionRunnerPerThread implements TransactionRunner{

	static private final Logger log = Logger.getLogger(TransactionRunnerPerThread.class.getName());

	private final Supplier<Connection> connectionSupplier;
	private AtomicLong nextTransactionId = new AtomicLong(0);

	private ThreadLocal<Pair<Long, Connection>> currentConnection = new ThreadLocal<>();

	public TransactionRunnerPerThread(DataSource ds) {
		this(() -> {
			try {
				return ds.getConnection();
			} catch(SQLException e) {
				log.severe(e.getMessage());
				throw new TransactionsException("Error while getting a new Database connection", e);
			}
		});
	}

	public TransactionRunnerPerThread(Supplier<Connection> connectionSupplier) {
		this.connectionSupplier = connectionSupplier;
	}

	@Override
	public Supplier<Connection> getConnectionSupplier() {
		return connectionSupplier;
	}

	@Override
	public <T> T transNew(SqlCodeWithResult<T> code) {
		Pair<Long, Connection> prev = currentConnection.get();
		try {
			currentConnection.remove();
			return trans(code);
		} finally {
			currentConnection.set(prev);
		}
	}

	@Override
	public <T> T trans(SqlCodeWithResult<T> code) {
		return doRun(() -> {
			try {
				return code.run(currentConnection.get()._2);
			} catch(SQLException e) {
				throw new PersistSqlException(e);
			}
		});
	}

	private <R> R doRun(Callable<R> code) {
		boolean isNewConnection = false;
		if(currentConnection.get() == null) {
			currentConnection.set(new Pair<>(createNewTransactionId(), connectionSupplier.get()));
			try {
				currentConnection.get()._2.setAutoCommit(false);
				isNewConnection = true;
			} catch(SQLException sql) {
				throw new TransactionsException("Error while creating a new jdbc connection", sql);
			}
		}
		Connection con = currentConnection.get()._2;
		try {
			R result = code.call();
			con.commit();
			return result;
		} catch(Exception e) {
			try {
				con.rollback();
			} catch(SQLException e1) {
				e1.printStackTrace();
			}
			currentConnection.remove();
			throw new RuntimeException("Rolledback", e);
		} finally {
			if(isNewConnection) {
				try { con.close(); } catch(Exception e) { e.printStackTrace(); }
				currentConnection.remove();
			}
		}
	}

	private long createNewTransactionId() {
		long result = nextTransactionId.incrementAndGet();
		return (Thread.currentThread().getId() << 32) + result;
	}

	@Override
	public void transNew(SqlCode code) {
		Pair<Long, Connection> prev = currentConnection.get();
		try {
			currentConnection.remove();
			trans(code);
		} finally {
			currentConnection.set(prev);
		}
	}

	@Override
	public void trans(SqlCode code) {
		doRun(() -> {
			try {
				code.run(currentConnection.get()._2);
			} catch(SQLException e) {
				throw new PersistSqlException(e);
			}
			return null;
		});
	}

	public Optional<Long> currentTransactionId() {
		Pair<Long, Connection> con = currentConnection.get();
		if(con == null) {
			return Optional.empty();
		}
		return Optional.of(con._1);
	}
}
