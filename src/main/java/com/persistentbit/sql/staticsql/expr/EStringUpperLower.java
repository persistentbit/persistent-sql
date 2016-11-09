package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.PersistSqlException;

/**
 * Represents the Sql String function UPPER(str) or Lower(str).
 * User: petermuys
 * Date: 15/10/16
 * Time: 15:19
 */
public class EStringUpperLower implements ETypeString{

	public enum Type{
		toUpperCase, toLowerCase
	}

	private final Type         type;
	private final Expr<String> expr;

	public EStringUpperLower(Type type, Expr<String> expr) {
		this.type = type;
		this.expr = expr;
	}

	@Override
	public String _toSql(ExprToSqlContext context) {
		String val = expr._toSql(context);
		switch(type) {
			case toUpperCase:
				return context.getDbType().toUpperCase(val);
			case toLowerCase:
				context.getDbType().toLowerCase(val);
			default:
				throw new PersistSqlException("Unknown: " + type);

		}
	}

}
