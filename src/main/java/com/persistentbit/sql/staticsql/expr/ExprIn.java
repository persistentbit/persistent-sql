package com.persistentbit.sql.staticsql.expr;

/**
 * Created by petermuys on 14/10/16.
 */
public class ExprIn<T> implements ETypeBoolean{
    private final Expr<T> value;
    private final ETypeList<T> in;

    public ExprIn(Expr<T> value, ETypeList<T> in) {
        this.value = value;
        this.in = in;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
