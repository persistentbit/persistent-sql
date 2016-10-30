package com.persistentbit.sql.staticsql.expr;

/**
 * User: petermuys
 * Date: 15/10/16
 * Time: 15:03
 */
public class ExprBetween<T> implements ETypeBoolean{

	private final Expr<T> value;
	private final Expr<T> min;
	private final Expr<T> max;

	public ExprBetween(Expr<T> value, Expr<T> min, Expr<T> max) {
		this.value = value;
		this.min = min;
		this.max = max;
	}

	@Override
	public String _toSql(ExprToSqlContext context) {
		return value._toSql(context) + " BETWEEN " + min._toSql(context) + " AND " + max._toSql(context);
	}


}
