package com.persistentbit.sql.staticsql.expr;

import java.time.LocalDateTime;

/**
 * Created by petermuys on 4/10/16.
 */
public class ExprPropertyDateTime extends ExprProperty<LocalDateTime> implements ETypeDateTime{

	public ExprPropertyDateTime(ETypeObject parent, String propertyName, String columnName) {
		super(LocalDateTime.class, parent, propertyName, columnName);
	}
}
