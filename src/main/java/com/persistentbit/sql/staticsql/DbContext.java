package com.persistentbit.sql.staticsql;

import com.persistentbit.sql.databases.DbType;

import java.util.Optional;

/**
 * TODOC
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
}
