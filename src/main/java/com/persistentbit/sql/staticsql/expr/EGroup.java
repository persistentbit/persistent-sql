package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;

/**
 * Created by petermuys on 5/10/16.
 */
public class EGroup<T> implements Expr<T> {
    private Expr<T> value;

    public EGroup(Expr<T> value) {
        this.value = value;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public Expr<T> getValue() {
        return value;
    }

    @Override
    public T read(RowReader _rowReader, ExprRowReaderCache _cache) {
        return value.read(_rowReader,_cache);
    }
}
