package com.persistentbit.sql.staticsql.expr;

import java.time.LocalDateTime;

/**
 * Created by petermuys on 4/10/16.
 */
public class ExprDateTime implements Expr<LocalDateTime>,ETypeDateTime {
    private final LocalDateTime   value;

    public ExprDateTime(LocalDateTime value) {
        this.value = value;
    }

    public LocalDateTime getValue() {
        return value;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
