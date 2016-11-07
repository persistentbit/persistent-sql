package com.persistentbit.sql.databases;

/**
 * A DbType for a H2 database.
 * @author Peter Muys
 * @since 19/07/2016
 * @see DbType
 */
public class DbH2 extends AbstractDbType{

	public DbH2() {
		super("H2");
	}

	@Override
	public String sqlWithLimit(long limit, String sql) {
		return sql + " LIMIT " + limit;
	}

	@Override
	public String sqlWithLimitAndOffset(long limit, long offset, String sql) {
		return sql + " LIMIT " + limit + " OFFSET " + offset;
	}
	@Override
	public String setCurrentSchemaStatement(String schema) {
		return "SET SCHEMA " + schema;
	}


	@Override
	public void registerDriver() {
		registerDriver("org.h2.Driver");
	}
}
