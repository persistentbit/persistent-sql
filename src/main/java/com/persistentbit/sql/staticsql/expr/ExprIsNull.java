package com.persistentbit.sql.staticsql.expr;

/**
 * User: petermuys
 * Date: 15/10/16
 * Time: 13:54
 */
public class ExprIsNull implements ETypeBoolean{

	private Expr    expr;
	private boolean not;

	public ExprIsNull(Expr expr, boolean not) {
		this.expr = expr;
		this.not = not;
	}

	@Override
	public String _toSql(ExprToSqlContext context) {
		return expr._toSql(context) + " IS " + (not ? "NOT " : "") + "NULL";
	}


}
