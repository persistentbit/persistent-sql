package com.persistentbit.sql.staticsql.expr;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class ExprPropertyBoolean extends ExprProperty<Boolean> implements ETypeBoolean{

	public ExprPropertyBoolean(ETypeObject parent, String propertyName, String columnName) {
		super(Boolean.class, parent, propertyName, columnName);
	}
}
