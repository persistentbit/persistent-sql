package com.persistentbit.sql.databases;

import com.persistentbit.core.exceptions.ToDo;

/**
 * @author Peter Muys
 * @since 19/07/2016
 */
public class DbUnknownType extends AbstractDbType{

	public DbUnknownType() {
		super("Unknown");
	}

	@Override
	public String sqlWithLimit(long limit, String sql) {
		return unknown();
	}

	@Override
	public String sqlWithLimitAndOffset(long limit, long offset, String sql) {
		return unknown();
	}

	@Override
	public String setCurrentSchemaStatement(String schema) {
		throw new ToDo("Can't set the schema to '" + schema + "' for an unknown dbType");
	}

	@Override
	public void registerDriver() {
		throw new ToDo("Can't register an unknown driver");
	}
}
