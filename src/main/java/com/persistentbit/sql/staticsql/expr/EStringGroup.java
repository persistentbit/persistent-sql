package com.persistentbit.sql.staticsql.expr;

/**
 * Represents a {@link EGroup} of type String
 * @author Peter Muys
 * @since 5/10/16
 */
public class EStringGroup extends EGroup<String> implements ETypeString{

	public EStringGroup(Expr<String> value) {
		super(value);
	}
}
