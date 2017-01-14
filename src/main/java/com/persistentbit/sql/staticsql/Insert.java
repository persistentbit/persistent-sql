package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PStream;
import com.persistentbit.core.result.Result;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.sqlwork.DbTransManager;
import com.persistentbit.sql.staticsql.expr.ETypeObject;
import com.persistentbit.sql.staticsql.expr.Expr;

import java.sql.PreparedStatement;
import java.util.function.Consumer;

/**
 * Represents an Sql Insert statement
 * @see InsertSqlBuilder
 * @author Peter Muys
 * @since 2/10/16*
 */
public class Insert implements DbWork<Integer>{

	private final ETypeObject into;
	private final PList<Expr> valueList;

	public Insert(ETypeObject into, PList<Expr> values) {
		this.into = into;
		this.valueList = values;
	}

	public static Insert into(ETypeObject into, Expr... values) {
		return new Insert(into, PStream.from(values).plist());
	}


	public ETypeObject getInto() {
		return into;
	}

	public PList<Expr> getValues() {
		return valueList;
	}

	public <T> InsertWithGeneratedKeys<T> withGeneratedKeys(Expr<T> generatedKeys) {
		if(valueList.size() > 1) {
			throw new RuntimeException("Can't get generated keys when inserting more than 1 row");
		}
		return new InsertWithGeneratedKeys<>(this, generatedKeys);
	}

	@Override
	public Result<Integer> execute(DbContext dbc, DbTransManager tm) throws Exception {
		return Result.function(dbc, tm).code(log -> {
			InsertSqlBuilder b = new InsertSqlBuilder(dbc, this);

			log.info(b.generateNoParams());

			Tuple2<String, Consumer<PreparedStatement>> generatedQuery = b.generate();
			try(PreparedStatement s = tm.get().prepareStatement(generatedQuery._1)) {
				generatedQuery._2.accept(s);
				return Result.success(s.executeUpdate());
			}
		});
	}



}
