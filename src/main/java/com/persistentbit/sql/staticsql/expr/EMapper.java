package com.persistentbit.sql.staticsql.expr;

import java.util.function.Function;

/**
 * Created by petermuys on 2/10/16.
 */
public class EMapper<T,R> implements Expr<R>{
    private Expr<T> expr;
    private Function<T,R> mapper;

    public EMapper(Expr<T> expr, Function<T, R> mapper) {
        this.expr = expr;
        this.mapper = mapper;
    }

    @Override
    public <R1> R1 accept(ExprVisitor<R1> visitor) {
        return visitor.visit(this);
    }

    public Expr<T> getExpr() {
        return expr;
    }

    public Function<T, R> getMapper() {
        return mapper;
    }
}
