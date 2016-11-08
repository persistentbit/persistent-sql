package com.persistentbit.sql.staticsql.expr;

import java.time.LocalDate;

/**
 * @since 4/10/16
 * @author Peter Muys
 */
public class ExprPropertyDate extends ExprProperty<LocalDate> implements ETypeDate{

	public ExprPropertyDate(ETypeObject parent, String propertyName, String columnName) {
		super(LocalDate.class, parent, propertyName, columnName);
	}
}
