package com.persistentbit.sql.staticsql.expr;

/**
 * Created by petermuys on 14/10/16.
 */
public class ExprIn<T> implements ETypeBoolean{

	private final Expr<T>      value;
	private final ETypeList<T> in;

	public ExprIn(Expr<T> value, ETypeList<T> in) {
		this.value = value;
		this.in = in;
	}


	@Override
	public String _toSql(ExprToSqlContext context) {
		return value._toSql(context) + " IN " + in._toSql(context);
	}


}
