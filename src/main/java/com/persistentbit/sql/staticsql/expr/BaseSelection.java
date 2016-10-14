package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.Query;
import com.persistentbit.sql.staticsql.RowReader;

import java.util.Optional;

/**
 * Created by petermuys on 14/10/16.
 */
public abstract class BaseSelection<T> implements ETypeSelection<T>{
    private final Query query;
    private final Expr<T> selection;

    public BaseSelection(Query query, Expr<T> selection) {
        this.query = query;
        this.selection = selection;
    }

    public Query getQuery() {
        return query;
    }


    public Expr<T> getSelection() {
        return selection;
    }

    public PList<T> getResult() {
        return query.getDbSql().run(this);
    }
    public Optional<T> getOneResult() {
        return getResult().headOpt();
    }


}
