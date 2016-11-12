package com.persistentbit.sql.staticsql.expr;

/**
 * A {@link EGroup} for Boolean values
 * @author Peter Muys
 * @since 5/10/16
 */
public class EBooleanGroup extends EGroup<Boolean> implements ETypeBoolean{

	public EBooleanGroup(Expr<Boolean> value) {
		super(value);
	}


}
