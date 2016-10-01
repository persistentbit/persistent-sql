package com.persistentbit.sql.staticsql.expr;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public interface Expr<S>{

    static <N extends Number> ExprConstNumber<N> val(N number){
        return new ExprConstNumber<N>(number);
    }
    static ETypeString  val(String value){
        return new ExprConstString(value);
    }



}
