package com.persistentbit.sql.staticsql.expr;

/**
 * Created by petermuys on 5/10/16.
 */
public class ExprPropertyEnum<T extends Enum<T>> extends ExprProperty<T> implements ETypeEnum<T>{

	public ExprPropertyEnum(Class<T> valueClass, ETypeObject parent, String propertyName, String columnName) {
		super(valueClass, parent, propertyName, columnName);
	}

	@Override
	public Class<T> _getEnumClass() {
		return getValueClass();
	}
}
