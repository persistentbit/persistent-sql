package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;

/**
 * Created by petermuys on 14/10/16.
 */
public class ExprValueList<T> implements ETypeList<T>,Expr<T>{

    private final PList<Expr<T>> values;

    public ExprValueList(Iterable<Expr<T>> values) {
        this.values = PList.from(values);
    }



    @Override
    public T read(RowReader _rowReader, ExprRowReaderCache _cache) {
        throw new PersistSqlException("Don't know how to read a value list");
    }

    @Override
    public String _toSql(ExprToSqlContext context) {
        return "(" + values.map(v -> v._toSql(context)).toString(", ") + ")";
    }

    @Override
    public PList<Expr> _expand() {
        return PList.val(this);
    }
}
