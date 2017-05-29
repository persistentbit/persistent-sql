package com.persistentbit.sql.connect;

import com.persistentbit.core.doc.annotations.DSupport;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Used by {@link PooledConnectionSupplier} to wrap extra functionality around a {@link Connection}
 *
 * @author Peter Muys
 * @see PooledConnectionSupplier
 * @since 13/07/2016
 */
@DSupport
class ConnectionWrapper implements Connection{

	private final Connection master;
	private final ConnectionHandler handler;

	public ConnectionWrapper(Connection master, ConnectionHandler handler) {
		this.master = master;
		this.handler = handler;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return master.isWrapperFor(iface);
	}

	@Override
	public Statement createStatement() throws SQLException {
		return master.createStatement();
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return master.prepareStatement(sql);
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		return master.prepareCall(sql);
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		return master.nativeSQL(sql);
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		return master.getAutoCommit();
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		master.setAutoCommit(autoCommit);
	}

	@Override
	public void commit() throws SQLException {
		handler.onCommit(master);
	}

	@Override
	public void rollback() throws SQLException {
		handler.onRollback(master);
	}

	@Override
	public void close() throws SQLException {
		handler.onClose(master);
	}

	@Override
	public boolean isClosed() throws SQLException {
		return master.isClosed();
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return master.getMetaData();
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return master.isReadOnly();
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		master.setReadOnly(readOnly);
	}

	@Override
	public String getCatalog() throws SQLException {
		return master.getCatalog();
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		master.setCatalog(catalog);
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		return master.getTransactionIsolation();
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		master.setTransactionIsolation(level);
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return master.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		master.clearWarnings();
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return master.createStatement(resultSetType, resultSetConcurrency);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency
	) throws SQLException {
		return master.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return master.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return master.getTypeMap();
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		master.setTypeMap(map);
	}

	@Override
	public int getHoldability() throws SQLException {
		return master.getHoldability();
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		master.setHoldability(holdability);
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		return master.setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		return master.setSavepoint(name);
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		master.rollback(savepoint);
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		master.releaseSavepoint(savepoint);
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability
	) throws SQLException {
		return master.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
											  int resultSetHoldability
	) throws SQLException {
		return master.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
										 int resultSetHoldability
	) throws SQLException {
		return master.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return master.prepareStatement(sql, autoGeneratedKeys);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		return master.prepareStatement(sql, columnIndexes);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		return master.prepareStatement(sql, columnNames);
	}

	@Override
	public Clob createClob() throws SQLException {
		return master.createClob();
	}

	@Override
	public Blob createBlob() throws SQLException {
		return master.createBlob();
	}

	@Override
	public NClob createNClob() throws SQLException {
		return master.createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return master.createSQLXML();
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		return master.isValid(timeout);
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		master.setClientInfo(name, value);
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		return master.getClientInfo(name);
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return master.getClientInfo();
	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		master.setClientInfo(properties);
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return master.createArrayOf(typeName, elements);
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return master.createStruct(typeName, attributes);
	}

	@Override
	public String getSchema() throws SQLException {
		return master.getSchema();
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		master.setSchema(schema);
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		master.abort(executor);
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		master.setNetworkTimeout(executor, milliseconds);
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		return master.getNetworkTimeout();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return master.unwrap(iface);
	}

	public interface ConnectionHandler{

		default void onClose(Connection connection) throws SQLException {
			connection.close();
		}

		default void onCommit(Connection connection) throws SQLException {
			connection.commit();
		}

		default void onRollback(Connection connection) throws SQLException {
			connection.rollback();
		}

		default void onAbort(Connection connection, Executor executor) throws SQLException {
			connection.abort(executor);
		}
	}

}
