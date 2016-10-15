package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.Query;
import com.persistentbit.sql.staticsql.RowReader;

import java.util.Optional;

/**
 * Created by petermuys on 14/10/16.
 */
public interface ETypeSelection<T> extends ETypeObject<T>,ETypeList<T>{
    @Override
    default Optional<ETypePropertyParent> getParent() {
        return Optional.empty();
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

    PList<BaseSelection<?>.SelectionProperty<?>> selections();

}
