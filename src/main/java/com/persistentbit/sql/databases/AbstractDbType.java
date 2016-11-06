package com.persistentbit.sql.databases;

import com.persistentbit.sql.PersistSqlException;

/**
 * @author Peter Muys
 * @since 19/07/2016
 */
public abstract class AbstractDbType implements DbType{

	private final String databaseName;

	protected AbstractDbType(String databaseName) {
		this.databaseName = databaseName;
	}


	@Override
	public String getDatabaseName() {
		return databaseName;
	}

	protected <T> T unknown() {
		throw new PersistSqlException("Unknown how to do that for " + this.getClass().getSimpleName());
	}

	protected <T> T notImplemented() {
		throw new PersistSqlException("Not Yet implemented for " + this.getClass().getSimpleName());
	}

}
