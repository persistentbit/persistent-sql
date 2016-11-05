package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PMap;
import com.persistentbit.sql.databases.DbType;

import java.util.Optional;

/**
 * Context used by {@link Expr} instances to generate Sql code
 * @since 14/10/16
 * @author Peter Muys
 */
public class ExprToSqlContext{

	private final DbType dbType;
	private final String schema;
	private int                nextUniqueId       = 1;
	private PMap<Expr, String> instanceNameLookup = PMap.empty();

	public ExprToSqlContext(DbType dbType, String schema) {
		this.dbType = dbType;
		this.schema = schema;
	}

	public String uniqueInstanceName(Expr expr, String defaultName) {
		String res = instanceNameLookup.getOrDefault(expr, null);
		if(res == null) {
			res = defaultName + "_" + nextUniqueId;
			nextUniqueId++;
			instanceNameLookup = instanceNameLookup.put(expr, res);
		}
		return res;
	}

	public Optional<String> getSchema() {
		return Optional.ofNullable(schema);
	}

	public DbType getDbType() {
		return dbType;
	}
}
