package com.persistentbit.sql.staticsql;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class ExprProperty<T> implements Expr<T>{
    private Expr    parent;
    private String  propertyName;

    public ExprProperty(Expr parent, String propertyName) {
        this.parent = parent;
        this.propertyName = propertyName;
    }

    @Override
    public String toString() {
        return parent.toString() + "." + propertyName;
    }
}
