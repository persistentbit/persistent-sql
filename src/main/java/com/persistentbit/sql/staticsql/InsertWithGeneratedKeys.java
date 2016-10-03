package com.persistentbit.sql.staticsql;

import com.persistentbit.sql.staticsql.expr.Expr;

/**
 * Created by petermuys on 3/10/16.
 */
public class InsertWithGeneratedKeys<T> {
    private final Insert insert;
    private final Expr<T>  generated;

    public InsertWithGeneratedKeys(Insert insert, Expr<T> generated) {
        this.insert = insert;
        this.generated = generated;
    }

    public Insert getInsert() {
        return insert;
    }

    public Expr<T> getGenerated() {
        return generated;
    }
}
