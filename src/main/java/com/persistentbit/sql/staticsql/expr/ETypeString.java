package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public interface ETypeString  extends Expr<String>,MixinEq<ETypeString>,MixinComparable<ETypeString> {

    default ETypeBoolean    eq(String other){
        return eq(Expr.val(other));
    }
    default ETypeBoolean notEq(String other){
        return notEq(Expr.val(other));
    }

    default ETypeBoolean    like(ETypeString other) { return new ExprStringLike(this,other);}
    default ETypeBoolean    like(String other) { return this.like(Expr.val(other));}


    default ETypeString add(ETypeString expr){
        return new ExprStringAdd(this,expr);
    }

    default ETypeString add(String value){
        return add(Expr.val(value));
    }

    @Override
    default String read(RowReader _rowReader, ExprRowReaderCache _cache) {
        return _rowReader.readNext(String.class);
    }

}
