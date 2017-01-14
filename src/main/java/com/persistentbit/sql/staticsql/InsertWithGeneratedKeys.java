package com.persistentbit.sql.staticsql;

import com.persistentbit.core.result.Result;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.staticsql.expr.Expr;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.function.Consumer;

/**
 * Created by petermuys on 3/10/16.
 */
public class InsertWithGeneratedKeys<T>{

	private final Insert  insert;
	private final Expr<T> generated;

	public InsertWithGeneratedKeys(Insert insert, Expr<T> generated) {
		this.insert = insert;
		this.generated = generated;
	}


	public SSqlWork<T> asWork() {
		return (dbContext, tm) -> Result.function().code(log -> {
			InsertSqlBuilder b = new InsertSqlBuilder(dbContext, insert, generated);

			log.info(b.generateNoParams());

			Tuple2<String, Consumer<PreparedStatement>> generatedQuery = b.generate();
			try(PreparedStatement s = tm.get().prepareStatement(generatedQuery._1, Statement.RETURN_GENERATED_KEYS)) {
				generatedQuery._2.accept(s);

				int           count      = s.executeUpdate();
				ExprRowReader exprReader = new ExprRowReader();


				try(ResultSet generatedKeys = s.getGeneratedKeys()) {
					ResultSetRowReader rowReader = new ResultSetRowReader(generatedKeys);
					if(generatedKeys.next()) {
						return Result.success(exprReader.read(generated, rowReader));
					}
					return Result.failure("There are no generated keys...");
				}
			}
		});
	}

}
