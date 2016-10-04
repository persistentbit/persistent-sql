package com.persistentbit.sql.staticsql.expr;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class ExprPropertyNumber<N extends Number> extends ExprProperty<N> implements ETypeNumber<N> {
    public ExprPropertyNumber(Class<N> valueClass,Expr parent, String propertyName) {
        super(valueClass,parent,propertyName);
    }


}