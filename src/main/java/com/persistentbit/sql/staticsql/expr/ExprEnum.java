package com.persistentbit.sql.staticsql.expr;

/**
 * Created by petermuys on 5/10/16.
 */
public class ExprEnum<T extends Enum<T>> implements ETypeEnum<T>{

	private T        value;
	private Class<T> enumClass;

	public ExprEnum(T value, Class<T> enumClass) {
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
	public String _toSql(ExprToSqlContext context) {
		return context.getDbType().asLiteralString(value.name());
	}


}
