package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.databases.DbType;
import com.persistentbit.sql.staticsql.expr.BaseSelection;
import com.persistentbit.sql.staticsql.expr.ETypeSelection;
import com.persistentbit.sql.staticsql.expr.Expr;
import com.persistentbit.sql.staticsql.expr.ExprToSqlContext;

import java.sql.PreparedStatement;
import java.util.function.Consumer;

/**
 * Builder for a SQL Select statement<br>
 *
 * @author Peter Muys
 * @since 2/10/16
 */
public class QuerySqlBuilder{

	private final ETypeSelection s;
	private final Query          q;
	private final DbType         type;
	private final String         schema;

	public QuerySqlBuilder(ETypeSelection s, DbType type, String schema) {
		this.s = s;
		this.q = s.getQuery();
		this.type = type;
		this.schema = schema;
	}

	public Tuple2<String, Consumer<PreparedStatement>> generate() {
		ExprToSqlContext context = new ExprToSqlContext(type, schema, true);
		return Tuple2.of(generate(context, false), prepStat ->
			context.getParamSetters().zipWithIndex().forEach(t -> t._2.accept(Tuple2.of(prepStat, t._1 + 1)))
		);
	}

	public String generateNoParams() {
		return generate(new ExprToSqlContext(type, schema, false), false);
	}

	public String generate(ExprToSqlContext context, boolean asSubQuery) {
		String nl      = "\r\n";
		String selName = context.uniqueInstanceName(s, "s");
		@SuppressWarnings("unchecked")
		PList<Expr<?>> exp = s._expand();

		String selItems;
		if(asSubQuery) {
			@SuppressWarnings("unchecked")
			PList<BaseSelection<?>.SelectionProperty<?>> selection = s.selections();
			selItems = selection
				.map(s -> s._getExpr()._toSql(context) + " AS " + s.getPropertyName()).toString(", ");
		}
		else {
			selItems = exp.map(e -> e._toSql(context)).toString(", ");
		}
		String distinct = q.distinct ? "DISTINCT " : "";
		String sql      = "SELECT " + distinct + selItems + nl;
		sql += "FROM " + q.getFrom().getFullTableName(schema) + " AS " + context
			.uniqueInstanceName(q.getFrom(), q.getFrom().getFullTableName(schema)) + " ";
		sql += q.getJoins().map(j -> joinToString(context, j)).toString(nl);
		sql += q.getWhere().map(w -> nl + "WHERE " + w._toSql(context)).orElse("");

		if(q.orderBy.isEmpty() == false) {
			sql += nl + "ORDER BY " + q.orderBy
				.map(ob -> ob.getExpr()._toSql(context) + " " + ob.getDir().name().toUpperCase()).toString(", ");
		}


		if(asSubQuery) {
			sql = "(" + sql + ")";
		}
		return sql;
	}

	private String joinToString(ExprToSqlContext context, Join join) {
		String res = "";
		switch(join.getType()) {
			case full:
				res += "FULL JOIN";
				break;
			case inner:
				res += "INNER JOIN";
				break;
			case left:
				res += "LEFT JOIN";
				break;
			case right:
				res += "RIGHT JOIN";
				break;
			default:
				throw new IllegalArgumentException(join.getType().toString());

		}
		res += " " + join.getTable().getFullTableName(schema) + " " + context
			.uniqueInstanceName(join.getTable(), join.getTable().getFullTableName(schema));
		res += join.getJoinExpr().map(e -> " ON " + e._toSql(context)).get();
		return res;
	}

}
