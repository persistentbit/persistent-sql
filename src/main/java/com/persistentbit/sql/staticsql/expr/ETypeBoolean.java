package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public interface ETypeBoolean extends Expr<Boolean>{
    default ETypeBoolean and(ETypeBoolean right){
        return new ExprAndOr(this,right, ExprAndOr.LogicType.and);
    }
    default ETypeBoolean or(ETypeBoolean right){
        return new ExprAndOr(this,right, ExprAndOr.LogicType.or);
    }

    @Override
    default Boolean read(RowReader _rowReader, ExprRowReaderCache _cache) {
        return _rowReader.readNext(Boolean.class);
    }
}
