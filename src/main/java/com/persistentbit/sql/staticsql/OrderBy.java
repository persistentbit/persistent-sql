package com.persistentbit.sql.staticsql;

import com.persistentbit.sql.staticsql.expr.Expr;

/**
 * User: petermuys
 * Date: 15/10/16
 * Time: 15:39
 */
public class OrderBy {
    public enum Direction{
        asc,desc
    }
    private final Expr<?> expr;
    private final Direction dir;

    public OrderBy(Expr<?> expr, Direction dir) {
        this.expr = expr;
        this.dir = dir;
    }

    public Expr<?> getExpr() {
        return expr;
    }

    public Direction getDir() {
        return dir;
    }
}
