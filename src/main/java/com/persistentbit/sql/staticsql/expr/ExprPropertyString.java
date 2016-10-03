package com.persistentbit.sql.staticsql.expr;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class ExprPropertyString extends ExprProperty<String> implements ETypeString {
    public ExprPropertyString(Expr parent, String propertyName) {
        super(String.class,parent,propertyName);
    }
}
