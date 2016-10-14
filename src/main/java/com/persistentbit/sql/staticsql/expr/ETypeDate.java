package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;

import java.time.LocalDate;

/**
 * Created by petermuys on 4/10/16.
 */
public interface ETypeDate extends Expr<LocalDate>,MixinEq<ETypeDate>{
    default ETypeBoolean eq(LocalDate date){
        return eq(new ExprDate(date));
    }
    default ETypeBoolean notEq(LocalDate date){
        return notEq(new ExprDate(date));
    }

    @Override
    default LocalDate read(RowReader _rowReader, ExprRowReaderCache _cache) {
        return _rowReader.readNext(LocalDate.class);
    }
}
