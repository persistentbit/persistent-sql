package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.logging.Log;
import com.persistentbit.core.result.Result;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.sqlwork.SqlWork;
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
class InsertSqlBuilder{

	private final DbContext dbContext;
	private final Insert    insert;
	private final Expr      generatedKeys;

	public InsertSqlBuilder(DbContext dbContext, Insert insert) {
		this(dbContext, insert, null);
	}

	public InsertSqlBuilder(DbContext dbContext, Insert insert, Expr generatedKeys) {
		this.dbContext = dbContext;
		this.insert = insert;
		this.generatedKeys = generatedKeys;
	}

	public Tuple2<String, Consumer<PreparedStatement>> generate() {
		ExprToSqlContext context = new ExprToSqlContext(dbContext, true);
		return Tuple2.of(generate(context), prepStat ->
			context.getParamSetters().zipWithIndex().forEach(t -> t._2.accept(Tuple2.of(prepStat, t._1 + 1)))
		);
	}

	public SqlWork<Integer> work() {
		return tm -> Result.function().code(l -> {
			l.info("Insert query");
			l.info(generateNoParams());
			Tuple2<String, Consumer<PreparedStatement>> generatedQuery = generate();
			try(PreparedStatement stat = tm.get().prepareStatement(generatedQuery._1)) {
				generatedQuery._2.accept(stat);
				return Result.success(stat.executeUpdate());
			}
		});
	}

	public String generateNoParams() {
		return generate(new ExprToSqlContext(dbContext, false));
	}

	private String generate(ExprToSqlContext context) {
		return Log.function(context).code(l -> {
			String fullTableName = insert.getInto().getFullTableName(dbContext.getSchemaName().orElse(null));
			context.uniqueInstanceName(insert.getInto(), fullTableName);
			String nl        = "\r\n";
			String res       = "";
			String tableName = insert.getInto()._getTableName();
			res += "INSERT INTO " + fullTableName + " ";
			@SuppressWarnings("unchecked")
			PList<Tuple2<String, Expr>> all = insert.getInto()._all();
			PList<Expr> expanded = all.map(e -> e._2._expand()).<Expr>flatten().plist();
			@SuppressWarnings("unchecked")
			PList<Expr> expandedGenerated = generatedKeys._expand();
			PList<Expr> expandedNotGenerated = expanded.filter(e -> expandedGenerated.contains(e) == false);

			PList<String> names = expandedNotGenerated.map(e -> e._fullColumnName(context));
			res += "(" + names.toString(", ") + ")" + nl;
			res += "VALUES \r\n";
			res += insert.getValues().map(v -> {
				@SuppressWarnings("unchecked")
				PList<Expr> vals = v._expand();
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
		});
	}
}
