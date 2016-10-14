package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;
import com.persistentbit.sql.staticsql.expr.ETypeNumber;

/**
 * Created by petermuys on 28/09/16.
 */
public class ExprConstNumber<N extends Number> implements ETypeNumber<N> {
    private final N value;
    private final Class<? extends Number> valueClass;

    public ExprConstNumber(Class<? extends Number> valueClass,N value) {
        this.valueClass = valueClass;
        this.value = value;
    }

    @Override
    public String toString() {
        return ""+value;
    }
    @Override
    public <R1> R1 accept(ExprVisitor<R1> visitor) {
        return visitor.visit(this);
    }

    public N getValue() {
        return value;
    }

    @Override
    public N read(RowReader _rowReader, ExprRowReaderCache _cache) {
        return (N)_rowReader.readNext(valueClass);
    }
}
