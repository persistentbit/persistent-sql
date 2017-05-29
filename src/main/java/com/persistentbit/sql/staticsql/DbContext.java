package com.persistentbit.sql.staticsql;

import com.persistentbit.sql.databases.DbType;

import java.util.Optional;

/**
 * The context for database access.<br>
 * A Context is linked to a {@link DbType} and a database Schema Name.<br>
 *
 * @author petermuys
 * @since 13/01/17
 */
public interface DbContext{

	DbType getDbType();

	Optional<String> getSchemaName();

	default String getFullTableName(String tableName) {
		return getSchemaName().map(schema -> schema + "." + tableName).orElse(tableName);
	}


	static DbContext of(DbType type) {
		return of(type, null);
	}

	static DbContext of(DbType type, String schemaName) {
		return new DbContext(){
			@Override
			public DbType getDbType() {
				return type;
			}

			@Override
			public Optional<String> getSchemaName() {
				return Optional.ofNullable(schemaName);
			}

			@Override
			public String toString() {
				return "DbContext[" + type + getSchemaName().map(s -> ", " + s).orElse("") + "]";
			}
		};
	}
}
