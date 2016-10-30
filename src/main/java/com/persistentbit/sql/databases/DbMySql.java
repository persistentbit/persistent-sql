package com.persistentbit.sql.databases;

/**
 * @author Peter Muys
 * @since 19/07/2016
 */
public class DbMySql extends AbstractDbType{

	public DbMySql() {
		super("MySQL");
	}

	static public String connectionUrl(String db) {
		return connectionUrl("localhost", 3306, db);
	}

	static public String connectionUrl(String host, int port, String db) {
		return "jdbc:mysql://" + host + ":" + port + "/" + db;
	}

	static public String connectionUrl(String host, String db) {
		return connectionUrl(host, 3306, db);
	}

	static public String getDriverClassName() {
		return "com.mysql.jdbc.Driver";
	}

	@Override
	public String sqlWithLimit(long limit, String sql) {
		return sql + " LIMIT " + limit;
	}

	@Override
	public String sqlWithLimitAndOffset(long limit, long offset, String sql) {
		return sql + " LIMIT " + limit + " OFFSET " + offset;
	}

}
