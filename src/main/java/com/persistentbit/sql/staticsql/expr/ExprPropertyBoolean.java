package com.persistentbit.sql.staticsql.expr;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class ExprPropertyBoolean extends ExprProperty<Boolean> implements ETypeBoolean {
    public ExprPropertyBoolean(Expr parent, String propertyName) {
        super(Boolean.class,parent,propertyName);
    }
}
