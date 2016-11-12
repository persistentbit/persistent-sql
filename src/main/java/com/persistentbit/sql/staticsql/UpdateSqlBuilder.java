package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.databases.DbType;
import com.persistentbit.sql.staticsql.expr.ETypeObject;
import com.persistentbit.sql.staticsql.expr.Expr;
import com.persistentbit.sql.staticsql.expr.ExprToSqlContext;

import java.sql.PreparedStatement;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Sql Builder for update statements.
 *
 * @author Peter Muys
 * @since 8/10/16
 */
public class UpdateSqlBuilder{

	private final DbType                       dbType;
	private final String                       schema;
	private final Update                       update;
	private final PMap<ETypeObject, TableInst> tables;

	public UpdateSqlBuilder(DbType dbType, String schema, Update update) {
		this.dbType = dbType;
		this.schema = schema;
		this.update = update;
		PMap<ETypeObject, TableInst> allUsed = PMap.empty();
		allUsed.put(update.getTable(), new TableInst(update.getTable().getFullTableName(schema), update.getTable()));
		tables = allUsed;
	}

	private Optional<String> getTableInstance(ETypeObject obj) {
		return tables.getOpt(obj).map(TableInst::getName);
	}

	public Tuple2<String, Consumer<PreparedStatement>> generate() {
		ExprToSqlContext context = new ExprToSqlContext(dbType, schema, true);
		return Tuple2.of(generate(context), prepStat ->
			context.getParamSetters().zipWithIndex().forEach(t -> t._2.accept(Tuple2.of(prepStat, t._1 + 1)))
		);
	}

	public String generateNoParams() {
		return generate(new ExprToSqlContext(dbType, schema, false));
	}

	private String generate(ExprToSqlContext context) {
		String nl = "\r\n";
		String res = "UPDATE " + update.getTable().getFullTableName(schema) + " AS " + context
			.uniqueInstanceName(update.getTable(), update.getTable()._getTableName()) + nl;
		res += " SET ";
		PList<Tuple2<Expr<?>, Expr<?>>> sets = update.getSet();
		res += sets.map(t -> {
			PList<Expr<?>> expanded       = t._1._expand();
			PList<String>  properties     = expanded.map(e -> e._fullColumnName(context));
			PList<Expr<?>> expandedValues = t._2._expand();
			PList<String> values = expandedValues
				.map(e -> e._toSql(context));
			return properties.zip(values).map(ns -> ns._2 + "=" + ns._1).toString(", ");
		}).toString(", ");
		if(update.getWhere() != null) {
			res += nl + " WHERE " + update.getWhere()._toSql(context);
		}
		return res;
	}


}
