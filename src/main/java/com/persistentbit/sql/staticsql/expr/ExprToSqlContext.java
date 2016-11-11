package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.databases.DbType;

import java.sql.PreparedStatement;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Context used by {@link Expr} instances to generate Sql code
 *
 * @author Peter Muys
 * @since 14/10/16
 */
public class ExprToSqlContext{

	private final DbType  dbType;
	private final String  schema;
	private final boolean useSqlParams;
	private int                                                 nextUniqueId       = 1;
	private PMap<Expr, String>                                  instanceNameLookup = PMap.empty();
	private PList<Consumer<Tuple2<PreparedStatement, Integer>>> paramSetters       = PList.empty();

	public ExprToSqlContext(DbType dbType, String schema, boolean useSqlParams) {
		this.dbType = dbType;
		this.schema = schema;
		this.useSqlParams = useSqlParams;
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

	public void setParam(Consumer<Tuple2<PreparedStatement, Integer>> re) {
		if(useSqlParams) {
			paramSetters = paramSetters.plus(re);
		}
	}

	public boolean isUsingSqlParameters() {
		return useSqlParams;
	}

	public PList<Consumer<Tuple2<PreparedStatement, Integer>>> getParamSetters() {
		return paramSetters;
	}
}
