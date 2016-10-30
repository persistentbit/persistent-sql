package com.persistentbit.sql.databases;

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
}
