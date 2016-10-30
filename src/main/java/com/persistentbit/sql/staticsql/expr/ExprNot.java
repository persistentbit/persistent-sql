package com.persistentbit.sql.staticsql.expr;

/**
 * User: petermuys
 * Date: 15/10/16
 * Time: 13:54
 */
public class ExprNot implements ETypeBoolean{

	private ETypeBoolean notExpr;

	public ExprNot(ETypeBoolean notExpr) {
		this.notExpr = notExpr;
	}

	@Override
	public String _toSql(ExprToSqlContext context) {
		return "NOT " + notExpr._toSql(context);
	}


}
