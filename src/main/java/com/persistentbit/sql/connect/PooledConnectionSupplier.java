package com.persistentbit.sql.connect;

import com.persistentbit.core.doc.annotations.DUsesClass;
import com.persistentbit.sql.PersistSqlException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * A {@link Connection} supplier that uses a connection pool to return new connections.<br>
 *
 * @author Peter Muys
 * @since 13/07/2016
 */
@DUsesClass(ConnectionWrapper.class)
public class PooledConnectionSupplier implements Supplier<Connection>{

	static private final Logger log = Logger.getLogger(PooledConnectionSupplier.class.getName());

	private final Supplier<Connection>      supplier;
	private final Consumer<Connection>      resetter;
	private final int                       poolSize;
	private final BlockingQueue<Connection> freeConnections;
	private       int                       activeConnections;


	public PooledConnectionSupplier(Supplier<Connection> supplier, int poolSize) {
		this(supplier, poolSize, (c) -> {
			try {
				c.setAutoCommit(false);
			} catch(SQLException e) {
				throw new PersistSqlException(e);
			}
		});
	}

	public PooledConnectionSupplier(Supplier<Connection> supplier, int poolSize,
									Consumer<Connection> connectionResetter
	) {
		this.supplier = supplier;
		this.poolSize = poolSize;
		this.resetter = connectionResetter;
		this.freeConnections = new LinkedBlockingQueue(poolSize);
	}

	@Override
	public synchronized Connection get() {
		if(freeConnections.isEmpty()) {
			if(activeConnections < poolSize) {
				//Nog geen pool opgebouwd...
				ConnectionWrapper con = newConnection(supplier.get());
				activeConnections++;
				return con;
			}
		}
		Connection con = null;
		try {
			do {
				try {
					con = freeConnections.poll(1000, TimeUnit.MILLISECONDS);
				} catch(InterruptedException e) {
					log.warning("Waiting for a free connection...");
				}
			} while(con == null);
			if(con.isValid(0) == false) {
				return newConnection(supplier.get());
			}
			return newConnection(con);
		} catch(SQLException e) {
			throw new PersistSqlException(e);
		}

	}

	private ConnectionWrapper newConnection(Connection realConnection) {
		resetter.accept(realConnection);
		ConnectionWrapper con = new ConnectionWrapper(realConnection, new ConnectionWrapper.ConnectionHandler(){
			private boolean isCommit = false;

			@Override
			public void onClose(Connection connection) throws SQLException {
				if(isCommit == false || connection.getAutoCommit()) {
					connection.rollback();
				}
				freeConnections.add(connection);

			}

			@Override
			public void onCommit(Connection connection) throws SQLException {
				isCommit = true;
				connection.commit();
			}

			@Override
			public void onRollback(Connection connection) throws SQLException {
				isCommit = true;
				connection.rollback();
			}

			@Override
			public void onAbort(Connection connection, Executor executor) throws SQLException {
				activeConnections--;
				connection.abort(executor);
			}
		});
		return con;
	}

	public synchronized void close() {
		int inuse = activeConnections - freeConnections.size();
		if(inuse > 0) {
			log.warning("Closing the connection pool with " + inuse + " connections still in use");
		}
		else {
			log.info("Closing the connection pool with " + activeConnections + " open connections");
		}
		while(activeConnections > 0) {
			Connection con = null;
			try {
				for(int t = 0; t < 20; t++) {
					con = freeConnections.poll(1000, TimeUnit.MILLISECONDS);
					if(con != null) {
						break;
					}
					log.warning("Waiting for the release of a connection");
				}
				try {
					con.close();
				} catch(SQLException e) {
					e.printStackTrace();
				}
				activeConnections--;
			} catch(InterruptedException e) {
				log.info("Waiting for connections to close");
			}
		}
	}
}
