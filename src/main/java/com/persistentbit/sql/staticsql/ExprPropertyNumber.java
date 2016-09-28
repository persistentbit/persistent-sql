package com.persistentbit.sql.staticsql;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class ExprPropertyNumber<N extends Number> extends ExprProperty<N> implements ETypeNumber<N>{
    public ExprPropertyNumber(Expr parent,String propertyName) {
        super(parent,propertyName);
    }


}
