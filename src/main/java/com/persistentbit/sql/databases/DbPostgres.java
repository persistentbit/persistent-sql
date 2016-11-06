package com.persistentbit.sql.databases;

/**
 * A DbType for a PostgreSQL database.
 * @author Peter Muys
 * @since 19/07/2016
 * @see DbType
 */
public class DbPostgres extends AbstractDbType{

	public DbPostgres() {
		super("PostgreSQL");
	}

	public static String connectionUrlLocal(String db) {
		return connectionUrl("localhost", db);
	}

	public static String connectionUrl(String host, String db) {
		return connectionUrl(host, 5432, db);
	}

	public static String connectionUrl(String host, int port, String db) {
		return "jdbc:postgresql://" + host + ":" + port + "/" + db;
	}

	public static String getDriverClassName() {
		return "org.postgresql.Driver";
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
		return "SET SEARCH_PATH TO " + schema + ", PUBLIC";
	}
}
