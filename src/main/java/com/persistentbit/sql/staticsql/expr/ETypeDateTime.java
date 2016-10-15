package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;
import com.persistentbit.sql.staticsql.expr.mixins.MixinEq;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by petermuys on 4/10/16.
 */
public interface ETypeDateTime  extends Expr<LocalDateTime>, MixinEq<ETypeDateTime> {

    default ETypeBoolean eq(LocalDateTime dateTime){
        return eq(Sql.val(dateTime));
    }
    default ETypeBoolean notEq(LocalDateTime dateTime){
        return notEq(Sql.val(dateTime));
    }

    @Override
    default LocalDateTime read(RowReader _rowReader, ExprRowReaderCache _cache) {
        return _rowReader.readNext(LocalDateTime.class);
    }

    //***************************  BETWEEN
    default ETypeBoolean between(Expr<LocalDateTime> left, Expr<LocalDateTime> right){
        return new ExprBetween<>(this,left,right);
    }
    default ETypeBoolean between(Expr<LocalDateTime> left, LocalDateTime right){
        return between(left,Sql.val(right));
    }
    default ETypeBoolean between(LocalDateTime left,Expr<LocalDateTime> right){
        return between(Sql.val(left),right);
    }
    default ETypeBoolean between(LocalDateTime left, LocalDateTime right){
        return between(Sql.val(left),Sql.val(right));
    }
}
