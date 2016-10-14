package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.Query;
import com.persistentbit.sql.staticsql.RowReader;

import java.util.Optional;

/**
 * Created by petermuys on 14/10/16.
 */
public interface ETypeSelection<T> extends ETypeObject<T>{
    @Override
    default Optional<Expr<?>> getParent() {
        return null;
    }

    @Override
    default String _getTableName() {
        return "selection";
    }

    @Override
    default Optional<Expr> _getAutoGenKey() {
        return null;
    }

    @Override
    default T _setAutoGenKey(T object, Object value) {
        return null;
    }

    Query getQuery();
    Expr<T> getSelection();


}
