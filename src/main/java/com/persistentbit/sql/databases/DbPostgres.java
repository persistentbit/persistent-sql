package com.persistentbit.sql.databases;

import com.persistentbit.core.collections.PByteList;

/**
 * A DbType for a PostgreSQL database.
 *
 * @author Peter Muys
 * @see DbType
 * @since 19/07/2016
 */
public class DbPostgres extends AbstractDbType{

	/**
	 * The default tcp/ip connection port
	 */
	public static final int defaultPort = 5432;

	public DbPostgres() {
		super("PostgreSQL");
	}

	/**
	 * Create a jdbc connection url
	 *
	 * @param db     The database name
	 * @param schema The OPTIONAL schema name
	 *
	 * @return The connection url using the default port on localhost
	 */
	public static String connectionUrlLocal(String db, String schema) {
		return connectionUrl("localhost", db, schema);
	}


	/**
	 * Create a jdbc connection Url
	 *
	 * @param host   The host name or ip address
	 * @param db     The database name
	 * @param schema The OPTIONAL schema name
	 *
	 * @return The jdbc connection url using the default port
	 */
	public static String connectionUrl(String host, String db, String schema) {
		return connectionUrl(host, defaultPort, db, schema);
	}

	/**
	 * Create a jdbc connection Url
	 *
	 * @param host   The host name or ip address
	 * @param port   The connection port
	 * @param db     The database name
	 * @param schema The OPTIONAL schema name
	 *
	 * @return The jdbc connection url
	 */
	public static String connectionUrl(String host, int port, String db, String schema) {
		if(schema != null) {
			return "jdbc:postgresql://" + host + ":" + port + "/" + db + "?currentSchema=" + schema;
		}
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

	@Override
	public void registerDriver() {
		registerDriver(getDriverClassName());
	}

	@Override
	public String asLiteralBlob(PByteList byteList) {
		return "E'\\x" + byteList.toHexString() + "'";
	}
}
