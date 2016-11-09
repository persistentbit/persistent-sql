package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PByteList;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;

/**
 * Expression around a {@link PByteList} Binary value
 *
 * @author Peter Muys
 * @since 8/11/2016
 */
public class ExprConstBinary implements Expr<PByteList>{
    private final PByteList value;

    public ExprConstBinary(PByteList value) {
        this.value = value;
    }


    public PByteList getValue() {
        return value;
    }

    @Override
    public PByteList read(RowReader _rowReader, ExprRowReaderCache _cache) {
        return _rowReader.readNext(PByteList.class);
    }

    @Override
    public String _toSql(ExprToSqlContext context) {
        return context.getDbType().asLiteralBlob(value);
    }
}
