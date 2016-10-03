package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.staticsql.expr.Expr;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class ExprProperty<T> implements Expr<T> {
    private Expr    parent;
    private String  propertyName;
    private Class<T>  valueClass;

    public ExprProperty(Class<T> valueClass,Expr parent, String propertyName) {
        this.parent = parent;
        this.propertyName = propertyName;
        this.valueClass = valueClass;
    }

    @Override
    public String toString() {
        return parent.toString() + "." + propertyName;
    }
    @Override
    public <R1> R1 accept(ExprVisitor<R1> visitor) {
        return visitor.visit(this);
    }

    public Expr getParent() {
        return parent;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Class<T> getValueClass() {
        return valueClass;
    }
}
