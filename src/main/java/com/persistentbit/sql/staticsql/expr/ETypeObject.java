package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.core.utils.StringUtils;

import java.util.function.Function;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public interface ETypeObject<T> extends Expr<T>{


    PList<Tuple2<String,Expr>> _all();
    default <R> EMapper<T,R>  map(Function<T,R> mapper){
        return new EMapper<T, R>(this,mapper);
    }

    @Override
    default <R1> R1 accept(ExprVisitor<R1> visitor) {
        return visitor.visit(this);
    }

    Expr getParent();

    default String getTableName(){
        return StringUtils.dropLast(getClass().getSimpleName(),1);
    }
    default String getInstanceName() {
        return getTableName();
    }

    default EValTable<T> val(T value){
        return new EValTable<>(this,value);
    }
}
