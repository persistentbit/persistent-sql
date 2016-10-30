package com.persistentbit.sql.staticsql.expr;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class ExprBoolean implements ETypeBoolean{

	private final Boolean value;

	public ExprBoolean(Boolean value) {
		this.value = value;
	}


	public Boolean getValue() {
		return value;
	}

	@Override
	public String _toSql(ExprToSqlContext context) {
		return value.toString().toUpperCase();
	}

}
