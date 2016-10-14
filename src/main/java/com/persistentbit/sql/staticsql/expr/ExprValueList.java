package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;

/**
 * Created by petermuys on 14/10/16.
 */
public class ExprValueList<T> implements ETypeList<T>,Expr<PList<T>>{

    private final PList<T> values;

    public ExprValueList(Iterable<T> values) {
        this.values = PList.from(values);
    }


    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visit((ExprValueList) this);
    }

    @Override
    public PList<T> read(RowReader _rowReader, ExprRowReaderCache _cache) {
        throw new PersistSqlException("Don't know how to read a value list");
    }
}
