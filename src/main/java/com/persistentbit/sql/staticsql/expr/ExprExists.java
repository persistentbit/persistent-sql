package com.persistentbit.sql.staticsql.expr;

/**
 * User: petermuys
 * Date: 15/10/16
 * Time: 11:37
 */
public class ExprExists implements ETypeBoolean{

	private ETypeList<?> existsIn;

	public ExprExists(ETypeList<?> existsIn) {
		this.existsIn = existsIn;
	}

	@Override
	public String _toSql(ExprToSqlContext context) {
		return "EXISTS " + existsIn._toSql(context);
	}


}
