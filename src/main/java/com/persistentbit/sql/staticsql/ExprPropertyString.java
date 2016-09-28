package com.persistentbit.sql.staticsql;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class ExprPropertyString extends ExprProperty<String> implements ETypeString{
    public ExprPropertyString(Expr parent,String propertyName) {
        super(parent,propertyName);
    }
}
