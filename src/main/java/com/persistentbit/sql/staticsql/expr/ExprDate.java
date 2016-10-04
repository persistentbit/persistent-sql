package com.persistentbit.sql.staticsql.expr;

import java.time.LocalDate;

/**
 * Created by petermuys on 4/10/16.
 */
public class ExprDate implements Expr<LocalDate>,ETypeDate{
    private final LocalDate value;

    public LocalDate getValue() {
        return value;
    }

    public ExprDate(LocalDate value) {

        this.value = value;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
