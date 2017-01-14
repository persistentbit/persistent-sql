package com.persistentbit.sql.staticsql;

import com.persistentbit.core.result.Result;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.sqlwork.DbTransManager;
import com.persistentbit.sql.staticsql.expr.ETypeBoolean;
import com.persistentbit.sql.staticsql.expr.ETypeObject;

import java.sql.PreparedStatement;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Represents an SQL delete statement
 *
 * @author Peter Muys
 * @since 13/10/2016
 */
public class Delete implements DbWork<Integer>{

	private final ETypeObject  table;
	private final ETypeBoolean where;

	private Delete(ETypeObject table, ETypeBoolean where) {
		this.table = Objects.requireNonNull(table);
		this.where = where;
	}

	public Delete(ETypeObject table) {
		this(table, null);
	}


	public Delete where(ETypeBoolean whereExpr) {
		return new Delete(table, whereExpr);
	}

	@Override
	public Result<Integer> execute(DbContext dbc, DbTransManager tm) throws Exception {
		return Result.function(dbc, tm).code(log -> {
			DeleteSqlBuilder b = new DeleteSqlBuilder(dbc, this);
			log.info(b.generateNoParams());

			Tuple2<String, Consumer<PreparedStatement>> generatedQuery = b.generate();
			try(PreparedStatement s = tm.get().prepareStatement(generatedQuery._1)) {
				generatedQuery._2.accept(s);
				return Result.success(s.executeUpdate());
			}
		});
	}


	public ETypeObject getTable() {
		return table;
	}

	public ETypeBoolean getWhere() {
		return where;
	}


}
