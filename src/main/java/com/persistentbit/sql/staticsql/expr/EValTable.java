package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;

/**
 * Created by petermuys on 2/10/16.
 */
public class EValTable<T> implements Expr<T>{
    private final ETypeObject<T>   table;
    private final T value;

    public EValTable(ETypeObject<T> table, T value) {
        this.table = table;
        this.value = value;
    }



    public ETypeObject<T> getTable() {
        return table;
    }

    public T getValue() {
        return value;
    }

    @Override
    public T read(RowReader _rowReader, ExprRowReaderCache _cache) {
        return table.read(_rowReader,_cache);
    }

    @Override
    public String _toSql(ExprToSqlContext context) {
        return _expand().map(e -> _toSql(context)).toString(", ");
    }

    @Override
    public PList<Expr> _expand() {
        return table._asExprValues(value);
    }
}
