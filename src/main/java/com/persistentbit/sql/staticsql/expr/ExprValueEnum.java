package com.persistentbit.sql.staticsql.expr;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Represents an {@link Expr} that holds a java Enum value
 * @author Peter Muys
 * @since 5/10/16
 */
public class ExprValueEnum<T extends Enum<T>> implements ETypeEnum<T>, ETypeValue<T>{

	private T        value;
	private Class<T> enumClass;

	public ExprValueEnum(T value, Class<T> enumClass) {
		this.value = value;
		this.enumClass = enumClass;
	}


	public T getValue() {
		return value;
	}

	@Override
	public Class<T> _getEnumClass() {
		return enumClass;
	}

	@Override
	public ETypeValue setValue(T value) {
		this.value = value;
		return this;
	}

	@Override
	public void _setParam(PreparedStatement preparedStatement, int parameterIndex) throws SQLException {
		preparedStatement.setString(parameterIndex, value == null ? null : value.name());
	}

	@Override
	public String _literalValueToSql(ExprToSqlContext context) {
		return value == null ? "NULL" : context.getDbType().asLiteralString(value.name());
	}


}
