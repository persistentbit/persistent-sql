package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.databases.DbType;
import com.persistentbit.sql.staticsql.expr.Expr;
import com.persistentbit.sql.staticsql.expr.ExprToSqlContext;

import java.sql.PreparedStatement;
import java.util.function.Consumer;

/**
 * Builder for SQL insert statements.<br>
 *
 * @see Insert
 * @author Peter Muys
 * @since 2/10/16
 */
public class InsertSqlBuilder{

	private final DbType dbType;
	private final String schema;
	private final Insert insert;
	private final Expr   generatedKeys;

	public InsertSqlBuilder(DbType dbType, String schema, Insert insert) {
		this(dbType, schema, insert, null);
	}

	public InsertSqlBuilder(DbType dbType, String schema, Insert insert, Expr generatedKeys) {
		this.dbType = dbType;
		this.schema = schema;
		this.insert = insert;
		this.generatedKeys = generatedKeys;
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
		context.uniqueInstanceName(insert.getInto(), insert.getInto().getFullTableName(schema));
		String nl        = "\r\n";
		String res       = "";
		String tableName = insert.getInto()._getTableName();
		res += "INSERT INTO " + insert.getInto().getFullTableName(schema) + " ";
		@SuppressWarnings("unchecked")
		PList<Tuple2<String, Expr>> all                  = insert.getInto()._all();
		PList<Expr>                 expanded             = all.map(e -> e._2._expand()).<Expr>flatten().plist();
		@SuppressWarnings("unchecked")
		PList<Expr>                 expandedGenerated    = generatedKeys._expand();
		PList<Expr>                 expandedNotGenerated = expanded.filter(e -> expandedGenerated.contains(e) == false);

		PList<String> names = expandedNotGenerated.map(e -> e._fullColumnName(context));
		res += "(" + names.toString(", ") + ")" + nl;
		res += "VALUES \r\n";
		res += insert.getValues().map(v -> {
			@SuppressWarnings("unchecked")
			PList<Expr> vals      = v._expand();
			PList<Expr> valsNoGen = PList.empty();
			for(int t = 0; t < expanded.size(); t++) {
				boolean generated = expandedGenerated.contains(expanded.get(t));
				if(generated == false) {
					valsNoGen = valsNoGen.plus(vals.get(t));
				}
			}
			return valsNoGen.map(vn -> vn._toSql(context)).toString("(", ", ", ")");
		}).toString(",\r\n");


		return res;
	}
}
