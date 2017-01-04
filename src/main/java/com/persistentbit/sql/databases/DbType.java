package com.persistentbit.sql.databases;

import com.persistentbit.core.collections.PByteList;

import com.persistentbit.core.logging.Log;
import com.persistentbit.sql.PersistSqlException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * A DbType instance represent a database type like postgres, mysql, h2,...<br>
 * A DbType contains database type specific mappings for working with Sql.<br>
 * @author Peter Muys
 * @since 19/07/2016
 */
public interface DbType{


	/**
	 * Register the jdbc driver class for this database<br>
	 */
	void registerDriver();




	/**
	 * Try to create a DbType instance for a given database name.<br>
	 * Currently supported names:<br>
	 * <ul>
	 * <li>derby</li>
	 * <li>h2</li>
	 * <li>postgres or postgresql</li>
	 * <li>mysql</li>
	 * </ul>
	 * If the name does not match one of the supported names,
	 * then the name is seen as a java class name of a DbType class.
	 *
	 * @param dbTypeOrClassName The short name or the java class name of the DbType instance
	 *
	 * @return An Optional DbType.
	 */
	static Optional<DbType> createFromName(String dbTypeOrClassName) {
		return Log.function(dbTypeOrClassName).code(log -> {
			switch(dbTypeOrClassName.toLowerCase()) {
				case "derby":
					return Optional.of(new DbDerby());
				case "h2":
					return Optional.of(new DbH2());
				case "postgres":
				case "postgresql":
					return Optional.of(new DbPostgres());
				case "mysql":
					return Optional.of(new DbMySql());
				default:
					if(dbTypeOrClassName.contains(".") == false) {
						log.error("Don't know database type name '" + dbTypeOrClassName + "'");
						return Optional.empty();
					}
					//See the name as the class name...
					try {
						Class<?> cls = DbType.class.getClassLoader().loadClass(dbTypeOrClassName);
						return Optional.of((DbType) cls.newInstance());
					} catch(ClassNotFoundException e) {
						log.error("Can't load DbType class  '" + dbTypeOrClassName + "'");
						return Optional.empty();
					} catch(InstantiationException | IllegalAccessException e) {
						log.error("Error constructing DbType class '" + dbTypeOrClassName + "'");
						return Optional.empty();
					}

			}
		});

	}

	String getDatabaseName();

	String sqlWithLimit(long limit, String sql);

	String sqlWithLimitAndOffset(long limit, long offset, String sql);

	default String numberToString(String number, int charCount) {
		return "CAST(" + number + " AS VARCHAR(" + +charCount + ")";
	}

	default String concatStrings(String s1, String s2) {
		return "CONCAT(" + s1 + ", " + s2 + ")";
	}

	default String asLiteralString(String value) {
		if(value == null) {
			return null;
		}
		StringBuilder res = new StringBuilder();
		for(int t = 0; t < value.length(); t++) {
			char c = value.charAt(t);
			if(c == '\'') {
				res.append("\'\'");
			}
			else if(c == '\"') {
				res.append("\"\"");
			}
			else {
				res.append(c);
			}
		}
		return "\'" + res + "\'";
	}

	default String asLiteralDate(LocalDate date) {
		return "DATE '" + DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date) + "'";
	}

	default String asLiteralDateTime(LocalDateTime dateTime) {
		return "TIMESTAMP '" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnnnnnnnnn").format(dateTime) + "'";
	}

	default String asLiteralBlob(PByteList byteList) {
		throw new PersistSqlException("Can't convert a BLOB to a literal for " + getDatabaseName());
	}

	default String toUpperCase(String value) {
		return "UCASE(" + value + ")";
	}

	default String toLowerCase(String value) {
		return "LCASE(" + value + ")";
	}

	/**
	 * Create a Sql statement that set the current Schema for a connection
	 * @param schema The schema name
	 * @return The SQL statement (without ';')
	 */
	String setCurrentSchemaStatement(String schema);

}
