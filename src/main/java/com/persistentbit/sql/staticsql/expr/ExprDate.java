package com.persistentbit.sql.staticsql.expr;

import java.time.LocalDate;

/**
 * @author Peter Muys
 * @since 4/10/16
 */
public class ExprDate implements Expr<LocalDate>, ETypeDate{

	private final LocalDate value;

	public ExprDate(LocalDate value) {

		this.value = value;
	}

	public LocalDate getValue() {
		return value;
	}

	@Override
	public String _toSql(ExprToSqlContext context) {
		return context.getDbType().asLiteralDate(value);
	}


}
