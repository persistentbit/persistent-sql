package com.persistentbit.sql.databases;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A DbType for an Apache Derby database.
 * @author Peter Muys
 * @since 19/07/2016
 * @see DbType
 */
public class DbDerby extends AbstractDbType{

	public DbDerby() {
		super("Apache Derby");
	}

	public static String urlInMemory(String name) {
		return "jdbc:derby:memory:" + name + ";create=true";
	}

	public static String url(String filePath) {
		return "jdbc:derby:" + filePath.replace('\\', '/') + ";create=true";
	}

	public static String getDriverClassName() {
		return "org.apache.derby.jdbc.EmbeddedDriver";
	}

	@Override
	public String sqlWithLimitAndOffset(long limit, long offset, String sql) {
		return sqlWithLimit(limit, sql + " OFFSET " + offset + " ROWS ");
	}

	@Override
	public String sqlWithLimit(long limit, String sql) {
		return sql + " FETCH NEXT " + limit + " ROWS ONLY ";
	}

	@Override
	public String asLiteralDate(LocalDate date) {
		return "DATE('" + DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date) + "')";
	}

	@Override
	public String asLiteralDateTime(LocalDateTime dateTime) {
		return "TIMESTAMP('" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(dateTime) + "')";
	}

	@Override
	public String setCurrentSchemaStatement(String schema) {
		return "SET CURRENT SCHEMA " + schema;
	}

	@Override
	public void registerDriver() {
		registerDriver(getDriverClassName());
	}


}
