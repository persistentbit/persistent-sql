package com.persistentbit.sql.staticsql.expr;

import java.time.LocalDateTime;

/**
 * Created by petermuys on 4/10/16.
 */
public class ExprDateTime implements Expr<LocalDateTime>, ETypeDateTime{

	private final LocalDateTime value;

	public ExprDateTime(LocalDateTime value) {
		this.value = value;
	}

	public LocalDateTime getValue() {
		return value;
	}


	@Override
	public String _toSql(ExprToSqlContext context) {
		return context.getDbType().asLiteralDateTime(value);
	}


}
