package com.persistentbit.sql.staticsql.expr;

/**
 * Created by petermuys on 28/09/16.
 */
public class ExprNumberToString implements ETypeString{

	private ETypeNumber number;

	public ExprNumberToString(ETypeNumber number) {
		this.number = number;
	}

	@Override
	public String toString() {
		return "((String)" + number + ")";
	}


	public ETypeNumber getNumber() {
		return number;
	}

	@Override
	public String _toSql(ExprToSqlContext context) {
		return context.getDbType().numberToString(number._toSql(context), 20);
	}


}
