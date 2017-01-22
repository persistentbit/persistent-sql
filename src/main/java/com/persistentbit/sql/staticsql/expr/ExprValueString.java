package com.persistentbit.sql.staticsql.expr;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Represents an {@link Expr} that holds a String value
 * @author Peter Muys
 * @since 28/09/16
 */
public class ExprValueString implements ETypeString, ETypeValue<String>{

	private String value;

	public ExprValueString(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "\"" + value + "\"";
	}

	public String getValue() {
		return value;
	}

	@Override
	public ETypeValue setValue(String value) {
		this.value = value;
		return this;
	}

	@Override
	public void _setParam(PreparedStatement preparedStatement, int parameterIndex) throws SQLException {
		preparedStatement.setString(parameterIndex, value);
	}

	@Override
	public String _literalValueToSql(ExprToSqlContext context) {
		return value == null ? "NULL" : context.getDbType().asLiteralString(value);
	}
}
