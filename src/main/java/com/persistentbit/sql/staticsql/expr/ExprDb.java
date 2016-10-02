package com.persistentbit.sql.staticsql.expr;

/**
 * Created by petermuys on 1/10/16.
 */
public interface ExprDb<T extends ExprDb> extends Expr<T> {
    @Override
    default <R1> R1 accept(ExprVisitor<R1> visitor) {
        return visitor.visit(this);
    }
}
