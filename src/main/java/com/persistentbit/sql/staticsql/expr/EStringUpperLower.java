package com.persistentbit.sql.staticsql.expr;

/**
 * User: petermuys
 * Date: 15/10/16
 * Time: 15:19
 */
public class EStringUpperLower implements ETypeString{

	private boolean upper = false;
	private Expr<String> expr;

	public EStringUpperLower(boolean upper, Expr<String> expr) {
		this.upper = upper;
		this.expr = expr;
	}

	@Override
	public String _toSql(ExprToSqlContext context) {
		String val = expr._toSql(context);
		if(upper) {
			return context.getDbType().toUpperCase(val);
		}
		return context.getDbType().toLowerCase(val);
	}

}
