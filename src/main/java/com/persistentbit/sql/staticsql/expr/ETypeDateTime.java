package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;

import java.time.LocalDateTime;

/**
 * Created by petermuys on 4/10/16.
 */
public interface ETypeDateTime  extends Expr<LocalDateTime>, MixinEq<ETypeDateTime>{

    default ETypeBoolean eq(LocalDateTime dateTime){
        return eq(Expr.val(dateTime));
    }
    default ETypeBoolean notEq(LocalDateTime dateTime){
        return notEq(Expr.val(dateTime));
    }

    @Override
    default LocalDateTime read(RowReader _rowReader, ExprRowReaderCache _cache) {
        return _rowReader.readNext(LocalDateTime.class);
    }
}
