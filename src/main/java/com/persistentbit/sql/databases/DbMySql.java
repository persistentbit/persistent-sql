package com.persistentbit.sql.databases;

import com.persistentbit.core.collections.PByteList;

/**
 * DbType for a MySql database
 * @author Peter Muys
 * @since 19/07/2016
 * @see DbType
 */
public class DbMySql extends AbstractDbType{

	public DbMySql() {
		super("MySQL");
	}

	public static String connectionUrl(String db) {
		return connectionUrl("localhost", 3306, db);
	}

	public static String connectionUrl(String host, int port, String db) {
		return "jdbc:mysql://" + host + ":" + port + "/" + db;
	}

	public static String connectionUrl(String host, String db) {
		return connectionUrl(host, 3306, db);
	}

	public static String getDriverClassName() {
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

	@Override
	public String setCurrentSchemaStatement(String schema) {
		return "USE " + schema;
	}

	@Override
	public void registerDriver() {
		registerDriver(getDriverClassName());
	}

	@Override
	public String asLiteralBlob(PByteList byteList) {
		return "0x" + byteList.toHexString();
	}
}
