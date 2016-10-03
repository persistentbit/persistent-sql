package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PList;
import com.persistentbit.sql.staticsql.expr.Expr;

import java.util.Optional;

/**
 * Created by petermuys on 1/10/16.
 */
public class Selection<T> {
    private final Query query;
    private final Expr<T> selection;

    public Selection(Query query, Expr<T> selection) {
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
    public Optional<T>  getOneResult() {
        return getResult().headOpt();
    }
}
