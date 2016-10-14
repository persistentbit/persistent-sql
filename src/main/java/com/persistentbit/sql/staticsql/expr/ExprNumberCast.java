package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;
import com.persistentbit.sql.staticsql.expr.ETypeNumber;

/**
 * Created by petermuys on 28/09/16.
 */
public class ExprNumberCast<F extends Number,T extends Number> implements ETypeNumber<T>{
    private ETypeNumber<F> from;
    private Class<T> clsTo;

    public ExprNumberCast(ETypeNumber<F> from, Class<T> clsTo) {
        this.from = from;
        this.clsTo = clsTo;
    }

    @Override
    public String toString() {
        return "((" + clsTo.getSimpleName() + ")" + from + ")";
    }
    @Override
    public <R1> R1 accept(ExprVisitor<R1> visitor) {
        return visitor.visit(this);
    }

    public ETypeNumber<F> getFrom() {
        return from;
    }

    public Class<? extends Number> getClsTo() {
        return clsTo;
    }

    @Override
    public T read(RowReader _rowReader, ExprRowReaderCache _cache) {
        return _rowReader.readNext(clsTo);
    }
}
